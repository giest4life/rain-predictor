package com.eqan.web.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.eqan.utils.dao.UserRowMapper;
import com.eqan.web.model.Location;
import com.eqan.web.model.User;

@Repository("userRepository")
public class UserRepository {

    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User createUser(User user) {
        // jdbcTemplate.update("insert into app_user(email, password)
        // values(?,?)", user.getEmail(), user.getPassword());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO app_user(email, password) VALUES(?,?)",
                        new String[] { "user_id" });
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());
                return ps;
            }
        }, keyHolder);

        Number id = keyHolder.getKey();
        user.setId(id.longValue());
        return getUserById(id.longValue());
    }

    public User getUserById(long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM app_user WHERE user_id = ?", new UserRowMapper(), id);

    }

    public User getUserByEmail(String email) {
        return jdbcTemplate.queryForObject("SELECT * FROM app_user WHERE email = ?", new UserRowMapper(), email);
    }

    public User updateUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE app_user SET email = ?, password = ? WHERE user_id = ?",
                        new String[] { "user_id" });
                ps.setString(1, user.getEmail());
                ps.setString(2,  user.getPassword());
                ps.setLong(3, user.getId());
                return ps;
            }
        }, keyHolder);
        Number id = keyHolder.getKey();
        return getUserById(id.longValue());
    }

    public List<User> getUsers() {
        List<User> users = jdbcTemplate.query("SELECT * FROM app_user", new UserRowMapper());
        return users;
    }

    public void deleteUser(long id) {
        jdbcTemplate.update("DELETE FROM app_user WHERE user_id = ?", id);
    }
    
    public void saveLocations(User user) {
        List<Object[]> pairs = new ArrayList<>();
    }
    
}
