package com.google.backend.trading.filter;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.util.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 神策推送 所需环境header信息 过滤器
 *
 * @author david.chen
 * @date 2022/1/17 19:37
 */
@Slf4j
public class SensorsPushParamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest rawRequest = (HttpServletRequest) servletRequest;
        String originChannel = rawRequest.getHeader(Constants.ORIGIN_CHANNEL);
        String lorp = rawRequest.getHeader(Constants.L_OR_P);
        if (log.isDebugEnabled()) {
            log.debug("from header {}, value = {}", Constants.ORIGIN_CHANNEL, originChannel);
            log.debug("from header {}, value = {}", Constants.L_OR_P, lorp);
        }
        if (StringUtils.isNotEmpty(lorp)) {
            ThreadLocalUtils.L_OR_P.set(lorp);
        }
        if (StringUtils.isNotEmpty(originChannel)) {
            ThreadLocalUtils.ORIGIN_CHANNEL.set(originChannel);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            ThreadLocalUtils.L_OR_P.remove();
            ThreadLocalUtils.ORIGIN_CHANNEL.remove();
        }
    }
}
