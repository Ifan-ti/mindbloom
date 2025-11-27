package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.ProfileModel;

public class ProfileResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private ProfileModel data; // Objek data yang berisi token dan user

    public String getStatus() {
        return status;
    }

    public ProfileModel getData() {
        return data;
    }
}
