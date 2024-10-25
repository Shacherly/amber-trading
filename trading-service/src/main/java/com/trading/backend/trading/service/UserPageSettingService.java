package com.google.backend.trading.service;

import com.google.backend.trading.model.config.api.UserPageSettingVo;

import java.sql.SQLException;

/**
 * @author adam.wang
 * @date 2021/10/4 10:41
 */
public interface UserPageSettingService {
    /**
     * 通过uid查询用户交易配置信息
     *
     * @param uid
     * @return userPageSettingVo
     */
    UserPageSettingVo queryUserPageSetting(String uid);

    /**
     * 插入更新
     *
     * @param req
     * @param uid
     */
    UserPageSettingVo updateOrInsertUserPageSetting(UserPageSettingVo req, String uid);
}
