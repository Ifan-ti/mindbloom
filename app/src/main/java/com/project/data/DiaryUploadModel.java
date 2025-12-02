package com.project.data;

public class DiaryUploadModel {
    private int user_id;
    private String title;
    private String content;
    private String mood_tag;

    public DiaryUploadModel(int user_id, String title, String content, String mood_tag) {
        this.user_id = user_id;
        this.title = title;
        this.content = content;
        this.mood_tag = mood_tag;
    }

    public int getUser_id() {
        return user_id;
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
}
