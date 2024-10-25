package com.google.backend.trading.config.web;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Date;

/**
 * Date序列话为Long
 * @author adam.wang
 * @date 2021/9/30 15:51
 */
public class DateLongSerializer extends StdSerializer<Date> {

    public final static DateLongSerializer instance = new DateLongSerializer();

    public DateLongSerializer() {
        super(Date.class);
    }
    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if(value==null){
            gen.writeNumber(0L);
        }else{
            gen.writeNumber(value.getTime());
        }
    }


}
