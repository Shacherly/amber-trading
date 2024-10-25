package com.google.backend.trading.model.pdt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.collect.ImmutableSet;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Set;

/**
 * 去PDT下单的请求数据
 *
 * @author savion.chen
 * @date 2021/10/4 15:37
 */
@Data
@ApiModel(value = "现货下单返回的数据")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CreateTradeRes {

    public static CreateTradeRes FAIL = new CreateTradeRes() {
        @Override
        public boolean isSuccess() {
            return false;
        }
    };

    public static CreateTradeRes HTTP_FAIL = new CreateTradeRes() {
        @Override
        public boolean httpSuccess() {
            return false;
        }
        @Override
        public boolean isSuccess() {
            return false;
        }
    };

    /**
     * PENDING状态 同参数幂等下次继续发送
     */
    private static final Set<String> ALL_STATUS = ImmutableSet.of("PENDING", "ERROR", "MARKET_MOVE_AWAY","PARTIAL_FILLED", "FILLED");

    private static final Set<String> SUCCESS = ImmutableSet.of("PARTIAL_FILLED", "FILLED");

    private String status;
    private BigDecimal filled;
    private BigDecimal quoteFilled;
    private BigDecimal filledPrice;

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS.contains(status);
    }

    @JsonIgnore
    public boolean isPending() {
        return StringUtils.equals("PENDING", status);
    }

    @JsonIgnore
    public boolean httpSuccess() {
        return true;
    }
}

