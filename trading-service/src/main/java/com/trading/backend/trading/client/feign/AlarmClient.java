package com.google.backend.trading.client.feign;

import com.google.backend.common.web.Response;
import com.google.backend.trading.client.fallback.AlarmClientFallbackFactory;
import com.google.backend.trading.config.feign.FeignConfig;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author trading
 * @date 2021/12/8 16:04
 */
@FeignClient(name = "alarmClient", url = "${alarm.host}", configuration = FeignConfig.class, fallbackFactory = AlarmClientFallbackFactory.class)
public interface AlarmClient {

	/**
	 * <a href="https://wiki.googleainsider.com/showdoc/web/#/14/435">
	 * AlarmWiki</a>
	 * @param req
	 * @return
	 */
	@PostMapping("/alarm_manager?method=on_alarm")
	@ApiOperation(value = "获取用户的资产池数据接口")
	Response<Object> alarm(@RequestBody AlarmReq req);


	@Data
	class AlarmReq {

		private String code;

		private String name;

		private String msg;

	}
}
