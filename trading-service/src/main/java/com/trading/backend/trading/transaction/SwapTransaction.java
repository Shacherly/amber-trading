package com.google.backend.trading.transaction;

import com.google.backend.trading.dao.mapper.DefaultTradeSwapOrderMapper;
import com.google.backend.trading.dao.model.TradeSwapOrder;
import com.google.backend.trading.dao.model.TradeSwapOrderExample;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.service.TradeTransactionService;
import com.google.backend.trading.util.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Component
public class SwapTransaction {

    @Resource
    private DefaultTradeSwapOrderMapper defaultTradeSwapOrderMapper;

    @Resource
    private TradeTransactionService tradeTransactionService;


    @Transactional(rollbackFor = Throwable.class)
    public void insertTransactionAndUpdateOrder(TradeTransaction transaction, TradeSwapOrder order) {
        order.setMtime(CommonUtils.getNowTime());
        if (order.getId() != null && order.getId() > 0) {
            defaultTradeSwapOrderMapper.updateByPrimaryKeySelective(order);
        } else {
            TradeSwapOrderExample example = new TradeSwapOrderExample();
            example.createCriteria().andUuidEqualTo(order.getUuid());
            defaultTradeSwapOrderMapper.updateByExampleSelective(order, example);
        }
        tradeTransactionService.insert(transaction);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateTransactionAndUpdateOrder(TradeTransaction transaction, TradeSwapOrder order) {
        Date nowTime = CommonUtils.getNowTime();
        order.setMtime(nowTime);
        transaction.setMtime(nowTime);
        if (order.getId() != null && order.getId() > 0) {
            defaultTradeSwapOrderMapper.updateByPrimaryKeySelective(order);
        } else {
            TradeSwapOrderExample example = new TradeSwapOrderExample();
            example.createCriteria().andUuidEqualTo(order.getUuid());
            defaultTradeSwapOrderMapper.updateByExampleSelective(order, example);
        }
        tradeTransactionService.updateTransactionById(transaction);
    }

}
