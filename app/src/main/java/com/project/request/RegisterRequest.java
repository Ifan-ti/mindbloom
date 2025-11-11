package com.project.request;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;

    // INI BAGIAN YANG HILANG (Constructor)
    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}