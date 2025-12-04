package com.project.adapter;

public class Post {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;

    private int type;
    private String username;
    private String time;
    private String description;
    private int imageResId;  // untuk post gambar

    public Post(int type, String username, String time, String description, int imageResId) {
        this.type = type;
        this.username = username;
        this.time = time;
        this.description = description;
        this.imageResId = imageResId;
    }

    public Post(int type, String username, String time, String description) {
        this(type, username, time, description, 0);
    }

    public int getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }
}
