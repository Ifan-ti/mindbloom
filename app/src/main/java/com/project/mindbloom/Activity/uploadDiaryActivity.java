package com.project.mindbloom.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.model.DiaryUploadModel;
import com.project.mindbloom.R;
import com.project.response.DiaryPostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class uploadDiaryActivity extends AppCompatActivity {

    private EditText etTitle, etContent;
    private TextView tvDate;
    private ImageView imgMood;
    private LinearLayout moodPopup;
    private ImageView moodHappy, moodNeutral, moodSad, moodAngry;
    private Button btnSave;
    private ImageButton btnBack;
    private String selectedMood = null;
    private boolean isEdit = false;
    private int diaryId = -1;

    // Nama SharedPreferences
    private static final String PREF_NAME = "USER_PREF";
    private static final String KEY_TOKEN = "token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_posting_diary);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        imgMood = findViewById(R.id.imgMood);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        tvDate = findViewById(R.id.tvDate);
        moodPopup = findViewById(R.id.moodPopup);

        moodHappy = findViewById(R.id.moodHappy);
        moodNeutral = findViewById(R.id.moodNeutral);
        moodSad = findViewById(R.id.moodSad);
        moodAngry = findViewById(R.id.moodAngry);

        // Set tanggal sekarang
        tvDate.setText(new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(new java.util.Date()));

        // Mood popup
        imgMood.setOnClickListener(v -> {
            if (moodPopup.getVisibility() == View.GONE) moodPopup.setVisibility(View.VISIBLE);
            else moodPopup.setVisibility(View.GONE);
        });

        moodHappy.setOnClickListener(v -> selectMood("happy", R.drawable.moodhappy));
        moodNeutral.setOnClickListener(v -> selectMood("neutral", R.drawable.moodneutral));
        moodSad.setOnClickListener(v -> selectMood("sad", R.drawable.moodsad));
        moodAngry.setOnClickListener(v -> selectMood("angry", R.drawable.moodangry));

        btnBack.setOnClickListener(v -> finish());

        // Cek edit mode
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            diaryId = getIntent().getIntExtra("diaryId", -1);
            etTitle.setText(getIntent().getStringExtra("title"));
            etContent.setText(getIntent().getStringExtra("content"));
            String mood = getIntent().getStringExtra("mood");
            selectedMood = mood;
            if (mood != null) setMoodImage(mood);
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

    private void setMoodImage(String mood) {
        switch (mood) {
            case "happy": imgMood.setImageResource(R.drawable.moodhappy); break;
            case "neutral": imgMood.setImageResource(R.drawable.moodneutral); break;
            case "sad": imgMood.setImageResource(R.drawable.moodsad); break;
            case "angry": imgMood.setImageResource(R.drawable.moodangry); break;
        }
    }

    // ---------------------------
    // SharedPreferences Token
    // ---------------------------
    private void saveToken(String token) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    private void clearToken() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
    }

    // ---------------------------
    // Upload Diary
    // ---------------------------
    private void uploadDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getToken();
        if (token == null) {
            Toast.makeText(this, "Token tidak tersedia, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryUploadModel diary = new DiaryUploadModel(title, content, selectedMood);

        RetrofitClient.getApiService().uploadDiary("Bearer " + token, diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(uploadDiaryActivity.this, "Diary tersimpan!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(uploadDiaryActivity.this, "Gagal upload: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Toast.makeText(uploadDiaryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ---------------------------
    // Update Diary
    // ---------------------------
    private void updateDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = getToken();
        if (token == null) {
            Toast.makeText(this, "Token tidak tersedia, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        DiaryUploadModel diary = new DiaryUploadModel(title, content, selectedMood);

        RetrofitClient.getApiService().updateDiary("Bearer " + token, diaryId, diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(uploadDiaryActivity.this, "Diary berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(uploadDiaryActivity.this, "Gagal update: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Toast.makeText(uploadDiaryActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
