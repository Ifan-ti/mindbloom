package com.project.model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("fullName")
    private String fullName;
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("bio")
    private String bio;
    @SerializedName("diary_count")
    private int diaryCont;

    public String getFullName() {
        return fullName;
    }

    public int getDiaryCont() {
        return diaryCont;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
