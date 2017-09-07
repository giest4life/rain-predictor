package com.eqan.web.controller;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Base64;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.eqan.utils.dao.PostgreSQL;
import com.eqan.utils.json.UserSerializer;
import com.eqan.web.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserAccountControllerIT {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountControllerIT.class);
    private static String URL = "http://localhost:8080/rain-predictor/api/{endpoint}";
    private static final String REGISTER_ENDPOINT = "register";
    private static final String SIGN_IN_ENDPOINT = "signin";
    private static ObjectMapper MAPPER;
    private static SimpleModule MODULE;

    @BeforeClass
    public static void setUpBeforeClass() {

        MAPPER = new ObjectMapper();
        MODULE = new SimpleModule();
        MODULE.addSerializer(User.class, new UserSerializer());
        MAPPER.registerModule(MODULE);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PostgreSQL dbUtils = new PostgreSQL();

    private User testUser = new User("jill@test.com", "password");

    private String getAuthorizationHeaderForUser(User user) {
        String credentials = user.getEmail() + ":" + user.getPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    private HttpEntity<String> getHttpEntityForAuthorization(String authorization) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        return new HttpEntity<String>(headers);
    }

    private ResponseEntity<User> sendJsonPostRequest(String jsonUser, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonUser, headers);

        if (LOG.isDebugEnabled())
            LOG.debug("Sending user {} to {}", jsonUser, URL);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(URL, HttpMethod.POST, entity, User.class, endpoint);
    }
    
    private ResponseEntity<User> getResponseToUserGetRequest(String URL, String endpoint, HttpEntity<String> entity) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(URL, HttpMethod.GET, entity, User.class,
                SIGN_IN_ENDPOINT);
    }

    @Before
    public void setUp() throws IOException {
        dbUtils.setJdbcTemplate(jdbcTemplate);
        dbUtils.truncateUserTable();
        dbUtils.addUsers();
    }

    @Test
    public void testRegisterController() throws JsonProcessingException {

        // Need to use custom mapper as default Jackson will ignore password on
        // serialization
        String jsonUser = MAPPER.writeValueAsString(testUser);

        ResponseEntity<User> response = sendJsonPostRequest(jsonUser, REGISTER_ENDPOINT);
        assertEquals("HTTP Status must be 201", HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();

        assertNotNull("The response user must not be null", responseUser);
        assertEquals("The response user must have same email", testUser.getEmail(), responseUser.getEmail());

    }

    @Test
    public void testSignInController() throws JsonProcessingException {
        User user = dbUtils.getTestUsers().get(0);
        String authorization = getAuthorizationHeaderForUser(user);

        if (LOG.isDebugEnabled())
            LOG.debug("Testing {} with Authorization header value {}", SIGN_IN_ENDPOINT, authorization);

        HttpEntity<String> entity = getHttpEntityForAuthorization(authorization);

        ResponseEntity<User> response = getResponseToUserGetRequest(URL, SIGN_IN_ENDPOINT, entity);
        assertEquals("Response code must be 200", HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testSignInControllerInvalid() throws JsonProcessingException {
        User user = dbUtils.getTestUsers().get(10);
        user.setPassword("Clearly wrong");
        String authorization = getAuthorizationHeaderForUser(user);

        HttpEntity<String> entity = getHttpEntityForAuthorization(authorization);
        try {
            getResponseToUserGetRequest(URL, SIGN_IN_ENDPOINT, entity);
        } catch (HttpClientErrorException e) {
            assertEquals("Response code must be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

}
