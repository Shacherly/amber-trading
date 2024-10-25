package com.google.backend.trading.task;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.google.backend.trading.client.feign.KlineInfoClient;
import com.google.backend.trading.constant.Constants;
import com.google.backend.trading.dao.mapper.DefaultTradeTransactionMapper;
import com.google.backend.trading.dao.mapper.TradeTransactionAmountMapper;
import com.google.backend.trading.dao.model.TradeTransaction;
import com.google.backend.trading.dao.model.TradeTransactionAmount;
import com.google.backend.trading.dao.model.TradeTransactionExample;
import com.google.backend.trading.model.common.Response;
import com.google.backend.trading.model.kline.dto.PriceChange;
import com.google.backend.trading.model.trade.AssetStatus;
import com.google.backend.trading.model.trade.fee.RedisTradeDTO;
import com.google.backend.trading.util.CommonUtils;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 初始化前30用户交易数据到 数据库中
 *
 * @author david.chen
 * @date 2022/1/17 11:18
 */
@Component
@Slf4j
public class InitUserTradeAmount2DBTask {

    @Resource
    private DefaultTradeTransactionMapper defaultTradeTransactionMapper;

    @Autowired
    private KlineInfoClient klineInfoClient;

    @Resource
    private TradeTransactionAmountMapper tradeTransactionAmountMapper;

    /*
     * 初始化前30用户交易数据到DB中
     */
    @XxlJob("initUserTradeAmount2db")
    @Trace
    public void initUserTradeAmount2db() {
        log.info("初始化user-trade amount 2 db ");
        try {
            HashMap<String, List<PriceChange>> klineCache = new HashMap<>();
            int page = 1;
            int pageSize = 2000;
            PageHelper.startPage(page, pageSize, true);
            TradeTransactionExample example = new TradeTransactionExample();
            TradeTransactionExample.Criteria criteria = example.createCriteria();
            criteria.andAssetStatusEqualTo(AssetStatus.COMPLETED.name());
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(31);
            criteria.andCtimeBetween(Date.valueOf(start.toLocalDate()), Date.valueOf(end.toLocalDate()));
            while (true) {
                List<TradeTransaction> tradeTransactionList = defaultTradeTransactionMapper.selectByExample(example);
                if (CollectionUtils.isEmpty(tradeTransactionList)) {
                    break;
                }
                log.info("查询分页 page : {} , size : {}", page, tradeTransactionList.size());
                init2db(end, start, tradeTransactionList, klineCache);
                page++;
                PageHelper.startPage(page, pageSize, false);
            }
            log.info("结束初始化user-trade amount 2 db ");
        } catch (Exception e) {
            log.error("初始化user-trade amount 2 db 【失败】", e);
        }
    }

