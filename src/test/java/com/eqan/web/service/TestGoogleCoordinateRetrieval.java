package com.eqan.web.service;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestGoogleCoordinateRetrieval {
    private static final Logger LOG = LoggerFactory.getLogger(TestGoogleCoordinateRetrieval.class);
    private static final String TEST_GOOGLE_API_KEY = "TEST_GOOGLE_API_KEY";
    
    @Value("${GOOGLE_API_KEY}")
    private String API_KEY;
    
    @Test
    public void googleAPIKeyTest() {
        assertEquals("The Test API keys must match", TEST_GOOGLE_API_KEY, API_KEY);
    }
    
}
