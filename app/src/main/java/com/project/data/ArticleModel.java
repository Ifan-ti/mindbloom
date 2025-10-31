package com.project.data;

import com.google.gson.annotations.SerializedName;

public class ArticleModel {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("excerpt")
    private String excerpt;

    @SerializedName("cover_image_url")
    private String coverImageUrl;

    @SerializedName("read_count")
    private int readCount;

    @SerializedName("name_tag")
    private String namaTag;

    // Variabel sudah ada

    // ... Tambahkan semua kolom lain yang Anda ambil

    // Getter untuk diakses di Activity/Adapter
    public String getTitle() {

        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    // 🔥 TAMBAHKAN GETTER INI 🔥
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    public int getReadCount() {
        return readCount;
    }

    public String getNamaTag() {
        return namaTag;
    }
}



    // ... Pastikan Anda juga menambahkan getter untuk kolom lain jika diperlukan (id, published_at, dll.)
