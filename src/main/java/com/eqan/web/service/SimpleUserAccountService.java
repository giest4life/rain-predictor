package com.eqan.web.service;

import static com.eqan.web.security.BcryptPasswordHashing.checkPassword;
import static com.eqan.web.security.BcryptPasswordHashing.hashPassword;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.eqan.web.model.User;
import com.eqan.web.repository.UserRepository;
@Service("simpleUser")
public class SimpleUserAccountService implements UserAccountService {
    private static final Logger LOG = LoggerFactory.getLogger(UserAccountService.class);
    
    private UserRepository userRepository;
    
    @Autowired
    public SimpleUserAccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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

    @Override
    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    @Override
    public boolean validate(String email, String password) {
        if (LOG.isTraceEnabled())
            LOG.trace("Validating user with email {} and password", email);
        try {
            User dbUser = getUserByEmail(email);
            return checkPassword(password, dbUser.getPassword());
        } catch (EmptyResultDataAccessException e) {
            if (LOG.isTraceEnabled())
                LOG.trace(e.getMessage());
        }
        return false;
    }

}
