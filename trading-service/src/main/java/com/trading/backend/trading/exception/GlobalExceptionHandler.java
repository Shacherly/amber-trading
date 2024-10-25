package com.google.backend.trading.exception;

import com.google.backend.trading.alarm.AlarmComponent;
import com.google.backend.trading.model.common.Response;
import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ValidationException;
import java.util.StringJoiner;


/**
 * 统一异常处理
 *
 * @author trading
 * @date 2021/3/17 19:45
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    public static final String INVALID_PARAMETER = "invalid parameter";

    private final AlarmComponent alarmComponent;

    public GlobalExceptionHandler(AlarmComponent alarmComponent) {
        this.alarmComponent = alarmComponent;
    }

    /**
     * 处理业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusinessException(BusinessException ex) {
        BusinessExceptionEnum exceptionEnum = ex.getExceptionEnum();
        if (exceptionEnum == BusinessExceptionEnum.UNEXPECTED_ERROR) {
            log.error("business unexpected error, cause = {}", ExceptionUtils.getRootCauseMessage(ex));
            Sentry.captureException(ex, SentryHint.BUSINESS_UNEXPECTED_ERROR);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("BusinessException trace {}", ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return Response.fail(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }


    /**
     * 参数绑定错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException ex) {
        StringJoiner sj = new StringJoiner(";");
        ex.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getField() + "#" + x.getDefaultMessage()));
        log.error("bind err, target = {}, msg = {}", ex.getTarget(), sj);
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    /**
     * 参数校验错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    public Response<Void> handleValidationException(ValidationException ex) {
        log.error("valid err, msg = {}", ExceptionUtils.getRootCauseMessage(ex));
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    /**
     * 字段校验不通过异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        StringJoiner sj = new StringJoiner(";");
        ex.getBindingResult().getFieldErrors().forEach(x -> sj.add(x.getField() + x.getDefaultMessage()));
        log.error("method valid err, parameter = {}, msg = {}", ex.getParameter(), sj);
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("handleIllegalArgumentException, cause = {}", ExceptionUtils.getRootCauseMessage(ex));
        return Response.fail(INVALID_PARAMETER);
    }

    /**
     * Controller参数绑定错误
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.error("method valid err, msg = {}", ex.getMessage());
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    /**
     * 处理方法不支持异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public Response<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("method not support err, msg = {}", ex.getMessage());
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public Response<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("notReadable err, msg = {}", ex.getMessage());
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    @ExceptionHandler(value = HttpMediaTypeNotAcceptableException.class)
    public Response<Void> handleHttpMediaTypeNotAcceptableException(HttpMediaTypeNotAcceptableException ex) {
        log.error("HttpMediaTypeNotAcceptable err, msg = {}", ex.getMessage());
        Sentry.captureException(ex, SentryHint.INVALID_PARAMETER);
        return Response.fail(INVALID_PARAMETER);
    }

    /**
     * 内部接口的异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = InternalApiException.class)
    public Response<Void> handleException(InternalApiException ex) {
        log.error("internal api err, msg = {}", ex.getMessage());
        Sentry.captureException(ex, SentryHint.INTERNAL_API_ERROR);
        return Response.fail(ex.getMessage());
    }

    @ExceptionHandler(value = NeedLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleException(NeedLoginException ex) {
        log.warn("NeedLoginException err, msg = {}", ex.getMessage());
    }
    /**
     * 其他未知异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Response<Void> handleException(Exception ex) {
        log.error("unexpected error, msg = {}", ex.getMessage(), ex);
        Sentry.captureException(ex, SentryHint.UNEXPECTED_ERROR);
        return Response.fail();
    }

}
