package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.internal.amp.AmpMarginReq;
import com.google.backend.trading.model.internal.amp.AmpMarginRes;
import com.google.backend.trading.model.internal.amp.AmpPositionFundReq;
import com.google.backend.trading.model.internal.amp.AmpPositionFundRes;
import com.google.backend.trading.model.internal.amp.AmpPositionReq;
import com.google.backend.trading.model.internal.amp.AmpPositionRes;
import com.google.backend.trading.model.internal.amp.AmpSpotReq;
import com.google.backend.trading.model.internal.amp.AmpSpotRes;
import com.google.backend.trading.model.internal.amp.AmpSwapReq;
import com.google.backend.trading.model.internal.amp.AmpSwapRes;
import com.google.backend.trading.model.internal.amp.AmpTransDetailReq;
import com.google.backend.trading.model.internal.amp.AmpTransDetailRes;
import com.google.backend.trading.model.internal.amp.AmpTransReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailReq;
import com.google.backend.trading.model.internal.amp.PositionFlowDetailRes;
import com.google.backend.trading.model.internal.amp.PositionInfoReq;
import com.google.backend.trading.model.internal.amp.PositionInfoRes;
import com.google.backend.trading.service.FundingCostService;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.SpotService;
import com.google.backend.trading.service.SwapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 提供给AMP的接口支持
 *
 * @author trading
 * @date 2021/9/27 19:32
 */
@Api(tags = "提供给amp的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/amp")
@RestController
public class ManageForAmpController {

    @Resource
    private MarginService marginService;
    @Resource
    private SwapService swapService;
    @Resource
    private FundingCostService fundingCostService;

    @Resource
    private SpotService spotService;

    @ApiOperation(value = "获取客户仓位信息")
    @PostMapping("/position/info")
    public Response<PageResult<PositionInfoRes>> info(@Valid @RequestBody PositionInfoReq req) {
        return Response.ok(marginService.queryPositionInfoForAmp(req));
    }

    @ApiOperation(value = "获取客户交易流水（订单查询）")
    @PostMapping("/position/flowDetail")
    public Response<PageResult<PositionFlowDetailRes>> flowDetail(@Valid @RequestBody PositionFlowDetailReq req) {
        return Response.ok(marginService.flowDetail(req));
    }

    @GetMapping("/history/position")
    @ApiOperation(value = "历史持仓")
    public Response<PageResult<AmpPositionRes>> positionHistory(@Valid AmpPositionReq req) {
        PageResult<AmpPositionRes> res = marginService.positionHistoryForAmp(req);
        return Response.ok(res);
    }

    @GetMapping("/history/margin")
    @ApiOperation(value = "杠杆订单")
    public Response<PageResult<AmpMarginRes>> marginHistory(@Valid AmpMarginReq req) {
        PageResult<AmpMarginRes> res = marginService.marginHistoryForAmp(req);
        return Response.ok(res);
    }

    @GetMapping("/history/spot")
    @ApiOperation(value = "现货订单")
    public Response<PageResult<AmpSpotRes>> spotHistory(@Valid AmpSpotReq req) {
        PageResult<AmpSpotRes> res = spotService.spotHistoryForAmp(req);
        return Response.ok(res);
    }

    @GetMapping("/history/swap")
    @ApiOperation(value = "兑换订单")
    public Response<PageResult<AmpSwapRes>> swapHistory(@Valid AmpSwapReq req) {
        PageResult<AmpSwapRes> pageResult = swapService.swapHistoryForAmp(req);
        return Response.ok(pageResult);
    }

    @GetMapping("/history/fund")
    @ApiOperation(value = "仓位资金订单")
    public Response<PageResult<AmpPositionFundRes>> fund(@Valid AmpPositionFundReq req) {
        PageResult<AmpPositionFundRes> pageResult = fundingCostService.getHistoryFundForAmp(req);
        return Response.ok(pageResult);
    }

    @GetMapping("/transaction/list")
    @ApiOperation(value = "交易id列表")
    public Response<PageResult<String>> transactionList(@Valid AmpTransReq ampTransReq) {
        PageResult<String> orderTransList = marginService.getTransactionListForAmp(ampTransReq);
        return Response.ok(orderTransList);
    }

    @GetMapping("/transaction/detail")
    @ApiOperation(value = "order成交明细")
    public Response<AmpTransDetailRes> getTransactionDetail(@Valid AmpTransDetailReq ampTransDetailReq) {
        AmpTransDetailRes ampTransDetailRes = marginService.getTransDetailForAmp(ampTransDetailReq.getUid(), ampTransDetailReq.getTransId());
        return Response.ok(ampTransDetailRes);
    }
}
