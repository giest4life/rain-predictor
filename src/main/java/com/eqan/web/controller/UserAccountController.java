package com.eqan.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.User;
import com.eqan.web.service.UserAccountService;

@RestController
public class UserAccountController {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountController.class);

    private UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to register user {}", user);
        return userAccountService.createUser(user);
    }

    @GetMapping("/users/user")
    public User getUser(@RequestParam("email") String email) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to get user {}", email);
        return userAccountService.getUserByEmail(email);
    }

}
