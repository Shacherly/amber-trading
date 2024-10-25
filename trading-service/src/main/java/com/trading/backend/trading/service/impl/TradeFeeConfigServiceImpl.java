package com.google.backend.trading.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.backend.trading.client.feign.ClubClient;
import com.google.backend.trading.dao.mapper.DefaultTradeFeeDefaultConfigMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeFeeUserConfigMapper;
import com.google.backend.trading.dao.mapper.DefaultTradeFundingDisableMapper;
import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.dao.model.TradeFeeDefaultConfigExample;
import com.google.backend.trading.dao.model.TradeFeeUserConfig;
import com.google.backend.trading.dao.model.TradeFeeUserConfigExample;
import com.google.backend.trading.dao.model.TradeFundingDisable;
import com.google.backend.trading.dao.model.TradeFundingDisableExample;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.mapstruct.user.TradeFeeConfigStruct;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.trade.fee.TradeFeeConfigData;
import com.google.backend.trading.model.trade.fee.UserFeeConfigRate;
import com.google.backend.trading.model.trade.fee.VIPLevelEnum;
import com.google.backend.trading.model.user.club.BriefUserClubInfo;
import com.google.backend.trading.model.user.club.ClubInfoUidReq;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author david.chen
 * @date 2021/12/30 17:50
 */
@Service
@Slf4j
public class TradeFeeConfigServiceImpl implements TradeFeeConfigService {
    @Resource
    private DefaultTradeFeeDefaultConfigMapper defaultTradeFeeDefaultConfigMapper;
    @Resource
    private DefaultTradeFeeUserConfigMapper defaultTradeFeeUserConfigMapper;
    @Resource
    private DefaultTradeFundingDisableMapper defaultTradeFundingDisableMapper;
    @Autowired
    private ClubClient clubClient;
    @Resource
    private TradeFeeConfigStruct tradeFeeConfigStruct;
    @Autowired
    private TradeAssetService tradeAssetService;


    /**
     * uid -> BriefUserClubInfo
     */
    private Cache<String, UserFeeConfigRate> userFeeConfigCache = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    /**
     * uid -> BriefUserClubInfo
     */
    private Cache<String, BriefUserClubInfo> uclubCachedMap = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();


    @Override
    public boolean insertOrUpdateDefault(TradeFeeDefaultConfig tradeFeeDefaultConfig) {
        TradeFeeDefaultConfigExample example = new TradeFeeDefaultConfigExample();
        TradeFeeDefaultConfigExample.Criteria criteria = example.createCriteria();
        criteria.andTagEqualTo(tradeFeeDefaultConfig.getTag());
        long count = defaultTradeFeeDefaultConfigMapper.countByExample(example);
        if (count == 0) {
            return defaultTradeFeeDefaultConfigMapper.insertSelective(tradeFeeDefaultConfig) > 0;
        } else {
            tradeFeeDefaultConfig.setMtime(new Date());
            return defaultTradeFeeDefaultConfigMapper.updateByExampleSelective(tradeFeeDefaultConfig, example) > 0;
        }
    }

    @Override
    public boolean delUserTradeFeeConfig(String uid) {
        // 1. 删除 trade_fee_config_user
        TradeFeeUserConfigExample example = new TradeFeeUserConfigExample();
        TradeFeeUserConfigExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        defaultTradeFeeUserConfigMapper.deleteByExample(example);

        //2. 删除 trade_funding_disable
        this.updateFundingCostConfig(uid, true);
        return true;
    }

    @Override
    public boolean updateFundingCostConfig(String uid, Boolean enable) {
        TradeFundingDisableExample example = new TradeFundingDisableExample();
        example.createCriteria().andUidEqualTo(uid);
        if (enable) {
            // del
            defaultTradeFundingDisableMapper.deleteByExample(example);
        } else {
            long count = defaultTradeFundingDisableMapper.countByExample(example);
            if (count == 0) {
                //insert
                TradeFundingDisable tradeFundingDisable = new TradeFundingDisable();
                tradeFundingDisable.setUid(uid);
                defaultTradeFundingDisableMapper.insertSelective(tradeFundingDisable);
            }
        }
        return true;
    }

    @Override
    public boolean insertOrUpdateUser(TradeFeeConfigData tradeFeeConfigData) {

        //1. 先更新 funding cost数据
        this.updateFundingCostConfig(tradeFeeConfigData.getUid(), tradeFeeConfigData.getFundingCostEnable());

        //2. insertOrUpdate tradeFeeUserConfig
        TradeFeeUserConfig tradeFeeUserConfig = tradeFeeConfigStruct.tradeFeeConfigData2TradeFeeUserConfig(tradeFeeConfigData);
        TradeFeeUserConfigExample example = new TradeFeeUserConfigExample();
        example.createCriteria().andUidEqualTo(tradeFeeConfigData.getUid());
        long count = defaultTradeFeeUserConfigMapper.countByExample(example);
        if (count == 0) {
            //insert
            defaultTradeFeeUserConfigMapper.insertSelective(tradeFeeUserConfig);
        } else {
            //update
            tradeFeeUserConfig.setMtime(new Date());
            defaultTradeFeeUserConfigMapper.updateByExampleSelective(tradeFeeUserConfig, example);
        }
        return true;
    }


