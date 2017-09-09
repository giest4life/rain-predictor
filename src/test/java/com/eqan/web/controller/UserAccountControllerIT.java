package com.eqan.web.controller;

import static org.junit.Assert.*;

import java.io.IOException;

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
    private static String URL = "http://localhost:8080/rain-predictor/users/{endpoint}";
    private static String USER_ENDPOINT = "user";
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


    private ResponseEntity<User> sendJsonPostRequest(String jsonUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonUser, headers);

        if (LOG.isDebugEnabled())
            LOG.debug("Sending user {} to {}{}", jsonUser, URL, USER_ENDPOINT);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(URL, HttpMethod.POST, entity, User.class, USER_ENDPOINT);
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

        ResponseEntity<User> response = sendJsonPostRequest(jsonUser);
        assertEquals("HTTP Status must be 201", HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();

        assertNotNull("The response user must not be null", responseUser);
        assertEquals("The response user must have same email", testUser.getEmail(), responseUser.getEmail());

    }

}
