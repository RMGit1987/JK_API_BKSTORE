package com.bkstore.fastapi.api.payloads;

public class UserSignup {
    private Integer id; 
    private String email;
    private String password;

    public UserSignup() {
    }

    // Constructor for creating new user signup requests
    public UserSignup(Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    // Constructor for creating new user signup requests without email
    public UserSignup(Integer id, String password) {
        this.id = id;
        this.password = password;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}