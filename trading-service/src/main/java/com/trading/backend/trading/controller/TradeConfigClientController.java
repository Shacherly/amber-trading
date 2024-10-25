package com.google.backend.trading.controller;

import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.dao.model.TradeUserTradeSetting;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.exception.NeedLoginException;
import com.google.backend.trading.mapstruct.config.TradeUserTradeSettingMapStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.common.model.config.UserTradeSettingVo;
import com.google.backend.trading.model.config.api.UserPageSettingVo;
import com.google.backend.trading.model.config.api.UserTradeSettingUpdateReq;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.MarginService;
import com.google.backend.trading.service.TradeCompositeService;
import com.google.backend.trading.service.UserPageSettingService;
import com.google.backend.trading.service.UserTradeSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

/**
 * 用户侧交易配置相关接口
 *
 * @author adam.wang
 * @date 2021/9/27 16:39
 */
@Slf4j
@Api(value="用户配置相关接口",tags="用户配置相关接口")
@RestController
@Validated
@RequestMapping("/v1/config/client")
public class TradeConfigClientController {
    @Resource
    private UserTradeSettingService userTradeSettingService;

    @Resource
    private TradeUserTradeSettingMapStruct tradeUserTradeSettingMapStruct;

    @Resource
    private UserPageSettingService userPageSettingService;

    @Autowired
    private TradeCompositeService tradeCompositeService;

    @Autowired
    private MarginService marginService;

    @Autowired
    private RedissonClient client;

    @Autowired
    private TradeProperties properties;

    @PutMapping
    @ApiOperation(value = "用户配置")
    public Response<UserTradeSettingVo> client(@Valid @RequestBody UserTradeSettingUpdateReq req, UserInfo userInfo) {
        String lockKey = userInfo.getUid() + ":" + "trade-setting";
        RLock lock = client.getLock(lockKey);
        boolean getLock = false;
        try {
            getLock = lock.tryLock(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("tryLock InterruptedException");
        }
        if (!getLock) {
            throw new BusinessException(BusinessExceptionEnum.UNEXPECTED_ERROR);
        }
        TradeUserTradeSetting tradeUserTradeSetting = tradeUserTradeSettingMapStruct.req2TradeUserTradeSetting(req);
        UserTradeSettingVo vo;
        try {
            vo = tradeCompositeService.checkAndUpdateUserTradeSetting(tradeUserTradeSetting,
                    userInfo.getUid());
            if (properties.getTraders().getUidArr().contains(userInfo.getUid())) {
                vo.setEnableOrderNotes(true);
            }
        } finally {
            lock.unlock();
        }
        return Response.ok(vo);
    }

    @GetMapping("/query")
    @ApiOperation(value = "用户配置查询", notes = "查询接口特别说明")
    public Response<UserTradeSettingVo> client(UserInfo req) {
        String uid = req.getUid();
        UserTradeSettingVo res = userTradeSettingService.queryTradeSetting(uid);
        boolean existMarginActiveOrder = marginService.countActiveOrder(uid) > 0;
        res.setExistMarginActiveOrder(existMarginActiveOrder);
        if (properties.getTraders().getUidArr().contains(uid)) {
            res.setEnableOrderNotes(true);
        }
        return Response.ok(res);
    }

    @PutMapping("/page")
    @ApiOperation(value = "用户页面行情卡片配置", notes = "用户页面行情卡片配置")
    public Response<UserPageSettingVo> clientPage(@Valid @RequestBody UserPageSettingVo req, UserInfo userInfo, HttpServletResponse response) {
        if (null == userInfo) {
            throw new NeedLoginException("put clientPage need login");
        }
        userPageSettingService.updateOrInsertUserPageSetting(req, userInfo.getUid());
        return Response.ok();
    }

    @GetMapping("/page")
    @ApiOperation(value = "用户页面行情卡片配置", notes = "用户页面行情卡片配置")
    public Response<UserPageSettingVo> clientPage(UserInfo req) {
        String uid = null;
        if (req != null) {
            uid = req.getUid();
        }
        UserPageSettingVo res = userPageSettingService.queryUserPageSetting(uid);
        return Response.ok(res);
    }
}
