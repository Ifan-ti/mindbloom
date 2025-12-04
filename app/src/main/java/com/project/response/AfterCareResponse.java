package com.project.response;

import com.google.gson.annotations.SerializedName;

public class AfterCareResponse {

    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("aftercare_text")
    private String aftercareText;

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

    public String getAftercareText() {
        return aftercareText;
    }

    public void setAftercareText(String aftercareText) {
        this.aftercareText = aftercareText;
    }
}
