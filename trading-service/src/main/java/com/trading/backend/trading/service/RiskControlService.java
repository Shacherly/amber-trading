package com.google.backend.trading.service;

import com.google.backend.common.dto.base.MsgHeadDto;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.common.model.riskcontrol.ReducePositionReq;
import com.google.backend.trading.model.common.model.riskcontrol.UserSettingRes;

/**
 * 提供给风控的接口定义
 *
 * @author savion.chen
 * @date 2021/10/8 11:11
 */
public interface RiskControlService {


    /**
     * 取消订单
     * @param uid
     */
    void cancelOrder(String uid);

    /**
     * 减仓
     * @param reducePositionReq
     */
    void reducePosition(ReducePositionReq reducePositionReq);

    /**
     * 清算现货
     * @param uid
     */
    void liquidSpot(String uid);

    /**
     * 清算余额
     * @param uid
     */
    void liquidBalance(String uid);

    /**
     * 通知风控用户设置变更
     * @param settingRes
     */
    void userSettingChangeNotice(UserSettingRes settingRes);

    /**
     * 仓位通知
     *
     * @param transaction
     */
    void positionNotice(TradeTransaction transaction);

    void positionNoticeWithVersion(TradeTransaction transaction, MsgHeadDto dto);


}
