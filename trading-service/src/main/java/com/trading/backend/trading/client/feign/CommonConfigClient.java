package com.google.backend.trading.client.feign;

import com.google.backend.trading.client.fallback.CommonConfigClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.commonconfig.dto.AllConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author trading
 * @date 2021/10/9 11:19
 */
@FeignClient(name = "commonConfigClient", url = "${common-config.host}", configuration = FeignConfig.class, fallbackFactory =
		CommonConfigClientFallbackFactory.class)
@RequestMapping("/internal/v1/config")
public interface CommonConfigClient {

	@GetMapping("/coin/config-info")
	@ApiOperation(value = "根据业务类型获取配置，目前交易模块只需要 1, 2, 6", notes = " 1 common 2 symbol 3 wallet 4 earn 5 dual 6 swap 7 loan 8 redpack")
	Response<AllConfig> configInfoByType(@RequestParam("type") Integer type);


	@GetMapping("/coin/all-config")
	@ApiOperation("获取所有币种配置")
	Response<AllConfig> allConfigInfo();
}
