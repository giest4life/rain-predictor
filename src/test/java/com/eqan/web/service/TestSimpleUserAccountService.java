package com.eqan.web.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eqan.utils.dao.PostgreSQL;
import com.eqan.web.exceptions.NotAuthorizedException;
import com.eqan.web.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class TestSimpleUserAccountService {
    private static Logger LOG = LoggerFactory.getLogger(TestSimpleUserAccountService.class);
    private User testUser = new User("jill@test.com", "password123");

    @Autowired
    private UserAccountService userService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private PostgreSQL dbUtils = new PostgreSQL();
    private List<User> testUsers;

    @Test(expected = EmptyResultDataAccessException.class)
    public void deleteUserTest() {
        User user = userService.createUser(testUser);
        assertNotNull("The user that must be deleted must not be null", user);
        userService.deleteUser(user.getId());
        userService.getUserById(user.getId());
    }

    @Test
    public void getUsersTest() {
        List<User> users = userService.getUsers();
        assertEquals("Returned users must be same size as test users", testUsers.size(), users.size());

        int userRow = 0;
        for (User user : users) {
            User testUser = testUsers.get(userRow);
            assertEquals("Retrieves user email must equal test user email", testUser.getEmail(), user.getEmail());
            userRow++;
        }

    }

    @Before
    public void preTestSetup() throws IOException {
        dbUtils.setJdbcTemplate(jdbcTemplate);
        dbUtils.truncateUserTable();
        dbUtils.addUsers();
        testUsers = dbUtils.getTestUsers();

    }

    @Test
    public void testCreateUser() {
        User user = userService.createUser(testUser);
        assertNotNull("The returned user from create must not be null", user);
        assertEquals("Retrieved user emails must be the same", testUser.getEmail(), user.getEmail());
    }

    @Test
    public void testGetUser() {
        List<User> testUsers = dbUtils.getTestUsers();
        User user = userService.getUserByEmail(testUsers.get(0).getEmail());
        assertEquals("Retrieved user emails must be the same", testUsers.get(0).getEmail(), user.getEmail());
        User user2 = userService.getUserById(user.getId());
        assertEquals("Retrieved users must have the same id", user, user2);
    }

    @Test
    public void testSignIn() {
        if (LOG.isDebugEnabled())
            LOG.debug("Testing signIn method");
        User user = testUsers.get(0);
        User testedUser = userService.signIn(user.getEmail(), user.getPassword());
        assertEquals("Returned user must be the same", user.getEmail(), testedUser.getEmail());
    }
    
    @Test(expected=NotAuthorizedException.class)
    public void testSignInInvalidUser() {
        if (LOG.isDebugEnabled())
            LOG.debug("Testing invalid user trying to sign in");
        User user = testUsers.get(0);
        user.setPassword("This is wrong");
        userService.signIn(user.getEmail(), user.getPassword());
    }

    @Test
    public void testUpdateUser() {
        User user = userService.createUser(testUser);
        user.setEmail("gretel@test.com");
        userService.updateUser(user);
        User user2 = userService.getUserById(user.getId());
        assertEquals("The new retrieved user must have the updated email", user.getEmail(), user2.getEmail());

    }

}
