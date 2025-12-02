package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.PatientDetailModel;
import java.util.List;

public class PatientDetailResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("count")
    private int count; // ✅ Tambahkan field count

    @SerializedName("data")
    private List<PatientDetailModel> data; // ✅ UBAH ke List!

    public String getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public List<PatientDetailModel> getData() { // ✅ Return List
        return data;
    }
}