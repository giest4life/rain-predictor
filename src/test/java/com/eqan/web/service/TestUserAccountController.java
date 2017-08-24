package com.eqan.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.springframework.web.client.RestTemplate;

import com.eqan.utils.dao.PostgreSQL;
import com.eqan.utils.json.UserSerializer;
import com.eqan.web.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestUserAccountController {

    private static final Logger LOG = LoggerFactory.getLogger(TestUserAccountController.class);
    private static String URL = "http://localhost:8080/rain-predictor/api/{endpoint}";
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String SIGN_IN_ENDPOINT = "/signin";
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

    private ResponseEntity<User> sendJsonPostRequest(String jsonUser, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonUser, headers);

        if (LOG.isDebugEnabled())
            LOG.debug("Sending user {} to {}", jsonUser, URL);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(URL, HttpMethod.POST, entity, User.class, endpoint);
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

    public void testSignInController() throws JsonProcessingException {
        User testUser = dbUtils.getTestUsers().get(0);
        String credentials = testUser.getEmail() + ":" + testUser.getPassword();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        String authorization = "Basic " + encodedCredentials;

        if (LOG.isDebugEnabled())
            LOG.debug("Testing {} with Authorization header value {}", SIGN_IN_ENDPOINT, authorization);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorization);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<User> response = restTemplate.exchange(URL, HttpMethod.GET, entity, User.class,
                SIGN_IN_ENDPOINT);
        assertEquals("Response code must be 200", HttpStatus.OK, response.getStatusCode());
    }

}
