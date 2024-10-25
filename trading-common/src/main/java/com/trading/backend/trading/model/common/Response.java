package com.google.backend.trading.model.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;


/**
 * @author alan
 * @date 2020/3/21 17:35
 */
@Builder
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ApiModel(value = "通用响应体")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Response<T> {
    
    public static final int COMMON_FAIL = 24200000;
    /**
     * 返回码
     */
    @ApiModelProperty(value = "返回码")
    private int code;
    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private T data;

    @ApiModelProperty(value = "消息")
    private String msg = "";

    public static <T> Response<T> ok() {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.msg("ok");
        return builder.build();
    }

    public static <T> Response<T> ok(@NonNull T data) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.data(data).msg("ok");
        return builder.build();
    }

    public static <T> Response<T> ok(@NonNull T data, String msg) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.data(data).msg(msg);
        return builder.build();
    }

    public static <T> Response<T> duplicate(String msg) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.msg(msg);
        return builder.build();
    }

    public static <T> Response<T> fail() {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.code(COMMON_FAIL);
        return builder.build();
    }

    public static <T> Response<T> fail(String msg) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.code(COMMON_FAIL).msg(msg);
        return builder.build();
    }

    public static <T> Response<T> fail(int code) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.code(code);
        return builder.build();
    }

    public static <T> Response<T> fail(int code, String msg) {
        ResponseBuilder<T> builder = new ResponseBuilder<T>();
        builder.code(code).msg(msg);
        return builder.build();
    }
}

