package com.google.backend.trading.config.web;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.backend.trading.util.AlarmLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;

/**
 * @author trading
 * @date 2021/11/19 14:59
 */
@Slf4j
public class I18nConvertSerializer extends StdSerializer<String> {

	public I18nConvertSerializer() {
		super(String.class);
	}

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		if (value == null) {
			gen.writeNull();
		} else if (value.trim().isEmpty()) {
			gen.writeString(value);
		} else {
			MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
			String message;
			try {
				message = messageSource.getMessage(value, null, LocaleContextHolder.getLocale());
			} catch (NoSuchMessageException e) {
				log.error("i18n not found message, cause = {}", ExceptionUtils.getRootCauseMessage(e), e);
				message = "";
			}
			gen.writeString(message);
		}
	}
}
