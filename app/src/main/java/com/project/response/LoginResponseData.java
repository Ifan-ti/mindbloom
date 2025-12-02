// Lokasi: com/project/respone/LoginResponseData.java (Sesuai tab Anda)
package com.project.response;

import com.google.gson.annotations.SerializedName;
import com.project.model.LoginModel; // <-- PENTING: Impor kelas LoginModel Anda

public class LoginResponseData {

    @SerializedName("token")
    private String token;

    // PASTIKAN INI MENGGUNAKAN TIPE "LoginModel"
    @SerializedName("user")
    private LoginModel user; // <-- Tipe data harus LoginModel

    // --- PASTIKAN GETTER INI ADA DAN PUBLIC ---
    public String getToken() {
        return token;
    }

    public LoginModel getUser() { // <-- Tipe kembalian harus LoginModel
        return user;
    }
}