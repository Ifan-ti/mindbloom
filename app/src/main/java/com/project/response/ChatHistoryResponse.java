// File: com/project/response/ChatHistoryResponse.java
package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.MessageModel;
import java.util.List;

public class ChatHistoryResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<MessageModel> data;

    public String getStatus() {
        return status;
    }

    public List<MessageModel> getData() {
        return data;
    }
}