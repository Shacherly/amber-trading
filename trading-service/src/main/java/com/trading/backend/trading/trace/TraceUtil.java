package com.google.backend.trading.trace;

import com.google.backend.trading.constant.Constants;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * @author trading
 * @date 2021/11/2 21:14
 */
public class TraceUtil {

	public static void startTrace() {
		String traceId = UUID.randomUUID().toString();
		MDC.put(Constants.TRACE_SPAN_ID, traceId);
	}

	public static void endTrace() {
		MDC.remove(Constants.TRACE_SPAN_ID);
	}
}
