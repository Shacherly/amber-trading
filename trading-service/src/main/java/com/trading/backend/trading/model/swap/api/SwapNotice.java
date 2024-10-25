package com.google.backend.trading.model.swap.api;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 推送给swap的消息体
 *
 * @author savion.chen
 * @date 2021/10/23 16:14
 */
@Data
@ApiModel(value = "兑换结果推送")
public class SwapNotice {

    @NotNull
    private String  order;

    @NotNull
    private String  status;

    private String  info;
}
