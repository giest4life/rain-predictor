package com.eqan.web.service;

import java.util.List;

import com.eqan.web.model.User;

public interface UserAccountService {
    public List<User> getUsers();
    public User getUserById(long id);
    public User getUserByEmail(String email);
    public User createUser(User user);
    public User updateUser(User user);
    public void deleteUser(String email);
    public void deleteUser(long id);
    
    public boolean validate(String email, String password);
    public User signIn(String email, String password);
    
}
