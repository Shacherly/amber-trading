package com.google.backend.trading.config.web;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author adam.wang
 * @date 2021/10/4 15:04
 */
public class CustomDateEditor extends PropertyEditorSupport {
    /**
     * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
     */
    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(new Date(Long.decode(text)));
    }

    /**
     * @see java.beans.PropertyEditorSupport#getAsText()
     */
    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        return (value != null ? String.valueOf(TimeUnit.MILLISECONDS.toSeconds(value.getTime())) : "");
    }

}
