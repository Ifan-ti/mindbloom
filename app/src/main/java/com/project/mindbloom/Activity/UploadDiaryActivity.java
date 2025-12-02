package com.project.mindbloom.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.project.mindbloom.R;

public class UploadDiaryActivity extends AppCompatActivity {

    private ImageView imgMood;
    private LinearLayout moodPopup;
    private ImageView moodHappy, moodCalm, moodSad, moodAngry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_posting_diary);

        imgMood = findViewById(R.id.imgMood);
        moodPopup = findViewById(R.id.moodPopup);

        moodHappy = findViewById(R.id.moodHappy);
        moodCalm = findViewById(R.id.moodCalm);
        moodSad = findViewById(R.id.moodSad);
        moodAngry = findViewById(R.id.moodAngry);

        // Klik emoji utama untuk munculkan popup
        imgMood.setOnClickListener(v -> {
            if (moodPopup.getVisibility() == View.GONE) {
                moodPopup.setVisibility(View.VISIBLE);
            } else {
                moodPopup.setVisibility(View.GONE);
            }
        });

        // Set event klik tiap mood
        moodHappy.setOnClickListener(v -> setMood(R.drawable.mood1));
        moodCalm.setOnClickListener(v -> setMood(R.drawable.mood2));
        moodSad.setOnClickListener(v -> setMood(R.drawable.mood3));
        moodAngry.setOnClickListener(v -> setMood(R.drawable.mood4));
    }

    private void setMood(int drawableId) {
        imgMood.setImageResource(drawableId);
        moodPopup.setVisibility(View.GONE);
    }
}
