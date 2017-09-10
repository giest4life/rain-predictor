package com.eqan.web.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

@Component("jwtAuthenticationProvider")
public class JwtAuthenticationProvider implements AuthenticationProvider{
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    public static final long HOUR = 3600L * 1000L;
    public static final long EXPIRE_HOUR = 1L;

    private String key;
    private JwtParser parser;

    @Autowired
    public JwtAuthenticationProvider(@Value("${JWT_KEY}") String key) {
        this.key = key;
        this.parser = Jwts.parser().setSigningKey(key);
    }

    public boolean authenticateToken(String token, String email) {
        boolean authenticated = false;
        try {
            authenticated = parser.parseClaimsJws(token).getBody().getSubject().equals(email);
        } catch (MalformedJwtException e) {
            if (LOG.isTraceEnabled())
                LOG.trace(e.getMessage());
        }
        return authenticated;
    }

    public String generateToken(String email) {
        if (LOG.isTraceEnabled())
            LOG.trace("Getting a JWT for {} with expiration in {} hour", email, EXPIRE_HOUR);
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() * EXPIRE_HOUR * HOUR);
        return generateToken(email, expiration);
    }

    public String generateToken(String email, Date expiration) {
        return Jwts.builder().setExpiration(expiration).setSubject(email).signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return String.class.equals(authentication);
    }
}
