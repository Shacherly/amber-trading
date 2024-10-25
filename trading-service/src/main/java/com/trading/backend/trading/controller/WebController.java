package com.google.backend.trading.controller;

import com.google.backend.trading.framework.web.RequestUnderlineToCamel;
import com.google.backend.trading.model.common.PageReq;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.model.web.OrderHistoryReq;
import com.google.backend.trading.model.web.OrderHistoryRes;
import com.google.backend.trading.model.web.OrderInfoRes;
import com.google.backend.trading.model.web.TransactionInfoRes;
import com.google.backend.trading.model.web.TransactionReq;
import com.google.backend.trading.service.WebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 适配web端，现货和杠杆融合接口
 *
 * @author jiayi.zhang
 * @date 2021/9/29
 */
@Api(value = "web端现货和杠杆融合接口", tags = "web端现货和杠杆融合接口")
@RestController
@RequestMapping("/v1/web")
@Validated
public class WebController {

    @Resource
    private WebService webService;

    @ApiOperation(value = "活跃订单")
    @GetMapping("/order/active")
    public Response<PageResult<OrderInfoRes>> orderActive(@Valid @RequestUnderlineToCamel PageReq req, UserInfo userInfo) {
        return Response.ok(webService.orderActive(req, userInfo.getUid()));
    }

    @ApiOperation(value = "历史订单列表")
    @GetMapping("/order/history")
    public Response<PageResult<OrderHistoryRes>> orderHistory(@Valid OrderHistoryReq req, UserInfo userInfo) {
        return Response.ok(webService.orderHistory(req, userInfo.getUid()));
    }

    @ApiOperation(value = "交易记录列表")
    @GetMapping("/transaction/history")
    public Response<PageResult<TransactionInfoRes>> transactionHistory(@Valid TransactionReq req, UserInfo userInfo) {
        return Response.ok(webService.transaction(req, userInfo.getUid()));
    }

}
