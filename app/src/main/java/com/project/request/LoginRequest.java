// Lokasi: com/project/request/LoginRequest.java
package com.project.request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    // Nama field ini harus cocok dengan yang diharapkan API
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter (opsional, tapi baik untuk ada)
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}