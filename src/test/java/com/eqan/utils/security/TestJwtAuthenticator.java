package com.eqan.utils.security;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eqan.web.security.JwtAuthenticationProvider;

import io.jsonwebtoken.ExpiredJwtException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestJwtAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(TestJwtAuthenticator.class);
    private static final String TEST_EMAIL = "test@email.com";
    
    @Autowired
    JwtAuthenticationProvider authenticator;
    
    @Test
    public void testGetToken() {
        String token = authenticator.generateToken(TEST_EMAIL);
        assertEquals("The token has 3 parts", 3, token.split("\\.").length);
    }
    
    @Test
    public void testAuthenticateToken() {
        String correctToken = authenticator.generateToken(TEST_EMAIL);
        String incorrectToken = authenticator.generateToken("wrong@wrong.com");
        
        boolean validToken = false;
        validToken = authenticator.authenticateToken(correctToken, TEST_EMAIL);
        assertTrue("JWT token must be valid", validToken);
        
        validToken = authenticator.authenticateToken(incorrectToken, TEST_EMAIL);
        assertFalse("JWT token must be invalid", validToken);
    }
    
    @Test(expected = ExpiredJwtException.class)
    public void testAuthenticateTokenWithExpiration() throws InterruptedException {
        Date expDate = new Date();
        expDate.setTime(expDate.getTime() + 500L);
        String expiredToken = authenticator.generateToken(TEST_EMAIL, expDate);
        
        if (LOG.isDebugEnabled())
            LOG.debug("Sleeping thread for 500 ms");
        Thread.sleep(500L);
        
        authenticator.authenticateToken(expiredToken, TEST_EMAIL);
    }

}
