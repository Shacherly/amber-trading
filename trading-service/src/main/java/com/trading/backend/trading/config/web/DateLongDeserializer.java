package com.google.backend.trading.config.web;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/10/3 12:18
 */
@Slf4j
public class DateLongDeserializer  extends JsonDeserializer<Date> {
    public final static DateLongDeserializer instance = new DateLongDeserializer();

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        return new Date(Long.valueOf(text));
    }
}
