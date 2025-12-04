package com.project.response;

import com.google.gson.annotations.SerializedName;

public class VerifyOTPResponse {
    private boolean success;
    private String message;

    @SerializedName("reset_token")
    private String resetToken;

    @SerializedName("token_expires_in")
    private String tokenExpiresIn;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public String getTokenExpiresIn() {
        return tokenExpiresIn;
    }

    public void setTokenExpiresIn(String tokenExpiresIn) {
        this.tokenExpiresIn = tokenExpiresIn;
    }
}
