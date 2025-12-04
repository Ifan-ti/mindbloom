package com.project.request;

import com.google.gson.annotations.SerializedName;

public class AftercareRequest {

    @SerializedName("diary_id")
    private int diaryId; // int, bukan String

    @SerializedName("content")
    private String content;

    // Constructor benar
    public AftercareRequest(int diaryId, String content) {
        this.diaryId = diaryId;
        this.content = content;
    }

    // Getter & Setter
    public int getDiaryId() { return diaryId; }
    public void setDiaryId(int diaryId) { this.diaryId = diaryId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
