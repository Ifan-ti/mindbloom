package com.project.model.firebase;

public class UserSession {
    private String activeRoomId;
    private int expertId;
    private String status; // active, closed

    // Empty constructor
    public UserSession() {}

    public UserSession(String activeRoomId, int expertId, String status) {
        this. activeRoomId = activeRoomId;
        this.expertId = expertId;
        this. status = status;
    }

    // Getters and Setters
    public String getActiveRoomId() { return activeRoomId; }
    public void setActiveRoomId(String activeRoomId) { this.activeRoomId = activeRoomId; }

    public int getExpertId() { return expertId; }
    public void setExpertId(int expertId) { this. expertId = expertId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this. status = status; }
}