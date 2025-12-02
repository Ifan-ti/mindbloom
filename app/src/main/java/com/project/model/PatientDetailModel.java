package com.project.model;

import com.google.gson.annotations. SerializedName;

public class PatientDetailModel {
    @SerializedName("user_id")
    private String userId;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("last_message")
    private String lastMessage;

    @SerializedName("last_sent_time")
    private String lastSentTime;

    @SerializedName("avatar_base64")
    private String avatar_base64;

    // ✅ TAMBAHKAN FIELD BARU
    @SerializedName("request_status")
    private String requestStatus;

    @SerializedName("room_id")
    private String roomId;

    // Getters
    public String getAvatar_base64() {
        return avatar_base64;
    }

    public String getLastSentTime() {
        return lastSentTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    // ✅ TAMBAHKAN GETTER BARU
    public String getRequestStatus() {
        return requestStatus;
    }

    public String getRoomId() {
        return roomId;
    }
}