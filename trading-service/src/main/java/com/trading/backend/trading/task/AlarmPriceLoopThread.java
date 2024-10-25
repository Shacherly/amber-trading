package com.google.backend.trading.task;

import com.google.backend.trading.component.SymbolDomain;
import com.google.backend.trading.constant.RedisKeyConstants;
import com.google.backend.trading.dao.model.TradeUserAlarmPrice;
import com.google.backend.trading.model.user.AlarmTriggerType;
import com.google.backend.trading.service.PushMsgService;
import com.google.backend.trading.service.UserAlarmPriceService;
import com.google.backend.trading.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author david.chen
 * @date 2021/12/22 20:01
 */

@Slf4j
@Component
public class AlarmPriceLoopThread extends AbstractLoopThread {


    @Autowired
    private UserAlarmPriceService userAlarmPriceService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PushMsgService pushMsgService;

    public AlarmPriceLoopThread() {
        super(Duration.ofSeconds(10).toMillis());
    }

    @Override
    public void handle() throws Throwable {
        List<TradeUserAlarmPrice> userAlarmPriceList = userAlarmPriceService.queryAll();
        log.info("loop handler handle userAlarmPrice num = {}", userAlarmPriceList.size());
        for (TradeUserAlarmPrice tradeUserAlarmPrice : userAlarmPriceList) {
            if (log.isDebugEnabled()) {
                log.debug("AlarmPriceLoopThread|l2| AlarmPrice:{}", tradeUserAlarmPrice);
            }
            AlarmTriggerType alarmTriggerType = AlarmTriggerType.getByName(tradeUserAlarmPrice.getAlarmCompare());
            //获取当前价格 比较
            String symbol = tradeUserAlarmPrice.getSymbol();
            BigDecimal alarmPrice = tradeUserAlarmPrice.getAlarmPrice();
            SymbolDomain symbolDomain = SymbolDomain.nonNullGet(symbol);
            BigDecimal midPrice = symbolDomain.midPrice();
            if (AlarmTriggerType.isSatisfyAlarm(alarmPrice, alarmTriggerType, midPrice)) {
                String userAlarm24HKey = RedisKeyConstants.getUserAlarm24HKey(tradeUserAlarmPrice.getUid(), tradeUserAlarmPrice.getId());
                //setIfAbsent 仅当key不存在时，set才会生效
                Boolean setSuccess = redisTemplate.opsForValue().setIfAbsent(userAlarm24HKey, 1, 1, TimeUnit.DAYS);
                if (!setSuccess) {
                    //24H内不再提醒
                    continue;
                }
                String baseCoin = CommonUtils.getBaseCoin(tradeUserAlarmPrice.getSymbol());
                pushMsgService.alarmPriceToUser(tradeUserAlarmPrice.getUid(),
                        baseCoin,
                        tradeUserAlarmPrice.getAlarmPrice());
                log.info("AlarmPrice client push : {}", tradeUserAlarmPrice);
                //反转价格
                AlarmTriggerType reversal = AlarmTriggerType.reversal(alarmTriggerType);
                tradeUserAlarmPrice.setAlarmCompare(reversal.getCode());
                userAlarmPriceService.updateById(tradeUserAlarmPrice);
            }
        }
        Thread.sleep(Duration.ofSeconds(5).toMillis());
    }
}
