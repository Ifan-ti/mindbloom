package com.project.response;

import com.google.gson.annotations.SerializedName;

// Model untuk mengambil data dari endpoint history
public class ChatHistoryChatBot {

    @SerializedName("prompt")
    private String prompt;

    @SerializedName("response")
    private String response;

    // Getter
    public String getPrompt() { return prompt; }
    public String getResponse() { return response; }
}