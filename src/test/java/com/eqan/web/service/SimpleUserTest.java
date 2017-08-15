package com.eqan.web.service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.eqan.web.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext.xml")
public class SimpleUserTest {
    private static Logger LOG = LoggerFactory.getLogger(SimpleUserTest.class);
    
    @Autowired
    private UserService userService;
    
    @Test
    public void getUsersAlwaysReturnsJon() {

        List<User> users = userService.getUsers();
        assertFalse("Returned users must not be empty", users.isEmpty());
        User actualUser = new User(1L, "jon@test.com", "password");
        User jon = users.get(0);
        if (LOG.isDebugEnabled())
            LOG.debug("User returned is {}", jon);
        LOG.info("User returned is {}", jon);
       
        assertEquals("Returned user must equal user jon", jon, actualUser);
        
    }
}
