package com.eqan.web.controller;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping("/hello")
    public String hello(Locale locale) {
        LOG.info("Received request for hello controller");
        locale.getVariant();

        return String.format("Country code: %s, Country name %s, Language: %s", locale.getCountry(),
                locale.getDisplayCountry(), locale.getDisplayLanguage());
    }

    @PostConstruct
    public void sayHello() {
        if (LOG.isInfoEnabled()) {
            LOG.info("Constructed hello controller");
        }
    }

}
