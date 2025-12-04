package com.project.model;

import com.google.gson.annotations.SerializedName;

public class ArticleModel {



    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("excerpt")
    private String excerpt;

    @SerializedName("id")
    private int id_articles;
    @SerializedName("peninjau")
    private String peninjau;
    @SerializedName("cover_image")
    private String coverImageUrl;

    @SerializedName("read_count")
    private int readCount;

    @SerializedName("name_tag")
    private String namaTag;

    @SerializedName("created_at")
    private String Date;

    @SerializedName("author")
    private String author;


    // Variabel sudah ada

    // ... Tambahkan semua kolom lain yang Anda ambil

    // Getter untuk diakses di Activity/Adapter


    public String getPeninjau() {
        return peninjau;
    }

    public String getTitle() {
        return title;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getContent() {
        return content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public int getId_articles() {
        return id_articles;
    }

    public int getReadCount() {
        return readCount;
    }

    public String getNamaTag() {
        return namaTag;
    }

    public String getDate() {
        return Date;
    }

    public String getAuthor() {
        return author;
    }
}



// ... Pastikan Anda juga menambahkan getter untuk kolom lain jika diperlukan (id, published_at, dll.)