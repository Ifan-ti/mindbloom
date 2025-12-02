package com.project.respone;

import com.project.data.DiaryModel;
import com.project.respone.DiaryDetailResponse;

public class DiaryDetailResponse {
    private String status;
    private DiaryModel data;

    public String getStatus() {
        return status;
    }

    public DiaryModel getData() {
        return data;
    }
}
