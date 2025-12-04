package com.project.mindbloom.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.project.mindbloom.R;

public class DetailDiaryActivity extends AppCompatActivity {

    private TextView tvDate, tvTitle, tvContent, tvAftercare;
    private ImageView imgMood;
    private ImageButton btnBack;
    private Button btnEdit;

    private int diaryId;
    private String mood;

    // Tambahkan variabel agar bisa diakses di btnEdit
    private String date;
    private String title;
    private String content;
    private String aftercare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_diary);

        tvDate = findViewById(R.id.txtDate);
        tvTitle = findViewById(R.id.txtTitle);
        tvContent = findViewById(R.id.txtContent);
        tvAftercare = findViewById(R.id.txtAftercare);
        imgMood = findViewById(R.id.imgMood);
        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);

        // Ambil data dari intent
        diaryId   = getIntent().getIntExtra("diaryId", -1);
        date      = getIntent().getStringExtra("date");
        title     = getIntent().getStringExtra("title");
        content   = getIntent().getStringExtra("content");
        mood      = getIntent().getStringExtra("mood");
        aftercare = getIntent().getStringExtra("aftercare");

        // Set UI
        tvDate.setText(date);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvAftercare.setText(aftercare);

        // Set mood icon
        if (mood != null) {
            switch (mood) {
                case "happy":
                    imgMood.setImageResource(R.drawable.moodhappy);
                    break;
                case "neutral":
                    imgMood.setImageResource(R.drawable.moodneutral);
                    break;
                case "sad":
                    imgMood.setImageResource(R.drawable.moodsad);
                    break;
                case "angry":
                    imgMood.setImageResource(R.drawable.moodangry);
                    break;
            }
        }

        // Tombol back
        btnBack.setOnClickListener(v -> finish());

        // Tombol edit
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(DetailDiaryActivity.this, uploadDiaryActivity.class);

            intent.putExtra("isEdit", true);
            intent.putExtra("diaryId", diaryId);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("mood", mood);
            intent.putExtra("date", date);
            intent.putExtra("aftercare", aftercare);

            startActivity(intent);
        });

    }
}
