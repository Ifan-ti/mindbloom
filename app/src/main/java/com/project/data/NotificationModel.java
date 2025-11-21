package com.project.data;

import com.google.gson.annotations.SerializedName;

// Model ini cocok dengan tabel 'notifications' [cite: image_85ad59.jpg]
public class NotificationModel {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("message_text")
    private String messageText;

    @SerializedName("type")
    private String type; // 'like', 'comment'

    @SerializedName("is_read")
    private int isRead;

    @SerializedName("created_at")
    private String createdAt;

    // Getter
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getMessageText() { return messageText; }
    public String getType() { return type; }
    public boolean isRead() { return isRead == 1; }
    public String getCreatedAt() { return createdAt; }
}