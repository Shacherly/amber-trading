package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/10/5 11:36
 */
@Data
@ApiModel(value = "swap下单返回")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateSwapRes {
    private String status;
    private BigDecimal filled;
    private BigDecimal toFilled;
    private BigDecimal filledPrice;


    public static CreateSwapRes FAIL = new CreateSwapRes() {
        @Override
        public boolean isSuccess() {
            return false;
        }
    };

    public static CreateSwapRes HTTP_FAIL = new CreateSwapRes() {
        @Override
        public boolean httpSuccess() {
            return false;
        }
        @Override
        public boolean isSuccess() {
            return false;
        }
    };

    private static final String SUCCESS = "FILLED";

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }

    @JsonIgnore
    public boolean httpSuccess(){return true;}
}
