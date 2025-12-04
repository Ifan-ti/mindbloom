// Lokasi: com/project/data/User.java
package com.project.model;

import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("id")
    private int id;

    @SerializedName("fullNane")
    private String fullName;
    @SerializedName("username")
    private String username;

    // password_hash DIHAPUS. Jangan pernah kirim ini ke klien.
    @SerializedName("email")
    private String email;

    @SerializedName("role_id")
    private int roleId;
    @SerializedName("avatar")
    private String avatar;

    public String getFullName() {
        return fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    // Getter
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public int getRoleId() { return roleId; }
}