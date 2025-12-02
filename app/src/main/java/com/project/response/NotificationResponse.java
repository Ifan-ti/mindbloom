package com.project.response;

import com.project.model.NotificationModel;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NotificationResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<NotificationModel> data;

    public String getStatus() { return status; }
    public List<NotificationModel> getData() { return data; }
}