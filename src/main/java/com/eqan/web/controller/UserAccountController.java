package com.eqan.web.controller;

import java.util.Base64;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.User;
import com.eqan.web.service.UserAccountService;

@RestController
public class UserAccountController {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountController.class);

    @Autowired
    UserAccountService userAccountService;

//    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to register user {}", user);
        return userAccountService.createUser(user);
    }

//    @GetMapping("/signin")
    public User signIn(@RequestHeader("Authorization") String authorization) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received request for /signin with header {}", authorization);
        }

        if (authorization != null && !authorization.contains(" ")) {
            throw new IllegalArgumentException("Authorization header is malformed");
        }

        String[] encodedCredentials = authorization.split(" ");
        if (encodedCredentials.length < 2 && !"Basic".equals(encodedCredentials[0])) {
            throw new IllegalArgumentException("Authorization header is malformed");
        }

        String[] credentials = new String(Base64.getDecoder().decode(encodedCredentials[1])).split(":");

        if (credentials.length < 2) {
            throw new IllegalArgumentException("Authorization header is malformed");
        }

        return userAccountService.signIn(credentials[0], credentials[1]);
    }
}
