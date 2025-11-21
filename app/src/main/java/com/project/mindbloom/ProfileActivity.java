package com.project.mindbloom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_profile);

        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnEditProfile = findViewById(R.id.btnEditProfile);

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        ImageButton navHome = findViewById(R.id.navHome);
        ImageButton navPeople = findViewById(R.id.navPeople);
        ImageButton navCalendar = findViewById(R.id.navCalendar);
        ImageButton navChat = findViewById(R.id.navChat);
        ImageButton navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, Homepage_Activity.class))
        );

        navPeople.setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ProfileActivity.class))
        );

        // Jika kamu sudah berada di ProfileActivity, kamu bisa disable tombolnya
        navProfile.setOnClickListener(v -> {
            // Optional: tampilkan toast bahwa sudah di halaman profil
            // Toast.makeText(this, "You're already here", Toast.LENGTH_SHORT).show();
        });
    }
}


