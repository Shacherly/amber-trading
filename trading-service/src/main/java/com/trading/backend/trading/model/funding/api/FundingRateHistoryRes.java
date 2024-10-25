package com.google.backend.trading.model.funding.api;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/9/28 17:29
 */
@ApiModel(value = "历史费率返回实体")
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class FundingRateHistoryRes implements java.io.Serializable {

    @ExcelProperty("Time")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "日期", example = "2021-09-26")
    private Date time;

    @ExcelProperty("Currency")
    @ApiModelProperty(value = "币种",example = "USD")
    private String coin;

    @ExcelProperty("Lending rate")
    @NumberFormat("#0.0000%")
    @ApiModelProperty(value = "返回利率", example = "0.001")
    private BigDecimal lend;

    @ExcelProperty("Borrowing rate")
    @NumberFormat("#0.0000%")
    @ApiModelProperty(value = "借出利率", example = "0.001")
    private BigDecimal borrow;
}
