package com.google.backend.trading.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.backend.common.model.kyc.res.ClientKycStatusRes;
import com.google.backend.trading.client.feign.UserCertificationClient;
import com.google.backend.trading.config.TradeProperties;
import com.google.backend.trading.exception.BusinessException;
import com.google.backend.trading.exception.BusinessExceptionEnum;
import com.google.backend.trading.model.trade.TradeType;
import com.google.backend.trading.model.user.UserInfo;
import com.google.backend.trading.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.EvictionMode;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.backend.trading.constant.RedisKeyConstants.TRADE_USER_LOCALE;
import static com.google.backend.trading.model.trade.TradeType.MARGIN;

/**
 * @author trading
 * @date 2021/12/6 17:57
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final Set<Integer> validKycStatus = ImmutableSet.of(2, 3);

    @Autowired
    private UserCertificationClient userCertificationClient;

    @Value("${spring.profiles.active}")
    private String active;


    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private TradeProperties properties;

    /**
     * uid -> ClientKycStatusRes
     */
    private Cache<String, ClientKycStatusRes> kycCachedMap = CacheBuilder.newBuilder()
            .maximumSize(1024)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    /**
     * uid -> Locale
     */
    private RMapCache<String, Locale> localeCachedMap;

    @PostConstruct
    public void init() {
        this.localeCachedMap = redissonClient.getMapCache(TRADE_USER_LOCALE, new SerializationCodec());
        this.localeCachedMap.setMaxSize(10240, EvictionMode.LFU);
    }

    @Override
    public void checkUserKycOnlyOrThrowE(UserInfo userInfo, TradeType tradeType) {
        checkKyc(userInfo, tradeType);
    }

    @Override
    public void checkUserComplianceOrThrowE(UserInfo userInfo) {
        //Check USA KYC user
        checkKyc(userInfo, MARGIN);
        if (properties.getMargin().isIpCompliance()) {
            checkIpCountryAndRegion(userInfo);
        }
    }

    private void checkKyc(UserInfo userInfo, TradeType tradeType) {
        String uid = userInfo.getUid();
        if (StringUtils.equalsAny(active, "prod", "uat")) {
            ClientKycStatusRes cache = kycCachedMap.getIfPresent(uid);
            if (null == cache) {
                com.google.backend.common.model.web.Response<ClientKycStatusRes> resp =
                        userCertificationClient.getCertificationStatus(uid);
                if (resp.getCode() == BusinessExceptionEnum.SUCCESS.getCode()) {
                    cache = resp.getData();
                    kycCachedMap.put(uid, cache);
                }
            }
            if (null == cache) {
                //pessimistic handle
                throw new BusinessException(BusinessExceptionEnum.MARGIN_NEED_KEY);
            }
            //检查kyc状态
            if (cache.getKycStatus() == null || !validKycStatus.contains(cache.getKycStatus())) {
                if (MARGIN == tradeType) {
                    throw new BusinessException(BusinessExceptionEnum.MARGIN_NEED_KEY);
                }
                throw new BusinessException(BusinessExceptionEnum.TRADE_NEED_KEY);
            }
            //margin需额外检查kyc国家
            if (MARGIN == tradeType && (StringUtils.isEmpty(cache.getKycCountry()) || "USA".equals(cache.getKycCountry()))) {
                throw new BusinessException(BusinessExceptionEnum.MARGIN_NOT_SUPPORT_USA_CLIENT);
            }
        }
    }

    private static final Map<String, Set<String>> COUNTRY_CODE_REGION_CODE_LIMIT_MAP = new HashedMap<>();

    private static final String ALL_REGION = "ALL_REGION";

    static {
        Map<String, Set<String>> map = new HashedMap<>();
        map.put("AU", ImmutableSet.of(ALL_REGION));
        map.put("BR", ImmutableSet.of(ALL_REGION));
        map.put("CA", ImmutableSet.of(ALL_REGION));
        map.put("HK", ImmutableSet.of(ALL_REGION));
        map.put("NZ", ImmutableSet.of(ALL_REGION));
        map.put("PH", ImmutableSet.of(ALL_REGION));
        map.put("RU", ImmutableSet.of(ALL_REGION));
        map.put("ZA", ImmutableSet.of(ALL_REGION));
        map.put("GB", ImmutableSet.of(ALL_REGION));
        map.put("UM", ImmutableSet.of(ALL_REGION));
        map.put("MP", ImmutableSet.of(ALL_REGION));
        map.put("US", ImmutableSet.of("AK", "AZ", "CA", "CO", "DE", "FL", "ID", "IL", "IN", "KS", "KY",
                "MA", "MO", "MT", "NH", "ND", "PA", "SC", "TN", "TX", "UT", "VA", "WI", "WY"));
        COUNTRY_CODE_REGION_CODE_LIMIT_MAP.putAll(map);
    }

    private void checkIpCountryAndRegion(UserInfo userInfo) {
        log.info("checkIpCountryAndRegion, user info = {}", userInfo);
        if (StringUtils.equalsAny(active, "prod", "uat")) {
            String ipCountryCode = userInfo.getIpCountryCode();
            String ipRegionCode = userInfo.getIpRegionCode();
            if (StringUtils.isEmpty(ipCountryCode)) {
                return;
            }
            if (StringUtils.equals(active, "uat") && "HK".equals(ipCountryCode)) {
                log.info("checkIpCountryAndRegion uat skip HK user check for test");
                return;
            }
            Set<String> regionSet = COUNTRY_CODE_REGION_CODE_LIMIT_MAP.getOrDefault(ipCountryCode, Collections.emptySet());
            if (regionSet.contains(ALL_REGION) || regionSet.contains(ipRegionCode)) {
                throw new BusinessException(BusinessExceptionEnum.COMPLIANCE_LIMIT);
            }
        }
    }

    @Override
    public Locale locale(String uid) {
        return this.localeCachedMap.getOrDefault(uid, Locale.US);
    }

    @Override
    public void setLocale(String uid, Locale locale) {
        this.localeCachedMap.put(uid, locale);
    }
}
