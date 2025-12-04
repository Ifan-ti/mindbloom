package com.project.request; // Sesuaikan package Anda

public class UpdateProfileRequest {

    private String fullName;
    private String username;
    private String bio;
    private String email;
    private String avatar_url;

    public UpdateProfileRequest(String fullName, String username, String bio, String email, String avatar_url ) {
        this.fullName = fullName;
        this.username = username;
        this.bio = bio;
        this.email = email;
        this.avatar_url = avatar_url;
    }
}
