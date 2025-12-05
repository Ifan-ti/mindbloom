package com.project.mindbloom.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager; // ✅ Import SessionManager
import com.project. model.DiaryUploadModel;
import com.project.mindbloom.R;
import com.project.response.DiaryPostResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class uploadDiaryActivity extends AppCompatActivity {

    private static final String TAG = "UploadDiary";

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

    // ✅ Gunakan SessionManager
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_posting_diary);

        // ✅ Initialize SessionManager
        sessionManager = new SessionManager(this);

        etTitle = findViewById(R.id. etTitle);
        etContent = findViewById(R.id.etContent);
        imgMood = findViewById(R.id.imgMood);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id. btnBack);
        tvDate = findViewById(R.id.tvDate);
        moodPopup = findViewById(R.id.moodPopup);

        moodHappy = findViewById(R. id.moodHappy);
        moodNeutral = findViewById(R.id.moodNeutral);
        moodSad = findViewById(R.id. moodSad);
        moodAngry = findViewById(R. id.moodAngry);

        // Set tanggal sekarang
        tvDate.setText(new java.text.SimpleDateFormat("dd MMM yyyy",
                java.util.Locale.getDefault()).format(new java.util.Date()));

        // Mood popup
        imgMood.setOnClickListener(v -> {
            if (moodPopup.getVisibility() == View.GONE)
                moodPopup.setVisibility(View.VISIBLE);
            else
                moodPopup.setVisibility(View.GONE);
        });

        moodHappy.setOnClickListener(v -> selectMood("happy", R.drawable.moodhappy));
        moodNeutral.setOnClickListener(v -> selectMood("neutral", R.drawable.moodneutral));
        moodSad.setOnClickListener(v -> selectMood("sad", R.drawable.moodsad));
        moodAngry.setOnClickListener(v -> selectMood("angry", R.drawable.moodangry));

        btnBack.setOnClickListener(v -> finish());

        // Cek edit mode
        isEdit = getIntent().getBooleanExtra("isEdit", false);
        if (isEdit) {
            diaryId = getIntent(). getIntExtra("diaryId", -1);
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

    // ✅ PERBAIKAN: Upload Diary
    private void uploadDiary() {
        String title = etTitle. getText().toString().trim();
        String content = etContent.getText(). toString().trim();

        // Validasi input
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Ambil token dari SessionManager
        String token = sessionManager.getAuthToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token tidak tersedia, silakan login ulang", Toast.LENGTH_SHORT). show();
            Log.e(TAG, "Token is null or empty");
            return;
        }

        Log.d(TAG, "Token: " + token. substring(0, Math.min(20, token.length())) + "...");
        Log.d(TAG, "Uploading diary - Title: " + title + ", Mood: " + selectedMood);

        // Buat object diary
        DiaryUploadModel diary = new DiaryUploadModel(title, content, selectedMood);

        // Panggil API
        RetrofitClient.getApiService(this)
                .uploadDiary("Bearer " + token, diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log. d(TAG, "Upload success: " + response.body().getMessage());
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Diary tersimpan!", Toast.LENGTH_SHORT).show();
                            finish(); // ✅ Tutup activity setelah berhasil
                        } else {
                            // ✅ Tambahkan log error body
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.e(TAG, "Upload failed - Code: " + response.code() +
                                    ", Error: " + errorBody);

                            Toast.makeText(uploadDiaryActivity.this,
                                    "Gagal upload: " + response.code() + " - " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Log.e(TAG, "Upload error: " + t.getMessage(), t);
                        Toast.makeText(uploadDiaryActivity. this,
                                "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ✅ PERBAIKAN: Update Diary
    private void updateDiary() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || content. isEmpty()) {
            Toast.makeText(this, "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMood == null) {
            Toast.makeText(this, "Pilih mood dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Ambil token dari SessionManager
        String token = sessionManager.getAuthToken();

        if (token == null || token.isEmpty()) {
            Toast. makeText(this, "Token tidak tersedia, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        if (diaryId == -1) {
            Toast.makeText(this, "ID diary tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Updating diary ID: " + diaryId);

        DiaryUploadModel diary = new DiaryUploadModel(title, content, selectedMood);

        RetrofitClient.getApiService(this)
                .updateDiary("Bearer " + token, diaryId, diary)
                .enqueue(new Callback<DiaryPostResponse>() {
                    @Override
                    public void onResponse(Call<DiaryPostResponse> call, Response<DiaryPostResponse> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Update success");
                            Toast.makeText(uploadDiaryActivity.this,
                                    "Diary berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String errorBody = "";
                            try {
                                if (response.errorBody() != null) {
                                    errorBody = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.e(TAG, "Update failed - Code: " + response.code() +
                                    ", Error: " + errorBody);

                            Toast.makeText(uploadDiaryActivity.this,
                                    "Gagal update: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DiaryPostResponse> call, Throwable t) {
                        Log.e(TAG, "Update error: " + t.getMessage(), t);
                        Toast.makeText(uploadDiaryActivity. this,
                                "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}