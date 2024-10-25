package com.google.backend.trading.framework.kafka.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.backend.trading.dao.model.TradeFeeDefaultConfig;
import com.google.backend.trading.framework.kafka.KafkaConsumerService;
import com.google.backend.trading.mapstruct.user.TradeFeeConfigStruct;
import com.google.backend.trading.model.trade.AlgoTradeAmountDTO;
import com.google.backend.trading.model.swap.dto.AipSwapOrderPlaceReqDTO;
import com.google.backend.trading.model.trade.fee.AceTradeFeeConfigDTO;
import com.google.backend.trading.service.SwapService;
import com.google.backend.trading.service.TradeAssetService;
import com.google.backend.trading.service.TradeFeeConfigService;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author david.chen
 * @date 2021/12/30 11:35
 */
@Slf4j
@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TradeFeeConfigService tradeFeeConfigService;
    @Resource
    private TradeFeeConfigStruct tradeFeeConfigStruct;
    @Autowired
    private SwapService swapService;
    @Autowired
    private TradeAssetService tradeAssetService;

    @KafkaListener(topics = "${kafka.topic.aceup-tradefee-config-consumer}")
    @Override
    public void aceUpTradeFeeConfig(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String msg = kafkaMessage.get().toString();
            log.info("kafkaConsumer | receive from aceup trade-fee-config, msg:{}", msg);
            try {
                AceTradeFeeConfigDTO res = objectMapper.readValue(msg, AceTradeFeeConfigDTO.class);
                if (res.isDefault()) {
                    //default insert or update
                    TradeFeeDefaultConfig tradeFeeDefaultConfig = tradeFeeConfigStruct.tradeFeeConfigData2TradeFeeDefaultConfig(res.getData());
                    log.info("kafkaConsumer | receive from aceup 【isDefault】 trade-fee-config , config:{}", tradeFeeDefaultConfig);
                    tradeFeeConfigService.insertOrUpdateDefault(tradeFeeDefaultConfig);
                } else if (res.isDelUser()) {
                    log.info("kafkaConsumer | receive from aceup 【isDelUser】 trade-fee-config , msg:{}", msg);
                    tradeFeeConfigService.delUserTradeFeeConfig(res.getData().getUid());
                } else if (res.isUpdateFundingCost()) {
                    log.info("kafkaConsumer | receive from aceup 【isUpdateFundingCost】 trade-fee-config , msg:{}", msg);
                    tradeFeeConfigService.updateFundingCostConfig(res.getData().getUid(), res.getData().getFundingCostEnable());
                    tradeFeeConfigService.delUserTradeFeeConfigWithOutFundingDisable(res.getData().getUid());
                } else {
                    //新增or修改用户信息
                    log.info("kafkaConsumer | receive from aceup 【insertOrUpdateUser】 trade-fee-config , msg:{}", msg);
                    tradeFeeConfigService.insertOrUpdateUser(res.getData());
                }
            } catch (Exception e) {
                AlarmLogUtil.alarm("kafkaConsumer | msg:{}, aceup trade-fee-config exception:", msg, e);
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.aip-swap-place-order-consumer}")
    @Override
    public void aipSwapOrderPlace(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String msg = kafkaMessage.get().toString();
            log.info("kafkaConsumer | receive from aip-swap-order-place, msg:{}", msg);
            try {
                AipSwapOrderPlaceReqDTO req = objectMapper.readValue(msg, AipSwapOrderPlaceReqDTO.class);
                try {
                    swapService.saveForAip(req);
                } catch (Exception e) {
                    AlarmLogUtil.alarm("kafkaConsumer | msg:{} , req : {}, swapService.aipOrderPlace 非预期异常 :{}", msg, req, ExceptionUtils.getRootCauseMessage(e), e);
                }
            } catch (Exception e) {
                AlarmLogUtil.alarm("kafkaConsumer | msg:{}, aip-swap-order-place exception:", msg, e);
            }
        }
    }

    @KafkaListener(topics = "${kafka.topic.algo-transaction-statistics-consumer}")
    @Override
    public void algoUserTradeAmount(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            String msg = kafkaMessage.get().toString();
            log.info("kafkaConsumer | receive from algo user-trade-amount, msg:{}", msg);
            try {
                AlgoTradeAmountDTO res = objectMapper.readValue(msg, AlgoTradeAmountDTO.class);
                tradeAssetService.setUserAmountUsd2DB(res.getUid(), res.getCoin(), res.getAmount(), res.getTradeId());
            } catch (Exception e) {
                log.error("kafkaConsumer | msg:{}, algo user-trade-amount exception:", msg, e);
            }
        }
    }
}
