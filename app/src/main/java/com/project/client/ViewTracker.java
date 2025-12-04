package com.project.client;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ViewTracker {
    private static final String PREF_NAME = "ArticleViewTracker";
    private static final String KEY_VIEWED_ARTICLES = "viewed_articles";

    private SharedPreferences prefs;
    private final String prefKey; // key yang dipakai di SharedPreferences, bisa per-user: viewed_articles_{userId}

    /**
     * Default: tracking per device (semua user pada device itu berbagi key)
     */
    public ViewTracker(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.prefKey = KEY_VIEWED_ARTICLES;
    }

    /**
     * Optional: tracking per-user pada device ini.
     * Jika kamu punya userId (misal dari SessionManager), panggil new ViewTracker(context, userIdString)
     * untuk membuat key yang terpisah per user: viewed_articles_{userId}
     */
    public ViewTracker(Context context, String userId) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (userId == null || userId.isEmpty()) {
            this.prefKey = KEY_VIEWED_ARTICLES;
        } else {
            this.prefKey = KEY_VIEWED_ARTICLES + "_" + userId;
        }
    }

    /**
     * Cek apakah artikel sudah pernah dibaca (menurut penyimpanan lokal)
     */
    public boolean isArticleViewed(int articleId) {
        Set<String> viewedArticles = prefs.getStringSet(prefKey, new HashSet<String>());
        // getStringSet dapat mengembalikan reference internal; untuk cuma baca aman
        return viewedArticles.contains(String.valueOf(articleId));
    }

    /**
     * Tandai artikel sebagai sudah dibaca (disimpan ke SharedPreferences)
     */
    public void markArticleAsViewed(int articleId) {
        // Ambil copy dari set agar aman terhadap mutasi internal
        Set<String> current = new HashSet<>(prefs.getStringSet(prefKey, new HashSet<String>()));
        current.add(String.valueOf(articleId));

        prefs.edit()
                .putStringSet(prefKey, current)
                .apply();
    }

    /**
     * Hapus tracking (untuk testing/reset)
     */
    public void clearAllViews() {
        prefs.edit().remove(prefKey).apply();
    }
}