package com.project.respone;

import com.project.data.DiaryModel;
import com.project.respone.DiaryRespone;
import java.util.List;

public class DiaryRespone {
    private String status;
    private List<DiaryModel> data;

    public String getStatus() {
        return status;
    }

    public List<DiaryModel> getData() {
        return data;
    }
}
