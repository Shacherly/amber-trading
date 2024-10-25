package com.google.backend.trading.httpclient.client.feign;

import com.google.backend.common.web.Response;
import com.google.backend.trading.model.common.PageResult;
import com.google.backend.trading.model.common.model.riskcontrol.PositionRes;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author adam.wang
 * @date 2021/10/2 16:23
 */
@RequestMapping({"/internal/v1/risk-control"})
public interface RiskControlApi {

    /**
     * 批量查询用户的仓位信息根据用户
     *
     * @param uids
     * @return
     */
    @ApiOperation(value = "批量查询用户的仓位信息根据用户")
    @PostMapping("/positionByUser")
    Response<List<PositionRes>> listAllActivePositions(@Valid @RequestBody List<String> uids);

    /**
     * 批量查询用户的仓位信息
     *
     * @return
     */
    @ApiOperation(value = "查询所有用户的仓位信息")
    @GetMapping("/position")
    Response<List<PositionRes>> listAllActivePositions();

    /**
     * 分页批量查询用户的仓位信息
     *
     * @return
     */
    @ApiOperation(value = "分页查询所有用户的仓位信息")
    @GetMapping("/position/page")
    Response<PageResult<PositionRes>> listAllActivePositions(@RequestParam(value = "page", required = false) Integer page,
                                                             @RequestParam(value = "page_size", required = false) Integer pageSize);

    /**
     * 查询用户的配置信息
     * @param uids
     * @return
     */
    @ApiOperation(value = "查询用户的配置信息")
    @PostMapping("/list/user/setting")
    Response<List<UserSettingRes>> queryUserSetting(@NotNull @RequestBody List<String> uids);

    /**
     * 强平--取消用户所有非系统订单
     * @param uid
     * @return
     */
    @ApiOperation(value = "取消用户所有非系统订单")
    @GetMapping("/order/cancel")
    Response<Void> cancelOrder(@RequestParam String uid);


    /**
     * 强平--减仓、平仓
     * @param reducePositionReq
     * @return
     */
    @ApiOperation(value = "强平--减仓、平仓")
    @PostMapping("/position/reduce")
    Response<Void> reducePosition(@Valid @RequestBody ReducePositionReq reducePositionReq);

    /**
     * 风控清算--现货
     * @param uid
     * @return
     */
    @ApiOperation(value = "风控清算--现货")
    @GetMapping("/liquid/spot")
    Response<Void> liquidSpot(@RequestParam String uid);

    /**
     * 风控清算--资产余额
     * @param uid
     * @return
     */
    @ApiOperation(value = "风控清算--余额")
    @GetMapping("/liquid/balance")
    Response<Void> liquidBalance(@RequestParam String uid);








}
