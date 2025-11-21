package com.project.modul;

public class ChatMessage {

    private String message;
    private boolean isUser;
    private boolean isLoading;

    // Normal message
    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.isLoading = false;
    }

    // Loading message
    public ChatMessage(boolean isLoading) {
        this.message = "Boomy Sedang Mengetik...";
        this.isLoading = isLoading;
        this.isUser = false;
    }

    public String getMessage() { return message; }
    public boolean isUser() { return isUser; }
    public boolean isLoading() { return isLoading; }
}
