package com.project.modul;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private boolean isLoading;

    public ChatMessage(String message, boolean isUser, boolean isLoading) {
        this.message = message;
        this.isUser = isUser;
        this. isLoading = isLoading;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}