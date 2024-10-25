package com.google.backend.trading.framework.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

/**
 * @author trading
 */
@Slf4j
@Component
public class KafkaSendResultHandler implements ProducerListener<Object, Object> {

    @Override
    public void onSuccess(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata) {
        log.info("msg send success, record = {}, offset = {}", toSimpleString(producerRecord), recordMetadata.offset());
    }

    @Override
    public void onError(ProducerRecord<Object, Object> producerRecord, Exception exception) {
        log.info("msg send error, record = {}, cause = {}", toSimpleString(producerRecord), ExceptionUtils.getRootCauseMessage(exception));
    }

    private String toSimpleString(ProducerRecord<Object, Object> producerRecord) {
        Headers headers = producerRecord.headers();
        StringBuilder sb = new StringBuilder("[ ");
        if (headers != null) {
            for (Header header : headers) {
                if (header.key() != null && header.value() != null) {
                    sb.append("RecordHeader(key = ").append(header.key()).append(", value = ").append(new String(header.value())).append("), ");
                }
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        String headersString = sb.toString();
        return "ProducerRecord(topic=" + producerRecord.topic() + ", partition=" + producerRecord.partition()
                + ", key=" + producerRecord.key() + ", value=" + producerRecord.value()
                + ", timestamp=" + producerRecord.timestamp() + ", headers=" + headersString + ")";
    }
}