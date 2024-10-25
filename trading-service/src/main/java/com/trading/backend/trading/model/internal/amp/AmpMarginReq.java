package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageTimeRangeExtReq;
import com.google.backend.trading.model.trade.AmpOrderStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author adam.wang
 * @date 2021/11/18 17:13
 */
@Data
@RequestUnderlineToCamel
@ApiModel(value = "APM杠杆查询对象AmpMarginReq")
public class AmpMarginReq extends PageTimeRangeExtReq {

    /**
     * 用户id
     */
    @NotBlank
    @ApiModelProperty(name = "uid", value = "用户编码", required = true, example = "616289a2d4b1a6d195d6f286")
    private String uid;

    @ApiModelProperty(name = "activities_id", value = "订单ID", example = "1bffc19c-8e23-44ff-8504-de28ef48d50c")
    private String activitiesId;

    @ApiModelProperty(value = "订单类型:LIMIT,MARKET,STOP_LIMIT,STOP_MARKET", allowableValues = "LIMIT,MARKET,STOP_LIMIT,STOP_MARKET",
            example = "MARKET")
    private String type;

    @ApiModelProperty(value = "订单策略:FOK,IOC,GTC", allowableValues = "FOK,IOC,GTC", example = "FOK")
    private String strategy;

    @ApiModelProperty(value = "币对", example = "BTC_USD")
    private String symbol;

    @ApiModelProperty(value = "方向", example = "BUY")
    private String direction;

    @ApiModelProperty(value = "仓位状态 PRE_TRIGGER(待触发) PENDING(等待处理) EXECUTING(挂单中) EXCEPTION(执行异常) COMPLETED(完全成交) CANCELED(完全取消) " +
            "PART_CANCELED(部分成交取消)", example = "COMPLETED")
    private String status;

    @ApiModelProperty(name = "position_id",value = "仓位ID")
    private String positionId;

    @JsonIgnore
    private List<String> statusList;

    public List<String> getStatusList() {
        if (StringUtils.isNotBlank(this.getStatus())){
            return AmpOrderStatus.getByCode(this.getStatus()).stream().map(AmpOrderStatus::getName).collect(Collectors.toList());
        }
        return null;
    }
}
