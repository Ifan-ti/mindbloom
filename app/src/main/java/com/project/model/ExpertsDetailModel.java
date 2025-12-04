package com.project.model;

import com.google.gson.annotations.SerializedName;

public class ExpertsDetailModel {
    @SerializedName("license_number")
    private String license_number;
    @SerializedName("bio")
    private String bio;
    @SerializedName("avatar")
    private String avatar;

    public String getBio() {
        return bio;
    }
    public String getAvatar() {
        return avatar;
    }
    public String getLicense_number() {
        return license_number;
    }
}
