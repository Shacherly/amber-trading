package com.google.backend.trading.controller.internal;

import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.swap.SwapType;
import com.google.backend.trading.model.swap.api.SwapPriceReq;
import com.google.backend.trading.model.swap.api.SwapPriceRes;
import com.google.backend.trading.service.SwapService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author david.chen
 * @date 2022/2/22 13:03
 */
@Api(tags = "提供给AIP定时任务的接口")
@Slf4j
@Validated
@RequestMapping("/internal/v1/aip")
@RestController
public class OperateForAipController {

    @Autowired
    private SwapService swapService;

    @ApiOperation(value = "兑换报价接口")
    @GetMapping("/swap/price")
    public Response<SwapPriceRes> price(@Valid SwapPriceReq req, @RequestParam(required = false) String uid) {
        req.setFeeFree(false);
        if (null == req.getMode()) {
            req.setMode(SwapType.OBTAINED.getName());
        }
        if (null == req.getQuantity()) {
            req.setQuantity(BigDecimal.ZERO);
        }
        if (req.getFromCoin().equals(req.getToCoin())) {
            throw new IllegalArgumentException("from coin and to coin can't be the same");
        }
        return Response.ok(swapService.queryPrice(req, uid));
    }
}
