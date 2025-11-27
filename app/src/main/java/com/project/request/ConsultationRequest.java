package com.project.request;

import com.google.firebase.Timestamp;

public class ConsultationRequest {
    private String requestId;
    private int userId;
    private int expertId;
    private String status; // pending, approved, rejected
    private Timestamp requestedAt;
    private String modality; // online, offline
    private String userName;
    private String expertName;

    // Empty constructor (required for Firestore)
    public ConsultationRequest() {}

    public ConsultationRequest(int userId, int expertId, String modality) {
        this.userId = userId;
        this.expertId = expertId;
        this.status = "pending";
        this. requestedAt = Timestamp.now();
        this.modality = modality;
    }

    // Getters and Setters
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getExpertId() { return expertId; }
    public void setExpertId(int expertId) { this.expertId = expertId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Timestamp requestedAt) { this.requestedAt = requestedAt; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this. modality = modality; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getExpertName() { return expertName; }
    public void setExpertName(String expertName) { this.expertName = expertName; }
}
