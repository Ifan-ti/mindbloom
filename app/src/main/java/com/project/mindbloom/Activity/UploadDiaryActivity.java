package com.project.mindbloom;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.project.data.DiaryModel;
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

        btnSave.setOnClickListener(v -> uploadDiary());
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

        int userId = 1; // sementara DI SET 1, nanti pakai dari session login

        DiaryUploadModel diary = new DiaryUploadModel(
                userId,
                title,
                content,
                selectedMood
        );


        RetrofitClient.getApiService().uploadDiary(diary)
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
}