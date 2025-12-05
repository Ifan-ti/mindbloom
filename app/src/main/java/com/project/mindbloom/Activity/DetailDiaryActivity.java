package com.project.mindbloom.Activity;

import static android.view.View.GONE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.mindbloom.R;
import com.project.request.AftercareRequest;
import com.project.response.AfterCareResponse;
import com.project.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailDiaryActivity extends AppCompatActivity {

    private TextView tvDate, tvTitle, tvContent, tvAftercare;
    private ImageView imgMood;
    private int diaryId;
    private String content;
    Button btnEdit;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_diary);

        tvDate = findViewById(R.id.txtDate);
        tvTitle = findViewById(R.id.txtTitle);
        tvContent = findViewById(R.id.txtContent);
        tvAftercare = findViewById(R.id.txtAftercare);
        imgMood = findViewById(R.id.imgMood);
        layout = findViewById(R.id.aftercareBox);
        btnEdit = findViewById(R.id.btnEdit);

        diaryId = getIntent().getIntExtra("diaryId", -1);
        String date = getIntent().getStringExtra("date");
        String title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        String mood = getIntent().getStringExtra("mood");

        tvDate.setText(date);
        tvTitle.setText(title);
        tvContent.setText(content);
        tvAftercare.setText("Sedang memuat aftercare...");

        layout.setVisibility(GONE);
        btnEdit.setVisibility(GONE);



        setMoodImage(mood);

        // Panggil API Aftercare
        getAftercareFromAI();
    }

    private void setMoodImage(String mood) {
        if (mood == null) return;
        switch (mood) {
            case "happy": imgMood.setImageResource(R.drawable.moodhappy); break;
            case "neutral": imgMood.setImageResource(R.drawable.moodneutral); break;
            case "sad": imgMood.setImageResource(R.drawable.moodsad); break;
            case "angry": imgMood.setImageResource(R.drawable.moodangry); break;
        }
    }

    private String getToken() {
        SharedPreferences prefs = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    private void getAftercareFromAI() {
        String token = getToken();
        if (token == null) {
            tvAftercare.setText("Token tidak tersedia. Silakan login ulang.");
            return;
        }

        AftercareRequest request = new AftercareRequest(diaryId, content);
        RetrofitClient.getApiService(this).generateAftercare("Bearer " + token, request)
                .enqueue(new Callback<AfterCareResponse>() {
                    @Override
                    public void onResponse(Call<AfterCareResponse> call, Response<AfterCareResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            tvAftercare.setText(response.body().getAftercareText());
                        } else {
                            tvAftercare.setText("Gagal memuat Aftercare. Coba lagi nanti.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AfterCareResponse> call, Throwable t) {
                        tvAftercare.setText("Error: " + t.getMessage());
                    }
                });
    }
}
