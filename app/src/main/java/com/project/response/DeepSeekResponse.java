package com.project.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DeepSeekResponse {

    @SerializedName("choices")
    private List<Choice> choices;

    public String getResponseText() {
        if (choices != null && choices.size() > 0 &&
                choices.get(0).message != null) {
            return choices.get(0).message.content;
        }
        return "Maaf, Boomy sedang kesulitan memproses...";
    }

    public static class Choice {
        @SerializedName("message")
        public Message message;
    }

    public static class Message {
        @SerializedName("role")
        public String role;

        @SerializedName("content")
        public String content;
    }
}
