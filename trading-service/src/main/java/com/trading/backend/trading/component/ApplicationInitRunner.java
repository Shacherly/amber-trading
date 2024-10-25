package com.google.backend.trading.component;

import com.google.backend.trading.client.feign.CommonConfigClient;
import com.google.backend.trading.client.feign.PdtClient;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.AllConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSwapConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.model.pdt.PriceReq;
import com.google.backend.trading.model.pdt.PriceRes;
import com.google.backend.trading.trace.TraceUtil;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author trading
 * @date 2021/10/9 13:11
 */
@Slf4j
@Component
public class ApplicationInitRunner implements ApplicationRunner {

	@Autowired
	private CommonConfigClient configClient;

	@Autowired
	private PdtClient pdtClient;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		TraceUtil.startTrace();
		Response<AllConfig> res = configClient.allConfigInfo();
		if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode() || null == res.getData()) {
			AlarmLogUtil.alarm("application symbol and coin config initialize fail, skip");
			return;
		}

		//获取币对配置并初始化
		Map<String, CoinSymbolConfig> symbolMap = res.getData().getSymbol();
		initSymbol(symbolMap);

		//获取币种配置并初始化，common 和 swap 都是币种维度，common的币种范畴是包含swap的
		Map<String, CoinCommonConfig> commonConfigMap = Optional.ofNullable(res.getData().getCommon()).orElse(Collections.emptyMap());
		Map<String, CoinSwapConfig> swapConfigMap = Optional.ofNullable(res.getData().getSwap()).orElse(Collections.emptyMap());
		initCoin(commonConfigMap, swapConfigMap, symbolMap.keySet());
		TraceUtil.endTrace();
	}

	public void initCoin(Map<String, CoinCommonConfig> commonConfigMap, Map<String, CoinSwapConfig> swapConfigMap, Set<String> symbolSet) {
		HashSet<String> tradeCoin = symbolSet.stream().map(s -> CommonUtils.coinPair(s).getFirst()).collect(Collectors.toCollection(HashSet::new));
		tradeCoin.addAll(Constants.MARKET_QUOTE_SET);
		//初始化cache
		for (String coin : commonConfigMap.keySet()) {
			if (tradeCoin.contains(coin)) {
				CoinDomain.initCache(coin);
			}
		}

		CoinDomain.CACHE.values().forEach(coinDomain -> {
			String name = coinDomain.getName();
			CoinCommonConfig commonConfig = commonConfigMap.get(name);
			CoinSwapConfig swapConfig = swapConfigMap.get(name);
			if (null == swapConfig) {
				swapConfig = CoinSwapConfig.NOT_SUPPORT;
			}
			if (null != commonConfig) {
				coinDomain.updateCoinConfig(commonConfig, swapConfig);
			}
		});
	}

	public void initSymbol(Map<String, CoinSymbolConfig> symbolMap) {
		//初始化cache
		for (String symbol : symbolMap.keySet()) {
			SymbolDomain.initCache(symbol);
		}

		//获取买一卖一价格
		List<String> symbolList = new ArrayList<>(symbolMap.keySet());
		PriceReq req = new PriceReq();
		req.setSymbols(symbolList);
		Response<Map<String, PriceRes>> priceRes = pdtClient.price(req);
		if (BusinessExceptionEnum.SUCCESS.getCode() != priceRes.getCode()) {
			throw new RuntimeException("pdt price err");
		}

		//获取指数价格
		Response<Map<String, BigDecimal>> indexPriceRes = pdtClient.indexPrice(req);
		if (BusinessExceptionEnum.SUCCESS.getCode() != indexPriceRes.getCode()) {
			throw new RuntimeException("pdt index price err");
		}

		Map<String, PriceRes> priceResMap = priceRes.getData();
		Map<String, BigDecimal> indexPriceResMap = indexPriceRes.getData();

		//初始化cache的价格（不包括分档价格）
		for (String symbol : symbolMap.keySet()) {
			SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
			PriceRes value = priceResMap.get(symbol);
			BigDecimal indexPrice = indexPriceResMap.get(symbol);
			CoinSymbolConfig config = symbolMap.get(symbol);
			try {
				symbolDomain.init(value.getAsk(), value.getBid(), indexPrice, config);
				log.debug("loadPrice |  symbolName:{}, buyPrice:{}, sellPrice:{}, indexPrice:{}, symbolConfig = {}",
						symbol, value.getAsk(), value.getBid(), indexPrice, config);
			} catch (Exception e) {
				SymbolDomain remove = SymbolDomain.removeCacheBySymbol(symbol);
				log.error("SymbolDomain init fail, remove symbol = {}, cause = {}", remove, ExceptionUtils.getRootCauseMessage(e), e);
			}
		}
	}
}
