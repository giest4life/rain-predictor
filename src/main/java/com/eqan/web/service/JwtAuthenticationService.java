package com.eqan.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eqan.web.security.JwtAuthenticationProvider;

@Service("jwtAuthenticationService")
public class JwtAuthenticationService {
    
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationService.class);
    
    private JwtAuthenticationProvider authenticator;
    @Autowired
    public JwtAuthenticationService(JwtAuthenticationProvider authenticator) {
        this.authenticator = authenticator;
        if (LOG.isTraceEnabled())
            LOG.trace("Constructed JwtAuthenticationService");
    }
    
    public boolean authenticateToken(String token, String email) {
        return authenticator.authenticateToken(token, email);
    }
    
    public String generateToken(String email) {
        return authenticator.generateToken(email);
    }
}
