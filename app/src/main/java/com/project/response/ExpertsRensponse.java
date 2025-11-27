package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.ExpertsModel;

import java.util.List;

public class ExpertsRensponse {
    public String getStatus() {
        return status;
    }

    public List<ExpertsModel> getData() {
        return data;
    }

    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private List<ExpertsModel> data;
}
