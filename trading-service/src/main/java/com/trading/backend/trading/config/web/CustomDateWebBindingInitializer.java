package com.google.backend.trading.config.web;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;

import java.util.Date;

/**
 * @author adam.wang
 * @date 2021/10/4 15:05
 */
public class CustomDateWebBindingInitializer implements WebBindingInitializer {

    @Override
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.registerCustomEditor(Date.class, new CustomDateEditor());
    }
}
