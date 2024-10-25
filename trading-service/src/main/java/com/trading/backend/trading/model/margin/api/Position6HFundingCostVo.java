package com.google.backend.trading.model.margin.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author david.chen
 * @date 2021/11/12 18:11
 *
 * 6H资金费率统计
 */
@Data
@ApiModel(value = "6H资金费率统计vo")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Position6HFundingCostVo {

    @ApiModelProperty(value = "前6小时 资金费率 fundingCostCount[0] ->前一个小时, fundingCostCount[1]-> 前两个小时,\n 例如：现在是 11：57 返回的第一个数据 fundingCostCount[0] 是(10,11] 左开右闭之间的资金费率总和 \n fundingCostCount[1] 是(9,10] \n 以此类推...",example = "[-1,-2,-3,-1,10,-10]")
    private List<BigDecimal> fundingCostCount;

}
