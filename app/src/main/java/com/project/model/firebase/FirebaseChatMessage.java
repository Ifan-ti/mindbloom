package com.project.model.firebase;

import com.google.firebase.Timestamp;

public class FirebaseChatMessage {
    private String messageId;
    private int senderId;
    private String senderType; // 'user' or 'expert'
    private String message;
    private Timestamp timestamp;
    private boolean isRead;

    // Empty constructor
    public FirebaseChatMessage() {}

    public FirebaseChatMessage(int senderId, String senderType, String message) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.message = message;
        this.timestamp = Timestamp.now();
        this.isRead = false;
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this. timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}