package com.eqan.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

public class User {
    private long id;
    private String email;

    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    // No-arg constructor needed for Jackson
    public User() {
        
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(long id, String email, String password) {
        this(email, password);
        this.id = id;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id != other.id || !email.equals(other.email))
            return false;
        return true;
    }

}
