package com.eqan.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eqan.web.model.User;
import com.eqan.web.service.JwtAuthenticationService;
import com.eqan.web.service.UserAccountService;

@RestController
@RequestMapping("/users")
public class UserAccountController {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountController.class);

    private UserAccountService userAccountService;
    private JwtAuthenticationService jwtAuthenticationService;

    @Autowired
    public UserAccountController(UserAccountService userAccountService,
            JwtAuthenticationService jwtAuthenticationService) {
        this.userAccountService = userAccountService;
        this.jwtAuthenticationService = jwtAuthenticationService;

    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to register user {}", user);
        return userAccountService.createUser(user);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Map<String, String>> getAuthenticationToken(@RequestBody User user) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to authenticate user {}", user);

        Map<String, String> responseMap = new HashMap<>();

        if (userAccountService.validate(user.getEmail(), user.getPassword())) {
            responseMap.put("token", jwtAuthenticationService.generateToken(user.getEmail()));
            return ResponseEntity.ok(responseMap);
        } else {
            responseMap.put("error", "Unauthorized");
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(responseMap);
        }
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("email") String email) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received request to get user {}", email);
        return userAccountService.getUserByEmail(email);
    }
}
