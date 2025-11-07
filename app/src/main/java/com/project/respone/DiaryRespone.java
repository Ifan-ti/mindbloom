package com.project.respone;

import com.google.gson.annotations.SerializedName;
import com.project.data.ArticleModel;
import com.project.data.DiaryModel;

import java.util.List;

public class DiaryRespone {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<DiaryModel> data;
    public List<DiaryModel> getData() {
        return data;
    }


    }
