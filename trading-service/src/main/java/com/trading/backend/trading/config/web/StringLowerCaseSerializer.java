package com.google.backend.trading.config.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * String 序列化为小写
 * @author adam.wang
 * @date 2021/9/30 15:51
 */
public class StringLowerCaseSerializer extends StdSerializer<String> {

    public final static StringLowerCaseSerializer instance = new StringLowerCaseSerializer();

    public StringLowerCaseSerializer() {
        super(String.class);
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (value == null){
            gen.writeNull();
        }else{
            gen.writeString(value.toLowerCase());
        }
    }


}
