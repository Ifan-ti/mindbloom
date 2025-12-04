// Lokasi: com/project/client/SessionManager.java
package com.project.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {

    private static final String PREF_NAME = "MindbloomLoginPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY__USER_USERNAME = "username";
    private static final String KEY__USER_FULLNAE = "fullname";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_ROLE_ID = "role_id";
    private static final String KEY_USER_AVATAR = "avatar";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveLoginSession(String token, int userId, String fullname, String username, String email, int roleId, String avatar) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_AUTH_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY__USER_FULLNAE, fullname);
        editor.putString(KEY__USER_USERNAME, username);
        editor.putString(KEY_USER_AVATAR, avatar);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putInt(KEY_ROLE_ID, roleId);
        editor.commit(); // Gunakan apply() untuk proses background
    }


    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public String getAuthToken() {
        return pref.getString(KEY_AUTH_TOKEN, null);
    }
    public int getRoleId() {
        return pref.getInt(KEY_ROLE_ID, -1);
    }
    public String getUsername() {
        return pref.getString(KEY__USER_USERNAME,null);
    }
    public String getFullName() {
        return pref.getString(KEY__USER_FULLNAE,null);
    }
    public String getAvatar() {
        return pref.getString(KEY_USER_AVATAR,null);
    }
    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }
    public void clearSession() {
        editor.clear();
        editor.commit();
    }


}