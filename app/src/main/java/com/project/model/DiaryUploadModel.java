package com.project.model;

import com.google.gson.annotations.SerializedName;

public class DiaryUploadModel {

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("mood_tag")
    private String moodTag;

    // Optional: jika API butuh aftercare langsung
    @SerializedName("aftercare")
    private String aftercare;

    // ======================
    // Constructor untuk upload
    // ======================
    public DiaryUploadModel(String title, String content, String moodTag) {
        this.title = title;
        this.content = content;
        this.moodTag = moodTag;
    }

    public DiaryUploadModel(String title, String content, String moodTag, String aftercare) {
        this.title = title;
        this.content = content;
        this.moodTag = moodTag;
        this.aftercare = aftercare;
    }

    // ======================
    // Getter & Setter
    // ======================
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMoodTag() {
        return moodTag;
    }

    public void setMoodTag(String moodTag) {
        this.moodTag = moodTag;
    }

    public String getAftercare() {
        return aftercare;
    }

    public void setAftercare(String aftercare) {
        this.aftercare = aftercare;
    }
}
