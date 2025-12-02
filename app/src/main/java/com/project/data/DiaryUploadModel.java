package com.project.data;

public class DiaryUploadModel {

    private String title;
    private String content;
    private String mood_tag;
    private String entry_date;

    public DiaryUploadModel(String title, String content, String mood_tag, String entry_date) {
        this.title = title;
        this.content = content;
        this.mood_tag = mood_tag;
        this.entry_date = entry_date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getMood_tag() {
        return mood_tag;
    }

    public String getEntry_date() {
        return entry_date;
    }
}
