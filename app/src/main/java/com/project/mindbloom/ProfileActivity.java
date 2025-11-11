package com.project.mindbloom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        sessionManager = new SessionManager(this);

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Hapus session
            sessionManager.clearSession(); // atau sessionManager.clearSession();

            // Arahkan ke Login_Activity dan clear back stack
            Intent i = new Intent(ProfileActivity.this, ActivityMain.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            // tidak perlu finish() karena CLEAR_TASK sudah membersihkan stack
        });
    }
}
