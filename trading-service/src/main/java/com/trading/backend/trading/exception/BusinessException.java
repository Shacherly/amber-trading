package com.google.backend.trading.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * 业务异常
 *
 * @author trading
 * @date 2021/3/17 20:16
 */
@Getter
@ToString(callSuper = true)
public class BusinessException extends RuntimeException {

    private final BusinessExceptionEnum exceptionEnum;

    public BusinessException(BusinessExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMsg());
        this.exceptionEnum = exceptionEnum;
    }


}
