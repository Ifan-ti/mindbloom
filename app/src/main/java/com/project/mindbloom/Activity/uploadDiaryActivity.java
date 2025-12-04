package com.project.mindbloom.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.project.mindbloom.R;
import com.project.model.DiaryModel;
import com.project.data.DiaryUploadModel;
import com.project.response.DiaryPostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class uploadDiaryActivity extends AppCompatActivity {

    private ImageView imgMood;
    private LinearLayout moodPopup;
    private ImageView moodHappy, moodNeutral, moodSad, moodAngry;
    private EditText etTitle, etContent;
    private TextView tvDate;
    private Button btnSave;
    private ImageButton btnBack;

    private String selectedMood = null;
    private String happy, neutral, sad, angry;

    private boolean isEdit = false;
    private int diaryId = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_posting_diary);

        imgMood = findViewById(R.id.imgMood);
        moodPopup = findViewById(R.id.moodPopup);

        moodHappy = findViewById(R.id.moodHappy);
        moodNeutral = findViewById(R.id.moodNeutral);
        moodSad = findViewById(R.id.moodSad);
        moodAngry = findViewById(R.id.moodAngry);

        happy = "happy";
        neutral = "neutral";
        sad = "sad";
        angry = "angry";

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        tvDate = findViewById(R.id.tvDate);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Date auto
        String currentDate = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // open mood picker
        imgMood.setOnClickListener(v -> {
            if (moodPopup.getVisibility() == LinearLayout.GONE)
                moodPopup.setVisibility(LinearLayout.VISIBLE);
            else
                moodPopup.setVisibility(LinearLayout.GONE);
        });

        moodHappy.setOnClickListener(v -> selectMood(happy, R.drawable.moodhappy));
        moodNeutral.setOnClickListener(v -> selectMood(neutral, R.drawable.moodneutral));
        moodSad.setOnClickListener(v -> selectMood(sad, R.drawable.moodsad));
        moodAngry.setOnClickListener(v -> selectMood(angry, R.drawable.moodangry));

        // 🟦 Cek apakah masuk mode "Edit"
        Intent i = getIntent();
        isEdit = i.getBooleanExtra("isEdit", false);

        if (isEdit) {
            diaryId = i.getIntExtra("diaryId", -1);

            etTitle.setText(i.getStringExtra("title"));
            etContent.setText(i.getStringExtra("content"));

            String mood = i.getStringExtra("mood");
            selectedMood = mood;

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

            tvDate.setText(i.getStringExtra("date")); // date dari detail

            btnSave.setText("Update Diary");
        }

        btnSave.setOnClickListener(v -> {
            if (isEdit) updateDiary();
            else uploadDiary();
        });
    }


    private void selectMood(String mood, int imageRes) {
        selectedMood = mood;
        imgMood.setImageResource(imageRes);
        moodPopup.setVisibility(LinearLayout.GONE);
    }


    private void uploadDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String date = tvDate.getText().toString();

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = 1;

        DiaryUploadModel diary = new DiaryUploadModel(
                userId,
                title,
                content,
                selectedMood
        );


        RetrofitClient.getApiService(this).uploadDiary(diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Diary tersimpan!",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Gagal menyimpan: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Toast.makeText(uploadDiaryActivity.this,
                                "Error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    // 🟦 Tambahan: fungsi UPDATE diary
    private void updateDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryUploadModel diary = new DiaryUploadModel(
                1,
                title,
                content,
                selectedMood
        );

        RetrofitClient.getApiService(this).updateDiary(diaryId, diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Diary berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Gagal update: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Toast.makeText(uploadDiaryActivity.this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
