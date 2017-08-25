package com.eqan.web.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtAuthenticator {
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticator.class);
    public static final long HOUR = 3600L * 1000L;
    public static final long EXPIRE_HOUR = 1L;

    private String key;
    private JwtParser parser;

    @Autowired
    public JwtAuthenticator(@Value("${JWT_KEY}") String key) {
        this.key = key;
        this.parser = Jwts.parser().setSigningKey(key);
    }

    public String getToken(String email) {
        if (LOG.isTraceEnabled())
            LOG.trace("Getting a JWT for {} with expiration in {} hour", email, EXPIRE_HOUR);
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() * EXPIRE_HOUR * HOUR);
        return getToken(email, expiration);
    }

    public String getToken(String email, Date expiration) {
        return Jwts.builder().setExpiration(expiration).setSubject(email).signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public boolean authenticateToken(String token, String email) {
        return parser.parseClaimsJws(token).getBody().getSubject().equals(email);
    }
}
