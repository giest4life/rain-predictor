package com.eqan.web.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

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
@SuppressWarnings("unchecked")
public class UserAccountControllerIT {

    private static final Logger LOG = LoggerFactory.getLogger(UserAccountControllerIT.class);
    private static String URL = "http://localhost:8080/rain-predictor/users/{endpoint}";
    private static String REGISTER_USER = "register";
    private static String GET_USER = "user";
    private static String AUTHENTICATE_USER = "authenticate";
    private static ObjectMapper MAPPER;
    private static SimpleModule MODULE;
    
    @BeforeClass
    public static void setUpBeforeClass() {

        MAPPER = new ObjectMapper();
        MODULE = new SimpleModule();
        MODULE.addSerializer(User.class, new UserSerializer());
        MAPPER.registerModule(MODULE);
    }

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private JwtAuthenticationService authService;

    private PostgreSQL dbUtils = new PostgreSQL();

    private User testUser = new User("jill@test.com", "password");

    private <T> ResponseEntity<T> sendJsonPostRequest(String jsonUser, Class<T> responseType, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(jsonUser, headers);

        if (LOG.isDebugEnabled())
            LOG.debug("Sending user {} to {}{}", jsonUser, URL, endpoint);

        return restTemplate.exchange(URL, HttpMethod.POST, entity, responseType, endpoint);
    }

    @Before
    public void setUp() throws IOException {
        dbUtils.setJdbcTemplate(jdbcTemplate);
        dbUtils.truncateUserTable();
        dbUtils.addUsers();
    }

    @Test
    public void testAuthenticateUserWithCorrectCredentials() throws JsonProcessingException {
        User user = dbUtils.getTestUsers().get(0);
        String jsonUser = MAPPER.writeValueAsString(user);
        ResponseEntity<Object> response = sendJsonPostRequest(jsonUser, Object.class, AUTHENTICATE_USER);
        Map<String, String> responseMap = (Map<String, String>) response.getBody();
        assertNotNull("Response must contain authenticate token", responseMap.get("token"));
    }

    @Test(expected = HttpClientErrorException.class)
    public void testAuthenticateWithBadPassword() throws JsonProcessingException {
        User user = dbUtils.getTestUsers().get(0);
        user.setPassword("wrong password");
        String jsonUser = MAPPER.writeValueAsString(user);
        try {
            sendJsonPostRequest(jsonUser, Object.class, AUTHENTICATE_USER);
        } catch (HttpClientErrorException e) {
            assertEquals("The Status code must be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
            throw e;
        }
    }

    @Test(expected = HttpClientErrorException.class)
    public void testAuthenticateWithNonExistentUser() throws JsonProcessingException {
        String jsonUser = MAPPER.writeValueAsString(testUser);
        try {
            sendJsonPostRequest(jsonUser, Object.class, AUTHENTICATE_USER);
        } catch (HttpClientErrorException e) {
            assertEquals("The Status code must be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
            throw e;
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
        assertNull("Password must not be returned", response.getBody().getPassword());
    }

    @Test(expected = HttpClientErrorException.class)
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
            throw e;
        }
    }
    
    @Test(expected = HttpClientErrorException.class)
    public void testGetUserWithMissingAuthorization() {
        User user = dbUtils.getTestUsers().get(0);
        URI uri = UriComponentsBuilder.fromUriString(URL).queryParam("email", user.getEmail()).buildAndExpand(GET_USER)
                .toUri();
        try {
            restTemplate.getForEntity(uri, User.class);
        } catch (HttpClientErrorException e) {
            assertEquals("Response code should be 401", HttpStatus.UNAUTHORIZED, e.getStatusCode());
            throw e;
        }
    }
    
    @Test(expected = HttpClientErrorException.class)
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
            throw e;
        }
    }
    
    @Test
    public void testRegisterUser() throws JsonProcessingException {

        // Need to use custom mapper as default Jackson will ignore password on
        // serialization
        String jsonUser = MAPPER.writeValueAsString(testUser);

        ResponseEntity<User> response = sendJsonPostRequest(jsonUser, User.class, REGISTER_USER);
        assertEquals("HTTP Status must be 201", HttpStatus.CREATED, response.getStatusCode());

        User responseUser = response.getBody();

        assertNotNull("The response user must not be null", responseUser);
        assertEquals("The response user must have same email", testUser.getEmail(), responseUser.getEmail());

    }

}
