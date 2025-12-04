package com.project.request;

import java.util.ArrayList;
import java.util.List;

public class AI_AfterCareRequest {

    public String model;
    public List<Message> messages;

    /**
     * Constructor untuk membuat request Aftercare
     * @param diaryContent Isi diary pengguna
     * @param mood Mood pengguna (happy, sad, neutral, angry)
     */
    public AI_AfterCareRequest(String diaryContent, String mood) {
        this.model = "deepseek/deepseek-chat";

        this.messages = new ArrayList<>();

        // SYSTEM: instruksi AI Aftercare
        this.messages.add(new Message("system",
                "Anda adalah Boomy, pendamping kesehatan mental yang empatik. " +
                        "Tugas Anda adalah membuat Aftercare singkat dari diary pengguna berdasarkan konten diary dan mood mereka. " +
                        "Aftercare bersifat:\n" +
                        "- Memberikan dukungan emosional\n" +
                        "- Memvalidasi perasaan pengguna\n" +
                        "- Memberikan saran ringan untuk self-care, journaling, teknik pernapasan, atau refleksi diri\n" +
                        "- Tidak memberikan diagnosis atau obat\n" +
                        "- Menggunakan bahasa Indonesia hangat, lembut, dan mudah dimengerti\n" +
                        "- Bisa menambahkan emoji agar lebih personal\n" +
                        "Rules:\n" +
                        "1. Buat Aftercare berdasar konten diary dan mood yang diberikan.\n" +
                        "2. Panjang maksimal 3 paragraf.\n" +
                        "3. Gunakan pertanyaan reflektif.\n" +
                        "4. Bersifat menenangkan, tidak menghakimi."
        ));

        // USER: diary dan mood
        this.messages.add(new Message("user", "Diary: " + diaryContent + "\nMood: " + mood));
    }

    // Inner class Message
    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
