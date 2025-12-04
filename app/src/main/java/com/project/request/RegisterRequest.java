package com.project.request;

public class RegisterRequest {
    private String FullName;
    private String username;
    private String email;
    private String password;

    // INI BAGIAN YANG HILANG (Constructor)
    public RegisterRequest(String username, String email, String password, String FullName) {
        this.FullName = FullName;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}