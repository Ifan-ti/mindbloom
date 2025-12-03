package com.project.response;

import com.project.model.PostModel;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private List<PostModel> data;

    public String getStatus() { return status; }
    public List<PostModel> getData() { return data; }
}