package com.eqan.web.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.eqan.web.model.User;

@Repository("userRepository")
public class UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<User> getUsers() {
        User jon = new User(1L, "jon@test.com", "password");
        List<User> users = new ArrayList<>();
        users.add(jon);
        return users;
    }
}
