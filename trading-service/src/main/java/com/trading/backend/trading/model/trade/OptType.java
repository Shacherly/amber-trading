package com.google.backend.trading.model.trade;

import com.google.backend.trading.dao.model.TradeTransaction;

/**
 * @author adam.wang
 * @date 2021/10/20 15:34
 */
public class OptType {

    public static String getOptType(TradeTransaction transaction){
        TransactionType byName = TransactionType.getByName(transaction.getType());
        //如果是强平前端需要展示该信息
        if (SourceType.FORCE_CLOSE.getName().equals(transaction.getSource())||TransactionType.SPOT.getCode().equals(byName.getCode()) || TransactionType.SWAP.getCode().equals(byName.getCode())) {
            SourceType sourceType = SourceType.getByName(transaction.getSource());
            if (null == sourceType) {
                return null;
            }
            return sourceType.getCode();
        }
        return byName.getName();
    }
}
