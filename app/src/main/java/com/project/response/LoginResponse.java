// Lokasi: com/project/respone/LoginResponse.java
package com.project.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    // Field ini (status, message, data) harus SAMA PERSIS
    // dengan respons JSON dari API backend Anda

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private LoginResponseData data; // Objek data yang berisi token dan user

    // Getter
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public LoginResponseData getData() {
        return data;
    }
}