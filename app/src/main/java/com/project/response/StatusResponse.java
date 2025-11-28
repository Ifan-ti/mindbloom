package com.project.response;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message") // Jaga-jaga kalau error, PHP kirim message
    private String message;

    @SerializedName("data")
    private StatusData data;

    // Getter
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public StatusData getData() { return data; }

    // --- INNER CLASS untuk objek "data" ---
    public static class StatusData {
        @SerializedName("request_status")
        private String requestStatus; // pending, approved, rejected, none

        @SerializedName("room_id")
        private String roomId; // Bisa null kalau belum approved

        public String getRequestStatus() { return requestStatus; }
        public String getRoomId() { return roomId; }
    }
}