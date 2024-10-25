package com.google.backend.trading.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author trading
 * @date 2021/11/16 10:28
 */
@Slf4j(topic = "alarm")
public class AlarmLogUtil {

	public static void alarm(String format, Object... arguments) {
		log.error(format, arguments);
	}
}
