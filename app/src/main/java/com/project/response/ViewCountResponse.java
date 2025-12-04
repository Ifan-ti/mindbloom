package com.project.response;

public class ViewCountResponse {
    private String status;
    private String message;
    private boolean already_viewed;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAlreadyViewed() {
        return already_viewed;
    }
}