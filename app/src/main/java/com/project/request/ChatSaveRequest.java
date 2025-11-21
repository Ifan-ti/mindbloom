package com.project.request;

import com.google.gson.annotations.SerializedName;

// Model untuk menyimpan chat ke backend Anda
public class ChatSaveRequest {

    @SerializedName("userId")
    private int userId;

    @SerializedName("prompt")
    private String prompt;

    @SerializedName("response")
    private String response;

    @SerializedName("model")
    private String model;

    // Sesuaikan 'type' jika perlu, atau hapus jika tidak
    @SerializedName("type")
    private String type;

    public ChatSaveRequest(int userId, String prompt, String response, String model) {
        this.userId = userId;
        this.prompt = prompt;
        this.response = response;
        this.model = model;
        this.type = "reflection"; // Default (sesuai ERD Anda)
    }
}