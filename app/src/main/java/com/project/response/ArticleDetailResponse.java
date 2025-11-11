// File: com/project/respone/ArticleDetailResponse.java
package com.project.response;

import com.project.data.ArticleModel;
import com.google.gson.annotations.SerializedName;

public class ArticleDetailResponse {

    @SerializedName("status")
    private String status;

    // 🔥 PERBEDAAN UTAMA: Ini adalah satu objek, BUKAN List
    @SerializedName("data")
    private ArticleModel data;

    // Getter
    public String getStatus() {
        return status;
    }

    public ArticleModel getData() {
        return data;
    }
}