    private void init2db(LocalDateTime end, LocalDateTime start, List<TradeTransaction> tradeTransactionList, HashMap<String, List<PriceChange>> klineCache) {
        List<RedisTradeDTO> redisTradeDTOList = new ArrayList<>(tradeTransactionList.size());
        log.info("查询 start: {} end: {} ", start, end);
        List<TradeTransaction> usdCoinIsQuoteList = tradeTransactionList.stream().filter(
                        e -> CommonUtils.getQuoteCoin(e.getSymbol()).equals(Constants.BASE_COIN) ||
                                "USDT".equals(CommonUtils.getQuoteCoin(e.getSymbol())))
                .collect(Collectors.toList());
        log.info("查询USD or USDT 为Quote start: {} end: {} 【usdCoinIsQuoteList size】：{}", start, end, usdCoinIsQuoteList.size());
        Map<String, List<TradeTransaction>> usdCoinIsQuoteGroupTran = usdCoinIsQuoteList.stream().collect(Collectors.groupingBy(TradeTransaction::getSymbol));
        for (String symbol : usdCoinIsQuoteGroupTran.keySet()) {
            List<TradeTransaction> tradeTransactions = usdCoinIsQuoteGroupTran.get(symbol);
            for (TradeTransaction tradeTransaction : tradeTransactions) {
                java.util.Date ctime = tradeTransaction.getCtime();
                String uid = tradeTransaction.getUid();
                BigDecimal quoteQuantity = tradeTransaction.getQuoteQuantity();
                redisTradeDTOList.add(new RedisTradeDTO(quoteQuantity, ctime.getTime(), uid, tradeTransaction.getUuid()));
            }
        }

        String dayListStr = StringUtils.arrayToDelimitedString(IntStream.range(0, 31).boxed().toArray(), ",");
        List<TradeTransaction> usdCoinNOTQuoteList = tradeTransactionList.stream()
                .filter(e ->
                        !CommonUtils.getQuoteCoin(e.getSymbol()).equals(Constants.BASE_COIN)
                                && !"USDT".equals(CommonUtils.getQuoteCoin(e.getSymbol())))
                .collect(Collectors.toList());
        log.info("查询usd不是Quote的 start: {} end: {} 【usdCoinNOTQuoteList size】：{}", start, end, usdCoinNOTQuoteList.size());
        Map<String, List<TradeTransaction>> baseCoinGroupList = usdCoinNOTQuoteList.stream().collect(Collectors.groupingBy(e -> CommonUtils.getBaseCoin(e.getSymbol())));
        for (String coin : baseCoinGroupList.keySet()) {
            if (coin.equals(Constants.BASE_COIN)) {
                List<TradeTransaction> tradeTransactions = baseCoinGroupList.get(coin);
                tradeTransactions.forEach(tradeTransaction -> {
                    BigDecimal amount = tradeTransaction.getBaseQuantity();
                    String uid = tradeTransaction.getUid();
                    java.util.Date ctime = tradeTransaction.getCtime();
                    redisTradeDTOList.add(new RedisTradeDTO(amount, ctime.getTime(), uid, tradeTransaction.getUuid()));
                });
            } else {
                String symbol = coin + Constants.BASE_QUOTE;
                List<PriceChange> priceChangeList = klineCache.get(symbol);
                if (CollectionUtils.isEmpty(priceChangeList)) {
                    Response<List<PriceChange>> priceChangeRes = klineInfoClient.priceChange(symbol, dayListStr);
                    priceChangeList = priceChangeRes.getData();
                    klineCache.put(symbol, priceChangeList);
                }
                if (priceChangeList != null && !priceChangeList.isEmpty()) {
                    List<TradeTransaction> tradeTransactions = baseCoinGroupList.get(coin);
                    for (TradeTransaction tradeTransaction : tradeTransactions) {
                        String uid = tradeTransaction.getUid();
                        java.util.Date ctime = tradeTransaction.getCtime();
                        long day = DateUtil.betweenDay(ctime, CommonUtils.getNowTime(), false);
                        PriceChange priceChange = null;
                        try {
                            priceChange = priceChangeList.stream().filter(e -> (int) day == e.getDays()).findFirst().orElse(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (priceChange != null) {
                            BigDecimal price = priceChange.getPriceOld();
                            BigDecimal baseQuantity = tradeTransaction.getBaseQuantity();
                            BigDecimal amount = price.multiply(baseQuantity);
                            redisTradeDTOList.add(new RedisTradeDTO(amount, ctime.getTime(), uid, tradeTransaction.getUuid()));
                        }
                    }
                }
            }
        }
        List<TradeTransactionAmount> tradeTransactionAmounts = redisTradeDTOList.stream().map(e -> {
            TradeTransactionAmount tradeTransactionAmount = new TradeTransactionAmount();
            tradeTransactionAmount.setAmount(e.getAmount());
            tradeTransactionAmount.setUid(e.getUid());
            tradeTransactionAmount.setTransId(e.getTransId());
            tradeTransactionAmount.setCtime(CommonUtils.getNowTime(e.getTime()));
            return tradeTransactionAmount;
        }).collect(Collectors.toList());
        if (!tradeTransactionAmounts.isEmpty()) {
            tradeTransactionAmountMapper.batchInsert(tradeTransactionAmounts);
            log.info("tradeTransactionAmountMapper batchInsert list size :{}", tradeTransactionAmounts.size());
        }
    }

    public static void main(String[] args) {
        String dayListStr = StringUtils.arrayToDelimitedString(IntStream.range(0, 31).boxed().toArray(), ",");
        System.out.println(dayListStr);
    }
}
