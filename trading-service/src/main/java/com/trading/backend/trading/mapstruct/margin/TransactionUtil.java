package com.google.backend.trading.mapstruct.margin;

import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.model.trade.Direction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author adam.wang
 * @date 2021/10/26 11:32
 */
public class TransactionUtil {


    public static BigDecimal getOpenPrice(TradeTransaction tradeTransaction){
        BigDecimal openPrice = tradeTransaction.getPrice();

        BigDecimal b = tradeTransaction.getPnl().divide(tradeTransaction.getBaseQuantity(), Constants.PRICE_PRECISION, RoundingMode.DOWN);
        if(Direction.isBuy(tradeTransaction.getDirection())){
            openPrice = openPrice.add(b);
        }else{
            openPrice = openPrice.subtract(b);
        }
        return openPrice;
    }
}
