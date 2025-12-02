package com.project.model;

import com.google.gson.annotations.SerializedName;

public class ProfileModel {
    @SerializedName("username")
    private String username;
    @SerializedName("email")
    private String email;

    @SerializedName("cover_image")
    private String coverImage;

    @SerializedName("bio")
    private String bio;
    @SerializedName("diary_count")
    private int diaryCont;

    public int getDiaryCont() {
        return diaryCont;
    }

    public String getBio() {
        return bio;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
