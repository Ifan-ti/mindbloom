// File: com/project/response/StatusResponse.java

package com.project.response;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public Data getData() { return data; }

    public class Data {
        @SerializedName("request_status")
        private String requestStatus;

        // 🔥🔥 INI YANG BIKIN ERROR SEBELUMNYA 🔥🔥
        // PHP kirim "room_id", tapi Java bacanya "roomId".
        // Tanpa @SerializedName, hasilnya jadi NULL.
        @SerializedName("room_id")
        private String roomId;

        public String getRequestStatus() { return requestStatus; }
        public String getRoomId() { return roomId; }
    }
}