    @Override
    public boolean isEnableUserTradeFunding(String uid) {
        TradeFundingDisableExample example = new TradeFundingDisableExample();
        example.createCriteria().andUidEqualTo(uid);
        long count = defaultTradeFundingDisableMapper.countByExample(example);
        return count == 0;
    }

    @Override
    public List<TradeFeeDefaultConfig> selectAllDefaultConfig() {
        TradeFeeDefaultConfigExample example = new TradeFeeDefaultConfigExample();
        TradeFeeDefaultConfigExample.Criteria criteria = example.createCriteria();
        //https://www.teambition.com/task/6220616b2f476c003f0492bc 业务调整 去掉VIP7
        criteria.andTagNotEqualTo(VIPLevelEnum.DEFAULT_VIP7.getTag());
        List<TradeFeeDefaultConfig> tradeFeeDefaultConfigList = defaultTradeFeeDefaultConfigMapper.selectByExample(example);
        return tradeFeeDefaultConfigList;
    }

    @Override
    public UserFeeConfigRate selectUserFeeConfig(String uid, boolean isDefault) {
        return this.selectUserFeeConfig(uid);
    }

    @Override
    public UserFeeConfigRate selectUserFeeConfig(String uid) {
        UserFeeConfigRate userFeeConfigRate = new UserFeeConfigRate();
        if (uid == null) {
            //默认
            TradeFeeDefaultConfig defaultConfig = this.selectDefaultConfigByTag(VIPLevelEnum.DEFAULT.getTag());
            userFeeConfigRate.setSpotFeeRate(defaultConfig.getSpotFeeRate());
            userFeeConfigRate.setSwapFeeRate(defaultConfig.getSwapFeeRate());
            userFeeConfigRate.setMarginFeeRate(defaultConfig.getMarginFeeRate());
            userFeeConfigRate.setMarginSettleFeeRate(defaultConfig.getMarginSettleFeeRate());
            userFeeConfigRate.setFundingCostEnable(defaultConfig.getFundingCostEnable());
            return userFeeConfigRate;
        }

        //缓存用户fee
        UserFeeConfigRate feeConfigRate = userFeeConfigCache.getIfPresent(uid);
        if (feeConfigRate != null) return feeConfigRate;

//        1. 查询用户VIP等级-》对应trade_fee_default_config
        VIPLevelEnum vipLevelEnum = VIPLevelEnum.DEFAULT;
        try {
            BriefUserClubInfo userClubInfo = getUserClubOrThrowE(uid);
            Integer clubLevel = userClubInfo.getClubLevel();
            Integer whaleLevel = userClubInfo.getWhaleLevel();
            userFeeConfigRate.setVipLevel(clubLevel);
            userFeeConfigRate.setBwcLevel(whaleLevel);
            userFeeConfigRate.setgoogleValue(userClubInfo.getClubPoint());
            vipLevelEnum = VIPLevelEnum.getByLevel(clubLevel == null ? -1 : clubLevel);
            //判断BWC会员等级
            if (whaleLevel != null && whaleLevel > 0) {
                vipLevelEnum = VIPLevelEnum.getByBwcLevel(whaleLevel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TradeFeeDefaultConfig defaultConfig = this.selectDefaultConfigByTag(vipLevelEnum.getTag());
        userFeeConfigRate.setSpotFeeRate(defaultConfig.getSpotFeeRate());
        userFeeConfigRate.setSwapFeeRate(defaultConfig.getSwapFeeRate());
        userFeeConfigRate.setMarginFeeRate(defaultConfig.getMarginFeeRate());
        userFeeConfigRate.setMarginSettleFeeRate(defaultConfig.getMarginSettleFeeRate());
//        2. 查询用户个性化 trade_fee_user_config
        TradeFeeUserConfig tradeFeeUserConfig = this.selectTradeFeeUserConfigByUid(uid);
        if (tradeFeeUserConfig != null) {
            //个人设置优先级最高

            //-1时使用default费率
            // 个人设置小于default才能生效
            if (CommonUtils.isZeroOrPositive(tradeFeeUserConfig.getSpotFeeRate()) &&
                    defaultConfig.getSpotFeeRate().compareTo(tradeFeeUserConfig.getSpotFeeRate()) > 0) {
                userFeeConfigRate.setSpotFeeRate(tradeFeeUserConfig.getSpotFeeRate());
                userFeeConfigRate.setIsCustomSpotFee(true);
            }
            if (CommonUtils.isZeroOrPositive(tradeFeeUserConfig.getSwapFeeRate()) &&
                    defaultConfig.getSwapFeeRate().compareTo(tradeFeeUserConfig.getSwapFeeRate()) > 0) {
                userFeeConfigRate.setSwapFeeRate(tradeFeeUserConfig.getSwapFeeRate());
                userFeeConfigRate.setIsCustomSwapFee(true);
            }
            if (CommonUtils.isZeroOrPositive(tradeFeeUserConfig.getMarginFeeRate()) &&
                    defaultConfig.getMarginFeeRate().compareTo(tradeFeeUserConfig.getMarginFeeRate()) > 0) {
                userFeeConfigRate.setMarginFeeRate(tradeFeeUserConfig.getMarginFeeRate());
                userFeeConfigRate.setIsCustomMarginFee(true);
            }
            if (CommonUtils.isZeroOrPositive(tradeFeeUserConfig.getMarginSettleFeeRate()) &&
                    defaultConfig.getMarginSettleFeeRate().compareTo(tradeFeeUserConfig.getMarginSettleFeeRate()) > 0) {
                userFeeConfigRate.setMarginSettleFeeRate(tradeFeeUserConfig.getMarginSettleFeeRate());
                userFeeConfigRate.setIsCustomMarginSettleFee(true);
            }
        }
//        3. 查询资金费率
        userFeeConfigRate.setFundingCostEnable(this.isEnableUserTradeFunding(uid));
        BigDecimal tradeAmount30d = tradeAssetService.get30TradeAmount(uid);
        userFeeConfigRate.setUserTradeAmount30d(tradeAmount30d);
        userFeeConfigCache.put(uid, userFeeConfigRate);
        return userFeeConfigRate;
    }

    public static void main(String[] args) {
        boolean equals = BigDecimal.ZERO.compareTo(new BigDecimal("-1")) > 0;
        System.out.println(equals);

    }

    private BriefUserClubInfo getUserClubOrThrowE(String uid) {
        BriefUserClubInfo briefUserClubInfo = uclubCachedMap.getIfPresent(uid);
        if (briefUserClubInfo != null) return briefUserClubInfo;
        ClubInfoUidReq clubInfoUidReq = new ClubInfoUidReq(uid);
        Response<BriefUserClubInfo> personalClub = clubClient.getPersonalClub(clubInfoUidReq);
        if (personalClub.getCode() != BusinessExceptionEnum.SUCCESS.getCode()) {
            log.error("clubClient req = {} ,resp = {}", clubInfoUidReq, personalClub);
            throw new BusinessException(BusinessExceptionEnum.CLUB_FALLBACK);
        }
        BriefUserClubInfo data = personalClub.getData();
        uclubCachedMap.put(uid, data);
        return data;
    }

    @Override
    public TradeFeeDefaultConfig selectDefaultConfigByTag(String tag) {
        TradeFeeDefaultConfigExample tradeFeeDefaultConfigExample = new TradeFeeDefaultConfigExample();
        TradeFeeDefaultConfigExample.Criteria criteria = tradeFeeDefaultConfigExample.createCriteria();
        criteria.andTagEqualTo(tag);
        List<TradeFeeDefaultConfig> tradeFeeDefaultConfigList = defaultTradeFeeDefaultConfigMapper.selectByExample(tradeFeeDefaultConfigExample);
        if (!tradeFeeDefaultConfigList.isEmpty()) {
            return tradeFeeDefaultConfigList.get(0);
        }
        return null;
    }

    @Override
    public TradeFeeUserConfig selectTradeFeeUserConfigByUid(String uid) {
        TradeFeeUserConfigExample example = new TradeFeeUserConfigExample();
        example.createCriteria().andUidEqualTo(uid);
        List<TradeFeeUserConfig> tradeFeeUserConfigs = defaultTradeFeeUserConfigMapper.selectByExample(example);
        if (!tradeFeeUserConfigs.isEmpty()) {
            return tradeFeeUserConfigs.get(0);
        }
        return null;
    }


    @Override
    public boolean delUserTradeFeeConfigWithOutFundingDisable(String uid) {
        // 1. 删除 trade_fee_config_user
        TradeFeeUserConfigExample example = new TradeFeeUserConfigExample();
        TradeFeeUserConfigExample.Criteria criteria = example.createCriteria();
        criteria.andUidEqualTo(uid);
        defaultTradeFeeUserConfigMapper.deleteByExample(example);
        return true;
    }
}
