package com.google.backend.trading.config.web;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author adam.wang
 * @date 2021/10/3 12:18
 */
@Slf4j
public class StringUpperCaseDeserializer extends JsonDeserializer<String> {
    public final static StringUpperCaseDeserializer instance = new StringUpperCaseDeserializer();

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        return text.toUpperCase();
    }
}
