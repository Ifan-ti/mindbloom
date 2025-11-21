package com.project.data;

import com.google.gson.annotations.SerializedName;

// Model ini cocok dengan hasil kueri JOIN SQL di 'routes/posts.js'
public class PostModel {

    @SerializedName("post_id")
    private int postId;

    @SerializedName("content")
    private String contentText;

    @SerializedName("cover_image")
    private String coverImageUrl; // Akan NULL jika ini postingan teks

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("is_anonymous")
    private int isAnonymous; // 1 (true) atau 0 (false)

    @SerializedName("username")
    private String username;

    @SerializedName("avatar_url")
    private String avatarUrl;

    @SerializedName("like_count")
    private int likeCount;

    @SerializedName("comment_count")
    private int commentCount;

    // Getter
    public int getPostId() { return postId; }
    public String getContentText() { return contentText; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getCreatedAt() { return createdAt; }
    public boolean isAnonymous() { return isAnonymous == 1; }
    public String getUsername() { return isAnonymous() ? "Anonimus" : username; } // Logika anonim
    public String getAvatarUrl() { return avatarUrl; }
    public int getLikeCount() { return likeCount; }
    public int getCommentCount() { return commentCount; }
}