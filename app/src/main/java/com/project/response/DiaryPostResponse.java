package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.data.DiaryModel;

public class DiaryPostResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private DiaryModel data;

    // ======= GETTER & SETTER =======

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DiaryModel getData() {
        return data;
    }

    public void setData(DiaryModel data) {
        this.data = data;
    }
}
