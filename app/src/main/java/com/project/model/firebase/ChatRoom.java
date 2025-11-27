package com.project.model.firebase;

import com.google.firebase.Timestamp;

public class ChatRoom {
    private String roomId;
    private int userId;
    private int expertId;
    private String status; // active, closed
    private Timestamp createdAt;
    private String lastMessage;
    private Timestamp lastMessageTime;
    private String userName;
    private String expertName;

    // Empty constructor
    public ChatRoom() {}

    public ChatRoom(int userId, int expertId) {
        this.userId = userId;
        this.expertId = expertId;
        this.status = "active";
        this. createdAt = Timestamp.now();
        this.lastMessage = "";
        this.lastMessageTime = Timestamp.now();
    }

    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getExpertId() { return expertId; }
    public void setExpertId(int expertId) { this.expertId = expertId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }

    public Timestamp getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(Timestamp lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getExpertName() { return expertName; }
    public void setExpertName(String expertName) { this.expertName = expertName; }
}
