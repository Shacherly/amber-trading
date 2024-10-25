package com.google.backend.trading.constant;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * @author trading
 * @date 2021/9/27 12:01
 */
public interface Constants {

    String SERVICE_NAME = "trading";

    int SETTLE_HOUR_OF_DAY = 4;
    String TRACE_SPAN_ID = "spanId";

    int DEFAULT_PRECISION = 16;
    int PRICE_PRECISION = 16;
    int SHOW_PRECISION = 8;
    int SWAP_PRICE_MAX_PRECISION = 12;
    int FUNDING_RATE_PRECISION = 8;
    int PRICE_CHANGE_RATE_PRECISION = 4;
    int RISK_RATE_PRECISION = 4;
    int PNL_PRECISION = 8;
    int USD_PRICE_PRECISION = 16;
    int USD_PRECISION = 2;
    int USD_LIQUID_PRECISION = 2;
    int LEVERAGE_PRECISION = 2;

    String SPOT_TYPE = "SPOT";
    String SWAP_TYPE = "SWAP";
    String MARGIN_TYPE = "MARGIN";

    int MAX_PEND_ORDER_NUM = 100;

    /**
     * 网关透传header
     */
    String X_GW_USER_HEADER = "x-gw-user";
    String X_GW_REQUEST_ID_HEADER = "x-gw-requestid";
    String L_OR_P = "l_or_p";
    String ORIGIN_CHANNEL = "origin_channel";

    String DEFAULT_COIN = "USD";

    String BASE_COIN = "USD";
    String BASE_QUOTE = "_USD";
    String BASE_COIN_AND_S = "USDⓢ";
    /**
     * web测默认的symbol面板配置
     */
    String DEFAULT_PAGE_SETTING_PRICE_CARD = "{\"new_layouts\":{\"lg\":[{\"w\":3,\"h\":6,\"x\":0,\"y\":0,\"i\":\"0\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":3,\"y\":0,\"i\":\"1\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":6,\"y\":0,\"i\":\"2\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":9,\"y\":0,\"i\":\"3\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":12,\"y\":0,\"i\":\"4\",\"moved\":false,\"static\":false,\"isResizable\":true}],\"md\":[{\"w\":3,\"h\":6,\"x\":0,\"y\":0,\"i\":\"0\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":3,\"y\":0,\"i\":\"1\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":6,\"y\":0,\"i\":\"2\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":9,\"y\":0,\"i\":\"3\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":0,\"y\":6,\"i\":\"4\",\"moved\":false,\"static\":false,\"isResizable\":true}],\"sm\":[{\"w\":3,\"h\":6,\"x\":0,\"y\":0,\"i\":\"0\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":3,\"y\":0,\"i\":\"1\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":6,\"y\":0,\"i\":\"2\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":0,\"y\":6,\"i\":\"3\",\"moved\":false,\"static\":false,\"isResizable\":true},{\"w\":3,\"h\":6,\"x\":3,\"y\":6,\"i\":\"4\",\"moved\":false,\"static\":false,\"isResizable\":true}]},\"new_data\":[{\"name\":\"BTC_USDT\"},{\"name\":\"BTC_USD\"},{\"name\":\"USDT_USD\"},{\"name\":\"ETH_USDT\"},{\"name\":\"ETH_BTC\"}]}";
    String[] DEFAULT_MARKET_FAVORITE_LIST = new String[]{"BTC_USD", "ETH_USD", "DOT_USD", "BNB_USD", "LINK_USD", "UNI_USD"};


    String[] DEFAULT_MIN_RECOMMEND_MARKET_LIST = new String[]{"BTC_USD", "ETH_USD"};

    Map<String, Integer> MARKET_QUOTE_INDEX_MAP = ImmutableMap.of("USD", 1, "USDT", 2, "BTC", 3);

    Set<String> MARKET_QUOTE_SET = ImmutableSet.of("USD", "USDT", "BTC");

    Integer PRICE_CHANGE_30D = 30;

    Integer PRICE_CHANGE_1Y = 365;

    /**
     * 某些时间端的价格浮动率，固定30天和365天
     */
    Integer[] PRICE_CHANGE_TIME_ARR = new Integer[]{PRICE_CHANGE_30D, PRICE_CHANGE_1Y};

    /**
     * symbol & coin 刷新间隔
     */
    long REFRESH_SYMBOL_COIN_CONFIG_INTERVAL_MILL = 5 * 60 * 1000;

    /**
     * 特殊用户持仓限额 刷新间隔
     */
    long REFRESH_POSITION_LIMIT_USER_INTERVAL_MILL = 10 * 60 * 1000;

    /**
     * symbol & coin 刷新间隔
     */
    long REFRESH_PRICE_CHECK_INTERVAL_MILL = 30 * 1000;

    /**
     * 清算发单的一个计算比例
     * 若abs（-USD）>= 某清算资产余额卖出USD计价 * 98% , 即此时按照Base卖出发单，发单数量 = CCY资产可用余额
     * 若abs（-USD）< 某清算资产余额卖出USD计价 * 98% , 则按照Quote发买入单，但是要限制base limit 不能超过余额，即按照负余额USD金额卖出CCY资产；
     */
    BigDecimal LIQUID_RATE =new BigDecimal("0.98");

    Map<String, Integer> BASE_LIQUIDATION_SEQUENCE_MAP = ImmutableMap.of("USDT", 1, "BTC", 2, "ETH", 3);

    /**
     * 交易设置的杠杆风险阈值，比如 关闭理财质押不能使风险超过此阈值，超过不允许关闭
     */
    BigDecimal TRADE_SETTING_MARGIN_RISK_THRESHOLD = new BigDecimal("0.9");
    /**
     * 交易设置止盈止损buffer
     */
    BigDecimal TRADE_SETTING_TASK_PROFIT_OR_STOP_LOSS_BUFFER = new BigDecimal("0.02");
    /**
     * 交易设置止盈max
     */
    BigDecimal TRADE_SETTING_TASK_PROFIT_MAX = new BigDecimal("100000000");
    /**
     * 交易设置止损max
     */
    BigDecimal TRADE_SETTING_STOP_LOSS_MAX = new BigDecimal("100000000");
    /**
     * 交易设置最大止损百分比
     */
    BigDecimal TRADE_SETTING_MAX_STOP_LOSS_PERCENTAGE = new BigDecimal("0.85");

    /**
     * 修改结算时间的时延buffer
     */
    Duration SETTLE_TIMEZONE_CHANGE_BUFFER_DURATION = Duration.ofMinutes(5);


    /**
     *  取消订单锁
     */
    String LOCK_CANCEL_ORDER ="cancel-order";
    /**
     * 强制减仓或者平仓现货锁
     */
    String LOCK_REDUCE_POSITION ="reduce-position";
    /**
     * 清算现货或余额
     */
    String LOCK_LIQUID_SPOT_BALANCE ="liquid-spot-balance";

    /**
     * +pnl 建仓抵扣率
     */
    BigDecimal PROFIT_OPEN_HAIR_CUT_AVAILABLE = new BigDecimal("0.95");

    /**
     * +pnl 清算抵扣率
     */
    BigDecimal PROFIT_OPEN_HAIR_CUT_LIQUIDATION = new BigDecimal("0.975");

    /**
     * -pnl 建仓抵扣率
     */
    BigDecimal LOSS_OPEN_HAIR_CUT_AVAILABLE = new BigDecimal("1");

    /**
     * -pnl 清算抵扣率
     */
    BigDecimal LOSS_OPEN_HAIR_CUT_LIQUIDATION = new BigDecimal("1");

    String IDK_COIN = "IDK";
}
