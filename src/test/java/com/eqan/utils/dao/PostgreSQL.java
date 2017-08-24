package com.eqan.utils.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.eqan.web.model.User;


public class PostgreSQL {
    private static Logger LOG = LoggerFactory.getLogger(PostgreSQL.class);
    private JdbcTemplate jdbcTemplate;
    private List<User> testUsers;
    public PostgreSQL() {
       
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate)  {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<User> getTestUsers() {
        return testUsers;
    }
    
    public void truncateUserTable() {
        if (LOG.isDebugEnabled())
            LOG.debug("Dropping all rows...");
        String truncateStatement = "TRUNCATE %s";
        String userTable = "app_user";
        String sql = String.format(truncateStatement, userTable);
        jdbcTemplate.execute(sql);
    }

    public void addUsers() {
        testUsers = new ArrayList<>();
        
        User user1 = new User("jon@test.com", "password123");
        User user2 = new User("jane@test.com", "welcome123");
        User user3 = new User("jack@test.com", "password");

        testUsers.add(user1);
        testUsers.add(user2);
        testUsers.add(user3);

        List<Object[]> pairs = new ArrayList<>();

        for (User user : testUsers) {
            pairs.add(new Object[] { user.getEmail(), user.getPassword() });
        }

        if (LOG.isDebugEnabled())
            LOG.debug("Batch adding test users into user table");
        jdbcTemplate.batchUpdate("INSERT INTO app_user(email, password) VALUES(?,?)", pairs);
    }
}
