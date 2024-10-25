package com.google.backend.trading.alarm;

import com.google.backend.common.web.Response;
import com.google.backend.trading.client.feign.AlarmClient;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.util.AlarmLogUtil;
import com.google.backend.trading.util.ArraySideWindow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author trading
 * @date 2021/12/8 16:46
 */
@Slf4j
@Service
public class AlarmComponent {

	@Value("${spring.profiles.active}")
	private String active;

	@Autowired
	private AlarmClient alarmClient;

	/**
	 * 时间窗口内的最大阈值，达到阈值进行告警
	 */
	private static final int ALARM_THRESHOLD = 10;

	private final ArraySideWindow swapPriceSideWindow = new ArraySideWindow(10 * 60);

	@Async
	public void asyncAlarm(AlarmEnum alarmEnum, String msg) {
		String message = alarmEnum.getMsg() + " cause = " + msg;
		alarm(alarmEnum, message);
	}

	@Async
	public void asyncAlarm(AlarmEnum alarmEnum, Throwable t) {
		String message = alarmEnum.getMsg() + " cause = " + ExceptionUtils.getRootCauseMessage(t) + "\n" + ExceptionUtils.getStackTrace(t);
		log.error("alarm message = {}", message);
		alarm(alarmEnum, message);
	}

	private void alarm(AlarmEnum alarmEnum, String message) {
		if (!StringUtils.equalsAny(active, "uat", "prod")) {
			return;
		}
		String code;
		if (StringUtils.equals(active, "prod")) {
			code = AlarmEnum.PROD_CODE;
		} else {
			code = AlarmEnum.UAT_CODE;
		}
		if (alarmEnum == AlarmEnum.SWAP_PRICE_ERROR) {
			swapPriceSideWindow.count();
			int triggerNum = swapPriceSideWindow.get();
			if (triggerNum < ALARM_THRESHOLD) {
				log.warn("swap price error, ArraySideWindow current num = {}, time window = {} s", triggerNum,
						swapPriceSideWindow.getSecond());
				return;
			}
		}

		AlarmClient.AlarmReq req = new AlarmClient.AlarmReq();
		req.setName(alarmEnum.getName() + String.format(" [%s]", active));
		req.setMsg(message);
		req.setCode(code);
		Response<Object> res = alarmClient.alarm(req);
		if (BusinessExceptionEnum.SUCCESS.getCode() != res.getCode()) {
			AlarmLogUtil.alarm("alarm component fail, req = {}, res = {}", req, res);
		}
	}
}
