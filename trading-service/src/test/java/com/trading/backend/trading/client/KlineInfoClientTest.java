package com.google.backend.trading.client;

import com.google.backend.asset.common.model.asset.req.PoolReq;
import com.google.backend.asset.common.model.base.PoolEntity;
import com.google.backend.trading.TradingServiceApplicationTest;
import com.google.backend.trading.client.feign.AssetInfoAsyncClient;
import com.google.backend.trading.client.feign.KlineInfoClient;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.kline.dto.PriceChange24h;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author trading
 * @date 2021/10/18 19:07
 */
@Slf4j
public class KlineInfoClientTest extends TradingServiceApplicationTest {

	@Autowired
	private KlineInfoClient klineInfoClient;

	@Autowired
	private AssetInfoAsyncClient assetInfoAsyncClient;

	@Test
	public void testPriceChange24h() {
		String symbolArrWithComma = StringUtils.arrayToDelimitedString(Constants.DEFAULT_MARKET_FAVORITE_LIST, ",");
		Response<List<PriceChange24h>> res = klineInfoClient.priceChange24h(symbolArrWithComma);
		log.info("testPriceChange24h res = {}", res);
	}

	@Test
	public void testAsyncClient() throws ExecutionException {
		PoolReq req = new PoolReq();
		List<CompletableFuture<com.google.backend.common.web.Response<Map<String, PoolEntity>>>> resultList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			req.setUid("616289a2d4b1a6d195d6f28" + i);
			CompletableFuture<com.google.backend.common.web.Response<Map<String, PoolEntity>>> res = assetInfoAsyncClient.getPool(req);
			resultList.add(res);
		}
		CompletableFuture<Void> all = CompletableFuture.allOf(resultList.toArray(new CompletableFuture[0]));
		all.join();
		System.out.println(resultList);
	}
}
