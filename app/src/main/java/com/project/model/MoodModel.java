package com.project.model; // Sesuaikan package kamu

import com.google.gson.annotations.SerializedName;
import com.project.mindbloom.R; // Pastikan import R file project kamu
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodModel {

    // Sesuaikan dengan nama kolom di database / key JSON dari PHP
    @SerializedName("entry_date")
    private String entryDate;

    @SerializedName("mood_tag")
    private String moodTag;

    // Constructor
    public MoodModel(String entryDate, String moodTag) {
        this.entryDate = entryDate;
        this.moodTag = moodTag;
    }

    // Getter
    public String getEntryDate() {
        return entryDate;
    }

    public String getMoodTag() {
        return moodTag;
    }

    // =================================================================
    // HELPER METHODS (Logika untuk Grafik)
    // =================================================================

    /**
     * Mengubah String mood menjadi Angka (Score) untuk posisi Sumbu Y di Grafik
     * 3.0 = Positif (Atas)
     * 2.0 = Netral (Tengah)
     * 1.0 = Negatif (Bawah)
     */
    public float getMoodScore() {
        if (moodTag == null) return 2f; // Default netral jika null

        switch (moodTag.toLowerCase()) {
            // Zona Positif
            case "happy":
            case "excited":
                return 3f;

            // Zona Netral / Tenang
            case "neutral":
            case "relaxed":
                return 2f;

            // Zona Negatif
            case "sad":
            case "anxious":
            case "angry":
                return 1f;

            default:
                return 2f;
        }
    }

    /**
     * Mengambil Resource ID Gambar (Drawable) berdasarkan mood.
     * Pastikan kamu sudah punya gambar di res/drawable dengan nama sesuai.
     */
    public int getMoodIconResId() {
        if (moodTag == null) return R.drawable.moodhappy; // Gambar default

        switch (moodTag.toLowerCase()) {
            case "happy":
                return R.drawable.moodhappy;
            case "neutral":
                return R.drawable.moodneutral;
            case "sad":
                return R.drawable.moodsad;
            case "angry":
                return R.drawable.moodangry;
            default:
                return R.drawable.moodhappy;
        }
    }

    /**
     * Mengambil HANYA tanggalnya saja (misal: "2023-11-22" -> "22")
     * Untuk label di bagian bawah grafik (Sumbu X).
     */
    public String getDayLabel() {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(this.entryDate);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return entryDate; // Jika error parsing, kembalikan string aslinya
        }
    }
}