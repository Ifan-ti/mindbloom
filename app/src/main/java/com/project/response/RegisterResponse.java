package com.project.response;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    // INI BAGIAN YANG HILANG (Getter)
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}