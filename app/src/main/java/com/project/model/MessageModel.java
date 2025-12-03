package com.project.model;

import com.google.gson.annotations. SerializedName;

public class MessageModel {

    // ✅ TAMBAHKAN FIELD ID
    @SerializedName("id")
    private int id;

    @SerializedName("sender_id")
    private int senderId;

    @SerializedName("message")
    private String message;

    @SerializedName("role")
    private String role;

    @SerializedName("timestamp")
    private String timestamp;

    // Constructor Kosong (Penting untuk Gson)
    public MessageModel() {}

    public MessageModel(int id, int senderId, String message, String timestamp) {
        this.id = id;
        this.senderId = senderId;
        this. message = message;
        this. timestamp = timestamp;
    }

    // ✅ Tambahkan Getter untuk ID
    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }

    public String getRole() {
        return role;
    }

    public String getTimestamp() {
        return timestamp;
    }
}