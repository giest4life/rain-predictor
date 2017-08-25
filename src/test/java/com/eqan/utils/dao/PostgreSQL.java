package com.eqan.utils.dao;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
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

    public void addUsers() throws IOException {
        addHashedUsersToDatabase();
        testUsers = new ArrayList<>();

        Reader reader = Files.newBufferedReader(Paths.get("utils/MOCK_DATA.csv"));

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        int n = 0;
        for (CSVRecord csvRecord : records) {
            if (n == 200) {
                break;
            }
            testUsers.add(new User(csvRecord.get("email"), csvRecord.get("password")));
            n++;
        }
        
        reader.close();

    }

    private void addHashedUsersToDatabase() throws IOException {
        List<User> users = new ArrayList<>();
        Reader reader = Files.newBufferedReader(Paths.get("utils/users.csv"));
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord csvRecord : records) {
            users.add(new User(csvRecord.get("email"), csvRecord.get("password")));
        }
        
        reader.close();

        List<Object[]> pairs = new ArrayList<>();

        for (User user : users) {
            pairs.add(new Object[] { user.getEmail(), user.getPassword() });
        }

        if (LOG.isTraceEnabled())
            LOG.trace("Batch adding test users into user table");
        jdbcTemplate.batchUpdate("INSERT INTO app_user(email, password) VALUES(?,?)", pairs);
    }

    public List<User> getTestUsers() {
        return testUsers;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void truncateUserTable() {
        if (LOG.isTraceEnabled())
            LOG.trace("Dropping all rows...");
        String truncateStatement = "TRUNCATE %s";
        String userTable = "app_user";
        String sql = String.format(truncateStatement, userTable);
        jdbcTemplate.execute(sql);
    }
}
