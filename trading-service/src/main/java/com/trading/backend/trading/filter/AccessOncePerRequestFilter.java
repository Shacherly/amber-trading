package com.google.backend.trading.filter;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author trading
 */
@Slf4j
public class AccessOncePerRequestFilter extends OncePerRequestFilter {

	public static final int MAX_PAYLOAD_LENGTH = 64000;

	public static final int MAX_COST_TIME_MS = 5000;

	private String getContentAsString(byte[] buf) {
		if (buf == null || buf.length == 0) {
			return "";
		}
		if (buf.length > MAX_PAYLOAD_LENGTH) {
			return "Content length is too long";
		}
		int length = buf.length;
		return new String(buf, 0, length);

	}

	/**
	 * Log each request and respponse with full Request URI, content payload and duration of the request in ms.
	 *
	 * @param request     the request
	 * @param response    the response
	 * @param filterChain chain of filters
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String traceId = request.getHeader(Constants.X_GW_REQUEST_ID_HEADER);
		if (null == traceId) {
			traceId = UUID.randomUUID().toString();
		}
		MDC.put(Constants.TRACE_SPAN_ID, traceId);
		long startTime = System.currentTimeMillis();
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
		String requestBody = "";
		if (null != request.getContentType() && request.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
			RequestWrapper wrappedRequest = new RequestWrapper(request);
			requestBody = wrappedRequest.getBody();
			request = wrappedRequest;
		}
		String userInfoJson = request.getHeader(Constants.X_GW_USER_HEADER);
		log.info("access start -> uri = {}, method = {}, query = {}, parameter = {}, body = {}, user header = {}",
				request.getRequestURI(), request.getMethod(), request.getQueryString(), request.getParameterMap(), requestBody, userInfoJson);
		filterChain.doFilter(request, wrappedResponse);
		long duration = System.currentTimeMillis() - startTime;
		String responseBody = "";
		if (null == response.getContentType() || response.getContentType().contains(MediaType.APPLICATION_JSON_VALUE)) {
			responseBody = getContentAsString(wrappedResponse.getContentAsByteArray());
		}
		if (duration < MAX_COST_TIME_MS) {
			log.info("access end <- body = {}, uri = {}, cost = {} ms", responseBody, request.getRequestURI(), duration);
		} else {
			AlarmLogUtil.alarm("access end <- body = {}, uri = {}, cost = {} ms, duration too long,  more than {} ms", request.getRequestURI(),
					responseBody, duration,
					MAX_COST_TIME_MS);
		}
		MDC.remove(Constants.TRACE_SPAN_ID);
		wrappedResponse.copyBodyToResponse();
	}

}