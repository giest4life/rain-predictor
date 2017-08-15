package com.eqan.web.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eqan.web.model.User;
import com.eqan.web.repository.UserRepository;

@Service("simpleUser")
public class SimpleUser implements UserService {
    private static Logger LOG = LoggerFactory.getLogger(UserService.class);
    
    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting all users");
        }
      return  userRepository.getUsers();
    }

}
