package com.google.backend.trading.model.internal.amp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.backend.trading.model.common.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 仓位信息请求对象
 * @author adam.wang
 * @date 2021/9/28 19:48
 */
@ApiModel(value = "仓位信息请求对象")
@Data
@Valid
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PositionInfoReq  extends PageReq {
    /**
     * 客户uid列表
     */
    @NotNull(message = "uid_list must not be null")
    @ApiModelProperty(name="uid_list",  required = true)
    @Size(min=1,message = "uid_list must not be null")
    private List<String> uidList;

    /**
     * 标的物列表
     */
    @ApiModelProperty(name="symbol_list", value = "币对列表")
    private List<String> symbolList;

    /**
     * 合约类型列表
     */
    @ApiModelProperty(name="type_list", value = "合约类型列表：MARGIN（预留未起作用）")
    private List<String> typeList;
}
