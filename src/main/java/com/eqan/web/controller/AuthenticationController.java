package com.eqan.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.exceptions.NotAuthorizedException;
import com.eqan.web.model.User;
import com.eqan.web.service.JwtAuthenticationService;
import com.eqan.web.service.UserAccountService;

@RestController
public class AuthenticationController {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);
    
    private UserAccountService userAccountService;
    private JwtAuthenticationService jwtAuthenticationService;
    
    @Autowired
    public AuthenticationController(UserAccountService userAccountService,
            JwtAuthenticationService jwtAuthenticationService) {
        this.userAccountService = userAccountService;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @PostMapping("/authenticate")
    public String getAuthenticationToken(@RequestBody User user) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to authenticate user {}", user);

        if (userAccountService.validate(user.getEmail(), user.getPassword())) {
            return jwtAuthenticationService.generateToken(user.getEmail());
        } else {
            throw new NotAuthorizedException(String.format("User %s could not be authenticated", user.getEmail()));
        }
    }
}
