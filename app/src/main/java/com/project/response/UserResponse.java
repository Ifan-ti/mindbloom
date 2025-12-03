package com.project.response;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private UserData data;

    public String getStatus() {
        return status;
    }

    public UserData getData() {
        return data;
    }

    public static class UserData {
        @SerializedName("username")
        private String username;

        public String getUsername() {
            return username;
        }
    }
}
