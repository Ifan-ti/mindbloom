package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.MoodModel;

import java.util.List;

public class MoodResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<MoodModel> data; // List dari MoodModel yang sudah kita buat sebelumnya

    public List<MoodModel> getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }
}
