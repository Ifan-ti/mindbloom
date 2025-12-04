package com.project.mindbloom.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.model.ArticleModel;
import com.project.model.DiaryModel; // 🔥 Import DiaryModel
import com.project.mindbloom.R;
import com.project.response.DiaryDetailResponse; // 🔥 Import DiaryDetailResponse
import com.project.service.ApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormDetailActivity extends AppCompatActivity {

    // Kunci untuk menerima ID dari Adapter
    public static final String EXTRA_ARTICLE_ID = "EXTRA_ARTICLE_ID";
    public static final String EXTRA_DIARY_ID = "EXTRA_DIARY_ID";

    private TextView txtTitle, txtContent, txtAuthor, txtDate, txtCountView, txtPeninjau;

    private ImageView icon_count_view, ivCover;
    ApiService apiService = RetrofitClient.getApiService(FormDetailActivity.this);

    int articleId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Pastikan layout_form_detail adalah nama file XML Anda yang benar
        setContentView(R.layout.layout_form_detail);

        txtTitle = findViewById(R.id.txtTilte);
        txtContent = findViewById(R.id.txtContent);
        txtAuthor = findViewById(R.id.txt_author);
        txtDate = findViewById(R.id.txt_tanggal);
        txtCountView = findViewById(R.id.txt_count_view);
        icon_count_view = findViewById(R.id.icon_count_view);
        txtPeninjau = findViewById(R.id.tvPeninjau);
        ivCover = findViewById(R.id.iv_imcageCover);


        fetchArticleDetails();


        // TODO: Tambahkan listener untuk btn_back di sini
        // findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // ==================================================
    // == BAGIAN UNTUK MEMUAT ARTIKEL
    // ==================================================

    private void fetchArticleDetails() {
        Intent intent = getIntent();

        txtTitle.setText(intent.getStringExtra("title"));
        txtAuthor.setText(intent.getStringExtra("author"));
        txtContent.setText(intent.getStringExtra("content"));
        txtDate.setText(intent.getStringExtra("created_at"));
        txtCountView.setText(intent.getIntExtra("readcount",0) + " kali dibaca");
        txtPeninjau.setText("Peninjau : " + intent.getStringExtra("peninjau"));

        String base64ImageString = intent.getStringExtra("cover");

        if (base64ImageString == null || base64ImageString.isEmpty()) {
            // Tangani kasus jika string kosong atau null
            return;
        }

        try {
            // 1. Mendekode string Base64 ke byte array
            // Menggunakan flag DEFAULT atau NO_WRAP (tergantung cara encoding API)
            byte[] decodedString = Base64.decode(base64ImageString, Base64.DEFAULT);

            // 2. Mengubah byte array menjadi Bitmap
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // 3. Menampilkan Bitmap di ImageView
            ivCover.setImageBitmap(decodedByte);

        } catch (IllegalArgumentException e) {
            // Tangani jika string Base64 tidak valid
            e.printStackTrace();
        }


    }


    // ==================================================
    // == BAGIAN UNTUK MEMUAT DIARY (BARU)
    // ==================================================

    private void fetchDiaryDetails(int diaryId) {

        // Panggil method getDiaryDetail (pastikan ini ada di ApiService)
        Call<DiaryDetailResponse> call = apiService.getDiaryDetail(diaryId);

        call.enqueue(new Callback<DiaryDetailResponse>() {
            @Override
            public void onResponse(Call<DiaryDetailResponse> call, Response<DiaryDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DiaryModel diaryDetail = response.body().getData();
                    if (diaryDetail != null) {
                        updateUIFromDiary(diaryDetail); // Panggil update UI Diary
                    } else {
                        txtContent.setText("Diary tidak ditemukan.");
                    }
                } else {
                    Log.e("API_FAIL", "Respon gagal (Diary): " + response.code());
                    txtContent.setText("Gagal memuat data (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<DiaryDetailResponse> call, Throwable t) {
                Log.e("API_ERROR", "Koneksi API Diary gagal: " + t.getMessage());
                txtContent.setText("Koneksi gagal.");
            }
        });
    }

    private void updateUIFromDiary(DiaryModel diary) {
        if (txtTitle != null) {
            // Untuk Diary, kita set Judul sebagai Tanggal
            txtTitle.setText(diary.getTitle()); // Asumsi ada getEntryDate()
        }
        if (txtContent != null) {
            txtContent.setText(diary.getContent()); // Asumsi ada getContent()
            try {
                // 1. Ini adalah format INPUT (dari database: 2025-10-30T17:00:00.000Z)
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = parser.parse(diary.getEntryDate());

                // 2. Ini adalah format OUTPUT (yang Anda inginkan: 2025-10-30)
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = formatter.format(date);

                txtDate.setText(formattedDate); // ✅ Set teks dengan tanggal yang sudah diformat

            } catch (ParseException e) {
                e.printStackTrace();
                // Jika gagal format, tampilkan tanggal aslinya
                txtDate.setText(diary.getEntryDate());
            }
            txtCountView.setVisibility(TextView.INVISIBLE);
            icon_count_view.setVisibility(ImageView.INVISIBLE);
        }


    }
    private void incrementViewCount(int articleId) {

        Call<Void> call = apiService.incrementArticleView(articleId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FormDetail", "View count berhasil di-increment.");
                } else {
                    Log.w("FormDetail", "Gagal increment view count: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FormDetail", "Error koneksi saat increment view count: " + t.getMessage());
            }
        });
    }
}