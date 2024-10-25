package com.google.backend.trading.util;

import com.google.backend.trading.component.CoinDomain;
import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.model.commonconfig.dto.CoinCommonConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSwapConfig;
import com.google.backend.trading.model.commonconfig.dto.CoinSymbolConfig;
import com.google.backend.trading.model.trade.Direction;
import com.google.backend.trading.model.trade.OrderType;
import com.google.backend.trading.model.trade.SourceType;
import com.google.backend.trading.model.trade.TradeStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.UUID;

/**
 * 公共的函数部分
 *
 * @author savion.chen
 * @date 2021/9/29 14:08
 */
@Slf4j
public class CommonUtils {

    @NonNull
    public static boolean checkSpotSymbol(String symbol) {
        SymbolDomain domain = SymbolDomain.nonNullGet(symbol);
        CoinSymbolConfig cfg = domain.getCoinSymbolConfig();
        return cfg.isSpotValid();
    }

    @NonNull
    public static boolean checkMarginSymbol(String symbol) {
        SymbolDomain domain = SymbolDomain.nonNullGet(symbol);
        CoinSymbolConfig cfg = domain.getCoinSymbolConfig();
        return cfg.isMarginValid();
    }

    @NonNull
    public static boolean checkCoinConfig(String coin) {
        CoinDomain domain = CoinDomain.nonNullGet(coin);
        if (domain.getSwapConfig().isSupport()) {
            return true;
        }
        log.error("swap coin not exist, coin = {}", coin);
        return false;
    }

    public static  BigDecimal getBuyPrice(String symbol) {
        return SymbolDomain.nonNullGet(symbol).getBuyPrice();
    }

    public static BigDecimal getSellPrice(String symbol) {
        return SymbolDomain.nonNullGet(symbol).getSellPrice();
    }

    public static BigDecimal getMiddlePrice(String symbol) {
        SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
        return symbolDomain.midPrice();
    }

    /**
     * 获取对应币种的usd价值，使用中间价
     *
     * @param coin
     * @param size
     * @return
     */
    public static BigDecimal usdValue(String coin, BigDecimal size) {
        if (coin.equals(Constants.BASE_COIN)) {
            return size;
        } else {
            String symbol = coin + Constants.BASE_QUOTE;
            // 根据当前价计算该币种以USD计价的市值
            BigDecimal lastPrice = CommonUtils.getMiddlePrice(symbol);
            return size.multiply(lastPrice);
        }
    }

    public static BigDecimal getMinOrder(String symbol) {
        CoinSymbolConfig item = SymbolDomain.nonNullGet(symbol).getCoinSymbolConfig();
        return item.getMinOrderAmount();
    }

    public static BigDecimal getMaxOrder(String symbol) {
        CoinSymbolConfig item = SymbolDomain.nonNullGet(symbol).getCoinSymbolConfig();
        return item.getMaxOrderAmount();
    }

    public static BigDecimal getFokMaxOrder(String symbol) {
        CoinSymbolConfig item = SymbolDomain.nonNullGet(symbol).getCoinSymbolConfig();
        return item.getFokMaxOrderAmount();
    }

    public static int getPrecision(String symbol) {
        CoinSymbolConfig item = SymbolDomain.nonNullGet(symbol).getCoinSymbolConfig();
        return item.getPrecision();
    }

    public static int getCoinPrecision(String coin) {
        CoinCommonConfig item = CoinDomain.nonNullGet(coin).getCommonConfig();
        return item.getBaseIssueQuantity();
    }


    public static BigDecimal getCoinMin(String coin) {
        CoinSwapConfig item = CoinDomain.nonNullGet(coin).getSwapConfig();
        return item.getMinOrderAmount();
    }

    public static BigDecimal getCoinMax(String coin) {
        CoinSwapConfig item = CoinDomain.nonNullGet(coin).getSwapConfig();
        return item.getMaxOrderAmount();
    }


    public static BigDecimal keepPrecision(BigDecimal origin, int digits) {
        return origin.setScale(digits, RoundingMode.HALF_UP);
    }

    // 根据小数位检查精度是否符合要求
    public static boolean checkPrecision(BigDecimal origin, int digits) {
        BigDecimal after = keepPrecision(origin, digits);
        return (origin.compareTo(after) == 0);
    }

    // 对给点浮点数判断小数点位数
    public static int getDecimalDigits(BigDecimal mantissa) {
        String tailNum = mantissa.toString();
        int index = tailNum.indexOf(".");
        return index < 0 ? 0 : tailNum.length() - index - 1;
    }

    public static boolean isPositive(BigDecimal num) {
        return (num != null) && (num.compareTo(ZERO_NUM) > 0);
    }

    public static boolean isZeroOrPositive(BigDecimal num) {
        return (num != null) && (num.compareTo(ZERO_NUM) >= 0);
    }

    // 检查值的范围是否符合
    public static boolean isWithinScope(BigDecimal origin, BigDecimal minNum, BigDecimal maxNum) {
        return (origin.compareTo(minNum) > -1 && origin.compareTo(maxNum) < 1);
    }

    public static boolean isValidString(String str) {
        return (str != null && str.length() > 0);
    }

    public static BigDecimal roundDivide(BigDecimal a, BigDecimal b) {
        if (isPositive(a) && isPositive(b)) {
            return a.divide(b, Constants.PRICE_PRECISION, RoundingMode.DOWN);
        }
        return ZERO_NUM;
    }

    public static BigDecimal convertShow(BigDecimal num) {
        return num.setScale(Constants.SHOW_PRECISION, RoundingMode.DOWN);
    }

    public static BigDecimal convertData(BigDecimal num) {
        return num.setScale(Constants.DEFAULT_PRECISION, RoundingMode.UP);
    }

    public static BigDecimal roundPrice(BigDecimal origin, String symbol) {
        int digits = CommonUtils.getPrecision(symbol);
        return origin.setScale(digits, RoundingMode.DOWN);
    }

    public static BigDecimal roundAmount(BigDecimal origin, String coin) {
        int digits = CommonUtils.getCoinPrecision(coin);
        return origin.setScale(digits, RoundingMode.DOWN);
    }

    public static String getPaymentCoin(String symbol, String direct) {
        return Direction.isBuy(direct) ? getQuoteCoin(symbol) : getBaseCoin(symbol);
    }

    public static String getObtainedCoin(String symbol, String direct) {
        return Direction.isBuy(direct) ? getBaseCoin(symbol) : getQuoteCoin(symbol);
    }

    public static String getBaseCoin(String symbol) {
        String[] coins = symbol.split(SEPARATOR);
        return coins.length > 1 ? coins[0] : "";
    }

    public static String getQuoteCoin(String symbol) {
        String[] coins = symbol.split(SEPARATOR);
        return coins.length > 1 ? coins[1] : "";
    }

    public static Pair<String, String> coinPair(String symbol) {
        String[] coins = symbol.split(SEPARATOR);
        return Pair.of(coins[0], coins[1]);
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    public static Date getNowTime() { return new Date(); }
    public static Date getNowTime(Long longTime) { return new Date(longTime);}

    public static boolean isSyncOrder(OrderType type, TradeStrategy strategy, SourceType source) {
        return (type == OrderType.LIMIT && (strategy == TradeStrategy.FOK || strategy == TradeStrategy.IOC))
                || (type == OrderType.MARKET && source != SourceType.PLACED_BY_CLIENT && source != SourceType.BUY_CRYPTOCURRENCY_CONVERSION);
    }

    public static final BigDecimal ZERO_NUM = BigDecimal.ZERO;
    public static final String SEPARATOR = "_";

}
