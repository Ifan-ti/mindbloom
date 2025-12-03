package com.project.request;

import com.google.gson.annotations.SerializedName;

public class ChatExpertsRequest {

    @SerializedName("expert_id")
    private int expertId;

    @SerializedName("message")
    private String message;

    public ChatExpertsRequest(int expertId, String message) {
        this.expertId = expertId;
        this.message = message;
    }

    // Getters (opsional, tapi good practice)
    public int getExpertId() {
        return expertId;
    }

    public String getMessage() {
        return message;
    }
}