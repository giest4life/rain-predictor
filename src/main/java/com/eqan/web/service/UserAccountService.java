package com.eqan.web.service;

import java.util.List;

import com.eqan.web.model.User;

public interface UserAccountService {
     List<User> getUsers();
     User getUserById(long id);
     User getUserByEmail(String email);
     User createUser(User user);
     User updateUser(User user);
     void deleteUser(String email);
     void deleteUser(long id);
     boolean validate(String email, String password);
    
}
