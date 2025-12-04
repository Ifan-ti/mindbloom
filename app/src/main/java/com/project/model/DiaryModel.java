package com.project.model;

import com.google.gson.annotations.SerializedName;

public class DiaryModel {

    @SerializedName("id")
    private int id_diary;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("entry_date")
    private String entry_date;

    @SerializedName("mood_tag")
    private String mood_tag;

    @SerializedName("ai_aftercare")
    private String ai_aftercare;

    @SerializedName("is_private")
    private String is_private;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    // --- GETTER & SETTER ---
    public int getId_diary() { return id_diary; }
    public void setId_diary(int id_diary) { this.id_diary = id_diary; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getEntry_date() { return entry_date; }
    public void setEntry_date(String entry_date) { this.entry_date = entry_date; }

    public String getMood_tag() { return mood_tag; }
    public void setMood_tag(String mood_tag) { this.mood_tag = mood_tag; }

    public String getAi_aftercare() { return ai_aftercare; }
    public void setAi_aftercare(String ai_aftercare) { this.ai_aftercare = ai_aftercare; }

    public String getIs_private() { return is_private; }
    public void setIs_private(String is_private) { this.is_private = is_private; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

}
