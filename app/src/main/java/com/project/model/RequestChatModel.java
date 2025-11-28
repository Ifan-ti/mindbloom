package com.project.model;

import com.google.gson.annotations.SerializedName;

public class RequestChatModel {

    // SerializedName harus SAMA PERSIS dengan key yang kamu baca di PHP ($data['expert_id'])
    @SerializedName("expert_id")
    private int expertId;

    public RequestChatModel(int expertId) {
        this.expertId = expertId;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }
}