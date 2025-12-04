
package com.project.request;
public class DiaryRequest {

    private int user_id;
    private String title;
    private String content;
    private String mood_tag;
    private String ai_aftercare;

    public DiaryRequest(int user_id, String title, String content, String mood_tag, String ai_aftercare) {
        this.user_id = user_id;
        this.title = title;
        this.content = content;
        this.mood_tag = mood_tag;
        this.ai_aftercare = ai_aftercare;
    }

    // Getter & Setter
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getMood_tag() {
        return mood_tag;
    }

    public void setMood_tag(String mood_tag) {
        this.mood_tag = mood_tag;
    }

    public String getAi_aftercare() {
        return ai_aftercare;
    }

    public void setAi_aftercare(String ai_aftercare) {
        this.ai_aftercare = ai_aftercare;
    }
}
