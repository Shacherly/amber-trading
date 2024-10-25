package com.google.backend.trading.framework.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @author david.chen
 * @date 2021/12/30 11:35
 */
public interface KafkaConsumerService {


    /**
     * aceup 交易费率
     *
     * @param record
     */
    void aceUpTradeFeeConfig(ConsumerRecord<?, ?> record);


    /**
     * AIP 定投下单
     *
     * @param record
     */
    void aipSwapOrderPlace(ConsumerRecord<?, ?> record);
    /**
     * algo 算法每笔交易额
     *
     * @param record
     */
    void algoUserTradeAmount(ConsumerRecord<?, ?> record);
}
