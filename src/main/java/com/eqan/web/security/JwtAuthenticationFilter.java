package com.eqan.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    
    private JwtAuthenticationProvider jwtAuthProvider;
    
    public JwtAuthenticationFilter(JwtAuthenticationProvider jwtAuthProvider) {
        this.jwtAuthProvider = jwtAuthProvider;
    }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (LOG.isDebugEnabled())
            LOG.debug("Authenticating request for {}", request.getParameter("email"));

        String header = request.getHeader("Authorization");
        
        if (header == null || !header.startsWith("Bearer ")) {
            if (LOG.isDebugEnabled())
                LOG.debug("Rejected request because header is malformed or not present");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        
        boolean authenticated = jwtAuthProvider.authenticateToken(header.substring(7), request.getParameter("email"));
        
        if (!authenticated) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            if (LOG.isDebugEnabled())
                LOG.debug("Rejected request user could not be authenticated");
            return;
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Authentication successfull for {}", request.getParameter("email"));
        chain.doFilter(request, response);
    }

}
