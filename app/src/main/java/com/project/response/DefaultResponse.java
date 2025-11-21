package com.project.response;

import com.google.gson.annotations.SerializedName;

public class DefaultResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}