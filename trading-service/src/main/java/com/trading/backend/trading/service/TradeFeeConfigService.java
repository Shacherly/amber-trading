package com.google.backend.trading.service;

import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.dao.model.TradeFeeUserConfig;
import com.google.backend.trading.model.trade.fee.TradeFeeConfigData;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * 用户手续费service
 * @author david.chen
 * @date 2021/12/30 17:49
 */
public interface TradeFeeConfigService {
    boolean insertOrUpdateDefault(TradeFeeDefaultConfig tradeFeeDefaultConfig);

    boolean delUserTradeFeeConfig(String uid);

    /**
     * 更新 funding cost fee 表
     *
     * @param uid    用户id
     * @param enable true 表示正常收取（删除） false表示关闭收取（插入）
     * @return
     */
    boolean updateFundingCostConfig(String uid, Boolean enable);

    boolean insertOrUpdateUser(TradeFeeConfigData tradeFeeConfigData);

    List<TradeFeeDefaultConfig> selectAllDefaultConfig();

    /**
     * @param uid 用户ID
     * @return
     */
    @NonNull
    UserFeeConfigRate selectUserFeeConfig(@Nullable String uid);

    /**
     * 已废弃 建议调用 {@link #selectUserFeeConfig(String)}
     *
     * @param uid                 用户ID
     * @param isComputeTradeLevel 是否查询tradeLevel
     * @param uid                 用户ID
     * @return
     */
    @Deprecated
    @NonNull
    UserFeeConfigRate selectUserFeeConfig(String uid, boolean isComputeTradeLevel);

    @Nullable
    TradeFeeDefaultConfig selectDefaultConfigByTag(String tag);

    @Nullable
    TradeFeeUserConfig selectTradeFeeUserConfigByUid(String uid);

    /**
     * 是否 关闭资金费率
     *
     * @param uid
     * @return true关闭 false开启
     */
    boolean isEnableUserTradeFunding(String uid);


    boolean delUserTradeFeeConfigWithOutFundingDisable(String uid);
}
