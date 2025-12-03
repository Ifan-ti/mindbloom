package com.project.respone;

import com.project.respone.DiaryRespone;
import com.google.gson.annotations.SerializedName;
import com.project.model.DiaryModel;

import java.util.List;

public class DiaryRespone {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<DiaryModel> data;

    public String getStatus() {
        return status;
    }

    public List<DiaryModel> getData() {
        return data;
    }
}
