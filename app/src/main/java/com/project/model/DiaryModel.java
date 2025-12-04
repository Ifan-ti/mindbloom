package com.project.model;

import com.google.gson.annotations.SerializedName;

public class DiaryModel {

    private int id;
    private String author;
    private int id_user;
    private String title;
    private String content;
    private String entry_date;
    private String mood_tag;
    private String is_private;
    private String ai_aftercare;
    private String created_at;
    private String updated_at;

    // GETTER & SETTER
    public int getId() {
        return id;
    }

    public void setId(int id) { this.id = id; }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

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

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getMood_tag() {
        return mood_tag;
    }

    public void setMood_tag(String mood_tag) {
        this.mood_tag = mood_tag;
    }

    public String getIs_private() {
        return is_private;
    }

    public void setIs_private(String is_private) {
        this.is_private = is_private;
    }

    public String getAi_aftercare() {
        return ai_aftercare;
    }

    public void setAi_aftercare(String ai_aftercare) {
        this.ai_aftercare = ai_aftercare;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
