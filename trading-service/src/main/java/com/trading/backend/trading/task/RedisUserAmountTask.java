package com.google.backend.trading.task;

import com.github.pagehelper.PageHelper;
import com.google.backend.trading.constant.RedisKeyConstants;
import com.google.backend.trading.dao.mapper.TradeTransactionAmountMapper;
import com.google.backend.trading.trace.annotation.TraceId;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 用户设置30天交易量
 *
 * @author david.chen
 * @date 2022/1/7 20:06
 */
@Slf4j
@Component
public class RedisUserAmountTask {
    @Autowired
    private RedissonClient redissonClient;
    @Resource
    private TradeTransactionAmountMapper tradeTransactionAmountMapper;

    /**
     * 设置用户
     */
    @TraceId
    @XxlJob("setRedisUserAmount")
    public void setRedisUserAmount() {
        log.info("开始set redis user 30d Amount");
        int page = 1;
        int size = 2000;
        while (true) {
            PageHelper.startPage(page++, size);
            LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime start = end.minusDays(31);
            //查询31天有交易的用户ID
            List<String> userIdList = tradeTransactionAmountMapper.getUserIdListByTime(start, end);
            if (CollectionUtils.isEmpty(userIdList)) {
                break;
            }
            //查询30天交易额
            start = end.minusDays(30);
            for (String uid : userIdList) {
                BigDecimal amount = tradeTransactionAmountMapper.sumAmountByUserIdAndCtime(uid, start, end);
                RBucket<BigDecimal> bucket = redissonClient.getBucket(RedisKeyConstants.get30DUserTradeAmountKey(uid));
                bucket.set(amount);
            }
        }
        log.info("结束set redis user 30d Amount");
    }
}
