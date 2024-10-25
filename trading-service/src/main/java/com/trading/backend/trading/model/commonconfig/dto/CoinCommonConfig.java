package com.google.backend.trading.model.commonconfig.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(value = "通用配置")
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CoinCommonConfig {
    @ApiModelProperty(value = "币种名称")
    private String name;
    @ApiModelProperty(value = " app端币种图标下载链接")
    private String appIcon;
    @ApiModelProperty(value = "web端币种图标下载链接")
    private String webIcon;
    @ApiModelProperty(value = "排序 优先级越小越优先")
    private int priority;
    @ApiModelProperty(value = "钱包精度，保底设置")
    private int precision;
    @ApiModelProperty(value = "是否生效(全局开关，false时其他所有业务都不可用)")
    private boolean isValid;
    @ApiModelProperty(value = "是否是USDs")
    private boolean isUsds;
    @ApiModelProperty(value = "Base数量精度，影响交易、理财等的数量的最小精度")
    private int baseIssueQuantity;
    @ApiModelProperty(value = "Base流动性指数")
    private BigDecimal baseLiquidityIndex;
    @ApiModelProperty(value = "Quote流动性指数")
    private BigDecimal quoteLiquidityIndex;
    @ApiModelProperty(value = "用于建仓与风险检查的基础抵扣率")
    private BigDecimal hairCutAvailable;
    @ApiModelProperty(value = "用于清算的基础抵扣率")
    private BigDecimal hairCutLiquidation;
    @ApiModelProperty(value = "行情区域分类 可多选，值为major/hot/defi/new")
    private String marketCategory;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "业务类型  lite,pro 枚举值 lite,pro 用, 分割")
    private String businessClient;
}
