package com.eqan.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URI;

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
import org.springframework.web.util.UriComponentsBuilder;

import com.eqan.utils.dao.PostgreSQL;
import com.eqan.utils.json.UserSerializer;
import com.eqan.web.model.User;
import com.eqan.web.service.JwtAuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class UserAccountControllerIT {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountControllerIT.class);
    private static String URL = "http://localhost:8080/rain-predictor/users/{endpoint}";
    private static String REGISTER_USER = "register";
    private static String GET_USER = "user";
    private static ObjectMapper MAPPER;
    private static SimpleModule MODULE;

    private RestTemplate restTemplate = new RestTemplate();

    @BeforeClass
    public static void setUpBeforeClass() {

        MAPPER = new ObjectMapper();
        MODULE = new SimpleModule();
        MODULE.addSerializer(User.class, new UserSerializer());
        MAPPER.registerModule(MODULE);
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JwtAuthenticationService authService;

    private PostgreSQL dbUtils = new PostgreSQL();

    private User testUser = new User("jill@test.com", "password");

    private ResponseEntity<User> sendJsonPostRequest(String jsonUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonUser, headers);

        if (LOG.isDebugEnabled())
            LOG.debug("Sending user {} to {}{}", jsonUser, URL, REGISTER_USER);

        return restTemplate.exchange(URL, HttpMethod.POST, entity, User.class, REGISTER_USER);
    }

    @Before
    public void setUp() throws IOException {
        dbUtils.setJdbcTemplate(jdbcTemplate);
        dbUtils.truncateUserTable();
        dbUtils.addUsers();
    }

    @Test
    public void testRegisterUser() throws JsonProcessingException {

        // Need to use custom mapper as default Jackson will ignore password on
        // serialization
        String jsonUser = MAPPER.writeValueAsString(testUser);

        ResponseEntity<User> response = sendJsonPostRequest(jsonUser);
        assertEquals("HTTP Status must be 201", HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();

        assertNotNull("The response user must not be null", responseUser);
        assertEquals("The response user must have same email", testUser.getEmail(), responseUser.getEmail());

    }

    @Test
    public void testGetUserWithMissingAuthorization() {
        User user = dbUtils.getTestUsers().get(0);
        URI uri = UriComponentsBuilder.fromUriString(URL).queryParam("email", user.getEmail()).buildAndExpand(GET_USER)
                .toUri();
        try {
            restTemplate.getForEntity(uri, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Response code should be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void testGetUserWithMalformedAuthorization() {
        URI uri = UriComponentsBuilder.fromUriString(URL).queryParam("email", "giest4life@gmail.com")
                .buildAndExpand(GET_USER).toUri();
        String token = "Bearer adfadfasdf";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            restTemplate.exchange(uri, HttpMethod.GET, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Response code should be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void testGetUserWithWrongToken() {
        User user1 = dbUtils.getTestUsers().get(0);
        
        
        String tokenUser1 = authService.generateToken(user1.getEmail());
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenUser1);
        HttpEntity<String> entity = new HttpEntity<String>(headers);
        
        User user2 = dbUtils.getTestUsers().get(1);
        URI uri = UriComponentsBuilder.fromUriString(URL).queryParam("email",user2.getEmail()).buildAndExpand(GET_USER)
                .toUri();
        try {
            restTemplate.exchange(uri, HttpMethod.GET, entity, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Response code should be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void testGetUserWithAuthorization() throws JsonProcessingException {
        User user = dbUtils.getTestUsers().get(0);
        String token = authService.generateToken(user.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        URI uri = UriComponentsBuilder.fromUriString(URL).queryParam("email", user.getEmail()).buildAndExpand(GET_USER)
                .toUri();

        ResponseEntity<User> response = restTemplate.exchange(uri, HttpMethod.GET, entity, User.class);

        assertEquals("Returned email must match", user.getEmail(), response.getBody().getEmail());
        assertEquals("Password must not be returned", null, response.getBody().getPassword());

    }

}
