package com.google.backend.trading.client.feign;

import com.google.backend.trading.config.feign.FeignConfig;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.kline.dto.PriceChange;
import com.google.backend.trading.model.kline.dto.PriceChange24h;
import com.google.backend.trading.model.kline.dto.PriceHistory;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * TODO 配置fallback，如果失败返回dummy数据
 *
 * @author trading
 * @date 2021/10/18 18:06
 */
@FeignClient(name = "kineInfoClient", url = "${kline.host}", configuration = FeignConfig.class)
@RequestMapping("/internal/api/v1")
public interface KlineInfoClient {

	@GetMapping("/pricechange24h")
	@ApiOperation(value = "查询币对实时价格和24h前的价格")
	Response<List<PriceChange24h>> priceChange24h(@RequestParam("symbols") String symbolArrWithComma);

	@GetMapping("/pricechange")
	@ApiOperation(value = "查询币对实时价格和历史价格（大于一天前的）")
	Response<List<PriceChange>> priceChange(@RequestParam("symbol") String symbol, @RequestParam("days") String dayArrWithComma);

	@GetMapping("/history")
	@ApiOperation(value = "查询币对kline")
	Response<List<PriceHistory>> history(@RequestParam("symbol") String symbol, @RequestParam("from") Long from,
										 @RequestParam("to") Long to, @RequestParam("interval") String interval);

}
