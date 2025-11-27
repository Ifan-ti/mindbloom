package com.project.mindbloom.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.model.ArticleModel;
import com.project.model.DiaryModel; // 🔥 Import DiaryModel
import com.project.mindbloom.R;
import com.project.response.ArticleDetailResponse;
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

    private TextView txtTitle;
    private TextView txtContent;
    private TextView txtAuthor;
    private TextView txtDate;
    private TextView txtCountView;
    private ImageView icon_count_view;
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




        // --- Blok Pengambilan ID ---
        // Kita periksa kedua kemungkinan ID
        articleId = getIntent().getIntExtra(EXTRA_ARTICLE_ID, -1);
        String diaryIdString = getIntent().getStringExtra("EXTRA_DIARY_ID");
        int diaryId = -1; // Nilai default jika konversi gagal
        try {
            diaryId = Integer.parseInt(diaryIdString);
        } catch (NumberFormatException e) {
            e.printStackTrace(); // Tangani error jika string-nya bukan angka
        }

        // --- 🔥 LOGIKA UTAMA: Panggil API yang Sesuai ---
        if (articleId != -1) {
            // Jika ada ID Artikel, panggil API Artikel
            Log.d("FormDetail", "ID Artikel yang diterima: " + articleId);
            fetchArticleDetails(articleId);

        } else if (diaryId != -1) {
            // Jika ada ID Diary, panggil API Diary
            Log.d("FormDetail", "ID Diary yang diterima: " + diaryId);
            fetchDiaryDetails(diaryId);

        } else {
            // Jika tidak ada ID sama sekali
            Log.e("FormDetail", "Tidak ada ID (Artikel atau Diary) yang diterima.");
            Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            finish();
        }

        // TODO: Tambahkan listener untuk btn_back di sini
        // findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    // ==================================================
    // == BAGIAN UNTUK MEMUAT ARTIKEL
    // ==================================================

    private void fetchArticleDetails(int articleId) {
        Call<ArticleDetailResponse> call = apiService.getArticleDetail(articleId);

        call.enqueue(new Callback<ArticleDetailResponse>() {
            @Override
            public void onResponse(Call<ArticleDetailResponse> call, Response<ArticleDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArticleModel articleDetail = response.body().getData();
                    if (articleDetail != null) {
                        updateUIFromArticle(articleDetail); // Panggil update UI Artikel
                        incrementViewCount(articleId);
                    } else {
                        txtContent.setText("Artikel tidak ditemukan.");
                    }
                } else {
                    Log.e("API_FAIL", "Respon gagal (Artikel): " + response.code());
                    txtContent.setText("Gagal memuat data (Code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<ArticleDetailResponse> call, Throwable t) {
                Log.e("API_ERROR", "Koneksi API Artikel gagal: " + t.getMessage());
                txtContent.setText("Koneksi gagal.");
            }
        });
    }

    private void updateUIFromArticle(ArticleModel article) {
        if (txtTitle != null) {
            txtTitle.setText(article.getTitle());
        }
        if (txtContent != null) {
            // TODO: Nanti ganti getExcerpt() dengan getter konten penuh
            txtContent.setText(article.getContent());
            txtAuthor.setText(article.getAuthor());
            try {
                // 1. Ini adalah format INPUT (dari database: 2025-10-30T17:00:00.000Z)
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = parser.parse(article.getDate());

                // 2. Ini adalah format OUTPUT (yang Anda inginkan: 2025-10-30)
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = formatter.format(date);

                txtDate.setText(formattedDate); // ✅ Set teks dengan tanggal yang sudah diformat

            } catch (ParseException e) {
                e.printStackTrace();
                // Jika gagal format, tampilkan tanggal aslinya
                txtDate.setText(article.getDate());
            }
            txtCountView.setText(article.getReadCount() + " kali dibaca");
            txtCountView.setVisibility(TextView.VISIBLE);
            icon_count_view.setVisibility(ImageView.VISIBLE);

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
            txtAuthor.setText(diary.getAuthor());
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