package com.google.backend.trading.config.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.backend.trading.model.common.jackson.BigDecimalStringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/9/30 15:52
 */
@Configuration
public class JacksonConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        SimpleModule bigDecimalModule = new SimpleModule();
        //序列化将BigDecimal转String类型
        bigDecimalModule.addSerializer(BigDecimal.class, BigDecimalStringSerializer.instance);
        bigDecimalModule.addKeySerializer(BigDecimal.class, BigDecimalStringSerializer.instance);

        //序列化将Date转Long类型
        SimpleModule dateModule = new SimpleModule();
        dateModule.addSerializer(Date.class,DateLongSerializer.instance);
        dateModule.addKeySerializer(Date.class,DateLongSerializer.instance);
        dateModule.addDeserializer(Date.class,DateLongDeserializer.instance);
        // 注册转换器
        objectMapper.registerModules(dateModule,bigDecimalModule);
        //json不返回null的字段
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

}
