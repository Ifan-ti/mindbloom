package com.project.model;

import com.google.gson.annotations.SerializedName;

public class DiaryModel {
    @SerializedName("id")
    private String id_diary;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String Content;
    @SerializedName("entry_date")
    private String entryDate;
    @SerializedName("mood_tag")
    private String moodTag;
    @SerializedName("ai_aftercare")
    private String aiAftercare;

    public String getId_diary() {
        return id_diary;
    }

    public String getMoodTag() {
        return moodTag;
    }

    public String getAiAftercare() {
        return aiAftercare;
    }

    public String getTitle() {
        return title;
    }
    public String getContent() {
        return Content;
    }
    public String getEntryDate() {
        return entryDate;
    }
    public String getIdDiary(){
        return id_diary;
    }
}
