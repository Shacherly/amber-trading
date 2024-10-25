package com.google.backend.trading.model.common.model.riskcontrol.notice;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author adam.wang
 * @date 2021/11/4 18:42
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionInfo {

        @ApiModelProperty("用户uid")
        @NotBlank(
                message = "uid can not be null"
        )
        private String uid;
        @ApiModelProperty("仓位id")
        @NotNull(
                message = "positionId can not be null"
        )
        private Long positionId;
        @ApiModelProperty("交易对")
        @NotBlank(
                message = "symbol can not be null"
        )
        private String symbol;
        @ApiModelProperty("数量(base)")
        @NotNull(
                message = "size can not be null"
        )
        private BigDecimal size;
        @ApiModelProperty("开仓价格")
        @NotNull(
                message = "price can not be null"
        )
        private BigDecimal price;
        @ApiModelProperty("状态（0,1）")
        @NotNull(
                message = "status can not be null"
        )
        private Integer status;
        @ApiModelProperty("方向 1 BUY, 2 SELL")
        @NotBlank(
                message = "direction can not be null"
        )
        private String direction;

}
