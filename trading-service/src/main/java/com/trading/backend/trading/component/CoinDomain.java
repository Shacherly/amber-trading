package com.google.backend.trading.component;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSwapConfig;
import com.google.backend.trading.model.funding.dto.FundingCostDto;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 币种业务对象
 *
 * @author trading
 * @date 2021/10/11 20:57
 */
@Slf4j
public class CoinDomain {

    public static ConcurrentHashMap<String, CoinDomain> CACHE = new ConcurrentHashMap<>();

    private final String name;

    /**
     * 客户借出利率
     */
    private volatile BigDecimal lend = BigDecimal.ZERO;

    /**
     * 客户借入利率
     */
    private volatile BigDecimal borrow = BigDecimal.ZERO;

    /**
     * 资金费率时间戳
     */
    private volatile long fundingRateTs;

    public static void initCache(String name) {
        CACHE.computeIfAbsent(name, CoinDomain::new);
    }

    private volatile CoinCommonConfig commonConfig;

    private volatile CoinSwapConfig swapConfig;

    private CoinDomain(String name) {
        this.name = name;
    }

    public void updateCoinConfig(CoinCommonConfig commonConfig, CoinSwapConfig swapConfig) {
        this.commonConfig = commonConfig;
        this.swapConfig = swapConfig;
    }

    public CoinCommonConfig getCommonConfig() {
        return commonConfig;
    }

    /**
     * 获取兑换配置后需要用方法 {@link CoinSwapConfig#isSupport()} 进行交易是否支持兑换
     *
     * @return
     */
    public CoinSwapConfig getSwapConfig() {
        return swapConfig;
    }

    /**
     * 更新 lend 和 borrow，唯一的写入入口
     *
     * @param lend
     * @param borrow
     */
    public void updateFundingRate(BigDecimal lend, BigDecimal borrow, long fundingRateTs) {
        this.lend = lend.setScale(Constants.FUNDING_RATE_PRECISION, RoundingMode.DOWN);
        this.borrow = borrow.setScale(Constants.FUNDING_RATE_PRECISION, RoundingMode.DOWN);
        this.fundingRateTs = fundingRateTs;
    }

    public BigDecimal getLend() {
        return lend;
    }

    public BigDecimal getBorrow() {
        return borrow;
    }

    public String getName() {
        return name;
    }

    public long getFundingRateTs() {
        return fundingRateTs;
    }

    @NonNull
    public static CoinDomain nonNullGet(String coin) {
        CoinDomain coinDomain = CoinDomain.CACHE.get(coin);
        return Objects.requireNonNull(coinDomain, String.format("CoinDomain not found in cache, coin = %s", coin));
    }

    @Nullable
    public static CoinDomain nullableGet(String coin) {
        return CoinDomain.CACHE.get(coin);
    }


    public static FundingCostDto positionFundingCostDto(String symbol, String direction, BigDecimal quantity) {
        Pair<String, String> pair = CommonUtils.coinPair(symbol);
        String base = pair.getFirst();
        String quote = pair.getSecond();
        BigDecimal midPrice = SymbolDomain.nonNullGet(base + Constants.BASE_QUOTE).midPrice();
        BigDecimal rate;
        BigDecimal lend;
        BigDecimal borrow;
        CoinDomain baseCoinDomain = nonNullGet(base);
        CoinDomain quoteCoinDomain = nonNullGet(quote);
        if (Direction.isBuy(direction)) {
            lend = baseCoinDomain.lend;
            borrow = quoteCoinDomain.borrow;
            rate = baseCoinDomain.lend.subtract(quoteCoinDomain.borrow);
        } else {
            lend = quoteCoinDomain.lend;
            borrow = baseCoinDomain.borrow;
            rate = quoteCoinDomain.lend.subtract(baseCoinDomain.borrow);
        }
        if (log.isDebugEnabled()) {
            log.debug("base lend = {} borrow = {}, quote lend = {} borrow = {}", baseCoinDomain.lend, baseCoinDomain.borrow,
                    quoteCoinDomain.lend, baseCoinDomain.borrow);
        }
        BigDecimal fundingCost = midPrice.multiply(quantity).multiply(rate).setScale(Constants.FUNDING_RATE_PRECISION, RoundingMode.DOWN);
        return new FundingCostDto(fundingCost, midPrice, lend, borrow);
    }

    public static FundingCostDto negativeBalanceFundingCostDto(String coin, BigDecimal quantity) {
        BigDecimal borrow = nonNullGet(coin).borrow;
        BigDecimal fundingCost = quantity.multiply(borrow).setScale(Constants.FUNDING_RATE_PRECISION,
                RoundingMode.DOWN);
        return new FundingCostDto(fundingCost, BigDecimal.ZERO, BigDecimal.ZERO, borrow);
    }

    public static BigDecimal positionFundingCost(String symbol, String direction, BigDecimal quantity) {
        Pair<String, String> pair = CommonUtils.coinPair(symbol);
        String base = pair.getFirst();
        BigDecimal midPrice = SymbolDomain.nonNullGet(base + Constants.BASE_QUOTE).midPrice();
        BigDecimal rate = fundingCostRate(symbol, direction);
        return midPrice.multiply(quantity).multiply(rate).setScale(Constants.FUNDING_RATE_PRECISION, RoundingMode.DOWN);
    }

    /**
     * 资金费率
     *
     * @param symbol
     * @param direction
     * @return
     */
    public static BigDecimal fundingCostRate(String symbol, String direction) {
        Pair<String, String> pair = CommonUtils.coinPair(symbol);
        String base = pair.getFirst();
        String quote = pair.getSecond();
        BigDecimal rate;
        CoinDomain baseCoinDomain = nonNullGet(base);
        CoinDomain quoteCoinDomain = nonNullGet(quote);
        BigDecimal baseLend = baseCoinDomain.lend;
        BigDecimal baseBorrow = baseCoinDomain.borrow;
        BigDecimal quoteLend = quoteCoinDomain.lend;
        BigDecimal quoteBorrow = quoteCoinDomain.borrow;
        if (Direction.isBuy(direction)) {
            rate = baseLend.subtract(quoteBorrow);
        } else {
            rate = quoteLend.subtract(baseBorrow);
        }
        if (log.isDebugEnabled()) {
            log.debug("base lend = {} borrow = {}, quote lend = {} borrow = {}", baseLend, baseBorrow, quoteLend, quoteBorrow);
        }
        return rate;
    }


    public static List<CoinDomain> getLiteCoin() {
        return CACHE.values().stream()
                .filter(e -> {
                    CoinCommonConfig commonConfig = e.getCommonConfig();
                    String businessClient = commonConfig.getBusinessClient();
                    return StringUtils.isNotEmpty(businessClient)
                            && StringUtils.contains(businessClient, "lite")
                            && e.getSwapConfig().isSupport();
                }).sorted(Comparator.comparingInt(o -> o.getCommonConfig().getPriority())).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "CoinDomain{" +
                "name='" + name + '\'' +
                '}';
    }
}
