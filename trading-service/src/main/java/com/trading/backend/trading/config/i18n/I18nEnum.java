package com.google.backend.trading.config.i18n;

import lombok.Getter;

/**
 * @author david.chen
 * @date 2021/11/9 19:05
 * 国际化properties key
 */
@Getter
public enum I18nEnum {

    /**
     * 兑换备注多语言key
     */
    TRADING_SWAP_ORDER_MEMO_TIMEOUT("trading_swap_order_memo_timeout"),
    TRADING_SWAP_ORDER_MEMO_NO_BALANCE("trading_swap_order_memo_no_balance"),
    TRADING_SWAP_ORDER_MEMO_PRICE_OFFSET_OVER("trading_swap_order_memo_price_offset_over"),

    ;

    private final String key;

    I18nEnum(String key) {
        this.key = key;
    }
}
