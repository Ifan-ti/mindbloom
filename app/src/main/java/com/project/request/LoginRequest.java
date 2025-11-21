package com.project.request;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password; // Jangan password_hash! PHP minta raw password

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}