package com.example.moneytrackerandroidsqlite.models;

import java.util.Date;

public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private Date createdAt;
    private Date lastLogin;
    public User() {}

    public User(String username, String password, String email, Date createdAt) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
}
