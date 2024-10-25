package com.google.backend.trading.task;

import com.google.backend.trading.client.feign.CommonConfigClient;
import com.google.backend.trading.component.ApplicationInitRunner;
import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradePositionLimitUserMapper;
import com.google.backend.trading.dao.model.TradePositionLimitUser;
import com.google.backend.trading.dao.model.TradePositionLimitUserExample;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.AllConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSwapConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.trace.annotation.TraceId;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author trading
 * @date 2021/10/11 11:58
 */
@Slf4j
@Component
public class CacheRefreshTask {

	@Autowired
	private CommonConfigClient configClient;

	@Autowired
	private ApplicationInitRunner applicationInitRunner;

	@Autowired
	private DefaultTradePositionLimitUserMapper limitUserMapper;

	public volatile static Map<String, BigDecimal> POSITION_LIMIT_USER_MAP = new HashMap<>();


	@TraceId
	@Scheduled(fixedRate = Constants.REFRESH_SYMBOL_COIN_CONFIG_INTERVAL_MILL, initialDelay = Constants.REFRESH_SYMBOL_COIN_CONFIG_INTERVAL_MILL)
	public void refreshSymbolConfig() {
		log.info("refresh symbol and coin config");
		Response<AllConfig> res = configClient.allConfigInfo();
		if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode()) {
			log.error("refresh symbol config err, res data = {}", res);
			return;
		}
		Map<String, CoinSymbolConfig> symbolConfigMap = res.getData().getSymbol();
		try {
			//已存在的symbol进行update
			SymbolDomain.CACHE.values().forEach((symbolDomain -> {
				String symbol = symbolDomain.getSymbol();
				CoinSymbolConfig coinSymbolConfig = symbolConfigMap.get(symbol);
				if (null != coinSymbolConfig) {
					symbolDomain.updateCoinSymbolConfig(coinSymbolConfig);
				}
			}));
			//新增的symbol进行初始化
			Collection<String> needInit = CollectionUtils.subtract(symbolConfigMap.keySet(), SymbolDomain.CACHE.keySet());
			if (CollectionUtils.isNotEmpty(needInit)) {
				log.info("need init symbol, data = {}", needInit);
				Map<String, CoinSymbolConfig> needInitSymbolConfigMap = symbolConfigMap.entrySet().stream().filter(entry -> needInit.contains(entry.getKey()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				applicationInitRunner.initSymbol(needInitSymbolConfigMap);
			}
		} catch (Exception e) {
			log.error("refresh symbol config err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}


		Map<String, CoinCommonConfig> commonConfigMap = res.getData().getCommon();
		Map<String, CoinSwapConfig> swapConfigMap = res.getData().getSwap();
		try {
			//新增的common先进行初始化
			Collection<String> commonNeedInit = CollectionUtils.subtract(commonConfigMap.keySet(), CoinDomain.CACHE.keySet());
			if (CollectionUtils.isNotEmpty(commonNeedInit)) {
				log.info("need init coin, data = {}", commonNeedInit);
				Map<String, CoinCommonConfig> needInitSymbolConfigMap = commonConfigMap.entrySet().stream().filter(entry -> commonNeedInit.contains(entry.getKey()))
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
				HashSet<String> tradeCoin =
						symbolConfigMap.keySet().stream().flatMap(s -> Stream.of(CommonUtils.coinPair(s).getFirst(),
								CommonUtils.coinPair(s).getSecond())).collect(Collectors.toCollection(HashSet::new));
				for (String coin : needInitSymbolConfigMap.keySet()) {
					if (tradeCoin.contains(coin)) {
						CoinDomain.initCache(coin);
					}
				}
			}

			CoinDomain.CACHE.values().forEach(coinDomain -> {
				CoinCommonConfig commonConfig = commonConfigMap.get(coinDomain.getName());
				CoinSwapConfig swapConfig = swapConfigMap.get(coinDomain.getName());
				if (null == swapConfig) {
					swapConfig = CoinSwapConfig.NOT_SUPPORT;
				}
				if (null != commonConfig) {
					coinDomain.updateCoinConfig(commonConfig, swapConfig);
				}
			});
		} catch (Exception e) {
			log.error("refresh coin config err, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
		}

	}

	@TraceId
	@Scheduled(fixedRate = Constants.REFRESH_POSITION_LIMIT_USER_INTERVAL_MILL, initialDelay = 0L)
	public void refreshPositionLimitUser() {
		log.info("refresh position limit user");
		List<TradePositionLimitUser> limitUsers = limitUserMapper.selectByExample(new TradePositionLimitUserExample());
		POSITION_LIMIT_USER_MAP = limitUsers.stream().collect(Collectors.toMap(TradePositionLimitUser::getUid,
				TradePositionLimitUser::getPositionLimitAmount));
	}

}
