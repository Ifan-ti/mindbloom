package com.project.response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

import com.project.model.ArticleModel;


public class ArticlePopulerResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<ArticleModel> data;

    // Getter untuk data
    public List<ArticleModel>
    getData() {
        return data;
    }
}
