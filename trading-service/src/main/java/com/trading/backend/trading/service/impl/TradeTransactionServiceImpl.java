package com.google.backend.trading.service.impl;

import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.service.TradeTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author david.chen
 * @date 2021/12/17 17:22
 */
@Service
@Slf4j
public class TradeTransactionServiceImpl implements TradeTransactionService {
    @Autowired
    private DefaultTradeTransactionMapper tradeTransactionMapper;


    @Override
    public TradeTransaction queryAllByTransId(String transId) {
        TradeTransactionExample example = new TradeTransactionExample();
        example.createCriteria().andUuidEqualTo(transId);
        List<TradeTransaction> tradeTransactions = tradeTransactionMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(tradeTransactions)) {
            return tradeTransactions.get(0);
        }
        return null;
    }

    @Override
    public boolean insert(TradeTransaction transaction) {
        return tradeTransactionMapper.insertSelective(transaction) > 0;
    }


    @Override
    public boolean updateTransactionById(TradeTransaction transaction) {
        TradeTransactionExample example = new TradeTransactionExample();
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        if (null != transaction.getId()) {
            criteria.andIdEqualTo(transaction.getId());
        } else {
            criteria.andUuidEqualTo(transaction.getUuid());
        }
        return tradeTransactionMapper.updateByExampleSelective(transaction, example) > 0;
    }

    @Override
    public TradeTransaction queryNearAssetExceptionByOrderId(String orderId) {
        TradeTransactionExample example = new TradeTransactionExample();
        example.setOrderByClause("ctime desc limit 1");
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<TradeTransaction> tradeTransactions = tradeTransactionMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(tradeTransactions)) {
            return tradeTransactions.get(0);
        }
        return null;
    }

    @Override
    public List<TradeTransaction> queryAllByOrderId(String orderId) {
        TradeTransactionExample example = new TradeTransactionExample();
        example.setOrderByClause("mtime desc");
        TradeTransactionExample.Criteria criteria = example.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        return tradeTransactionMapper.selectByExample(example);
    }

}
