package com.eqan.web.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eqan.web.exceptions.NotAuthorizedException;
import com.eqan.web.model.User;
import com.eqan.web.repository.UserRepository;

@Service("simpleUser")
public class SimpleUserAccountService implements UserAccountService {
    private static Logger LOG = LoggerFactory.getLogger(UserAccountService.class);
    private static int WORKLOAD = 12;

    private static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking hashed password");
        }
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
    
    private static String hashPassword(String plaintextPassword) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Hashing password with workload {}", WORKLOAD);
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plaintextPassword, salt);
    }
    
    @Autowired
    UserRepository userRepository;

    @Override
    public User createUser(User user) {
        String hashedPassword = hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.createUser(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteUser(id);
    }

    @Override
    public void deleteUser(String email) {

        throw new UnsupportedOperationException("Deleting user by email is not supported yet");
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    @Override
    public User getUserById(long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<User> getUsers() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting all users");
        }
        return userRepository.getUsers();
    }

    public User signIn(String email, String password) {
        User dbUser = getUserByEmail(email);
        if (!checkPassword(password, dbUser.getPassword())) {
            throw new NotAuthorizedException(String.format("User %s could not be authenticated", email));
        }
        return dbUser;
    }

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

}
