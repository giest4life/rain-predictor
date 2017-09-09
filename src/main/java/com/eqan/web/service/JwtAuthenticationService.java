package com.eqan.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eqan.web.security.JwtAuthenticator;

@Service("jwtAuthenticationService")
public class JwtAuthenticationService {
    
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationService.class);
    
    private JwtAuthenticator authenticator;
    @Autowired
    public JwtAuthenticationService(JwtAuthenticator authenticator) {
        this.authenticator = authenticator;
        if (LOG.isDebugEnabled())
            LOG.debug("Constructed JwtAuthenticationService");
    }
    
    public boolean authenticateToken(String token, String email) {
        return authenticator.authenticateToken(token, email);
    }
    
    public String generateToken(String email) {
        return authenticator.generateToken(email);
    }
}
