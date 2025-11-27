package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.ExpertsDetailModel;

public class ExpertsDetailResponse {
    @SerializedName("status")
    private String status;
    @SerializedName("data")
    private ExpertsDetailModel data;

    public String getStatus() {
        return status;
    }

    public ExpertsDetailModel getData() {
        return data;
    }
}
