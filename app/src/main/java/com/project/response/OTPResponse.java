package com.project.response;

import com.google.gson.annotations. SerializedName;

public class OTPResponse {
    private boolean success;
    private String message;
    private String email;

    @SerializedName("expires_in")
    private String expiresIn;

    @SerializedName("otp_for_testing")
    private String otpForTesting;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getOtpForTesting() {
        return otpForTesting;
    }

    public void setOtpForTesting(String otpForTesting) {
        this.otpForTesting = otpForTesting;
    }
}
