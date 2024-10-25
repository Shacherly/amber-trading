package com.google.backend.trading.model.common.model.riskcontrol;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/9 16:48
 */
@ApiModel(value = "平仓、减仓请求")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReducePositionReq {

    @ApiModelProperty(value = "用户id", required = true, example = "616289a2d4b1a6d195d6f286")
    private String uid;
    @DecimalMin(value = "0", inclusive = false)
    @Max(1)
    @ApiModelProperty(value = "减仓百分比", required = true, example = "0.12")
    private BigDecimal ratio;

    @ApiModelProperty(value = "仓位主要信息列表", notes = "用于和交易模块仓位数据做比对")
    private List<PositionMajorInfo> positions;


    @Data
    public static class PositionMajorInfo {

        /**
         * 仓位id
         */
        @ApiModelProperty(value = "仓位id", example = "21312")
        private Long positionId;

        /**
         * 仓位大小
         */
        @ApiModelProperty(value = "仓位大小", example = "21312")
        private BigDecimal size;

        /**
         * 仓位均价
         */
        @ApiModelProperty(value = "仓位均价", example = "21312")
        private BigDecimal price;
    }
}
