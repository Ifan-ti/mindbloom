package com.project.model;

import com.google.firebase.Timestamp;

public class MessageModel {
    private String senderId;
    private String message;
    private Timestamp timestamp;

    // Constructor kosong diperlukan untuk Firebase
    public MessageModel() { }

    public MessageModel(String senderId, String message, Timestamp timestamp) {
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public Timestamp getTimestamp() { return timestamp; }
}