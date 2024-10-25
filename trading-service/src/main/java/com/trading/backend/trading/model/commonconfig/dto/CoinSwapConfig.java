package com.google.backend.trading.model.commonconfig.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author: linhuayao
 * @Date: 2021/10/4 10:58
 */
@ToString
@Getter
@Setter
@ApiModel(value = "兑换配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoinSwapConfig {

    public static final CoinSwapConfig NOT_SUPPORT = new CoinSwapConfig(){
        @Override
        public boolean isSupport() {
            return false;
        }
    };

    @ApiModelProperty(value = "币种")
    private String name;
    @ApiModelProperty(value = "单笔最小数量")
    private BigDecimal minOrderAmount;
    @ApiModelProperty(value = "单笔最大数量")
    private BigDecimal maxOrderAmount;
    @ApiModelProperty(value = "开关")
    private Boolean isValid;
    @ApiModelProperty(value = "排序 优先级越小越优先")
    private Integer priority;

    @JsonIgnore
    public boolean isSupport() {
        return !Boolean.FALSE.equals(isValid);
    }
}
