package com.project.respone;

import com.google.gson.annotations.SerializedName;
import com.project.model.DiaryModel;

public class DiaryDetailResponse {
    @SerializedName("status")
    private String status;

    // 🔥 PERBEDAAN UTAMA: Ini adalah satu objek, BUKAN List
    @SerializedName("data")
    private DiaryModel data;

    // Getter
    public String getStatus() {
        return status;
    }

    public DiaryModel getData() {
        return data;
    }
}
