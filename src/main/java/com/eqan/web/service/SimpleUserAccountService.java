package com.eqan.web.service;

import static com.eqan.web.security.BcryptPasswordHashing.checkPassword;
import static com.eqan.web.security.BcryptPasswordHashing.hashPassword;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.eqan.web.exceptions.NotAuthorizedException;
import com.eqan.web.model.User;
import com.eqan.web.repository.UserRepository;
@Service("simpleUser")
public class SimpleUserAccountService implements UserAccountService {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountService.class);
    
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
        if(LOG.isDebugEnabled())
            LOG.debug("Received signIn request for {}", email);
        User dbUser = null;
        try {
            dbUser = getUserByEmail(email); 
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isTraceEnabled())
                LOG.trace(e.getMessage());
            throw new NotAuthorizedException(String.format("User %s could not be authenticated", email));
        }
        

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
