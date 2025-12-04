package com.project.mindbloom.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.inputmethod.InputMethodManager;


import android.content.Context;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Collections;
import java.util.Comparator;


import com.project.adapter.ScrollDiaryAdapter;
import com.project.adapter.SlideDiaryAdapter;
import com.project.adapter.dateAdapter;
import com.project.adapter.ScrollArtikelAdapter;
import com.project.adapter.SlideArtikelAdapter;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.model.ArticleModel;
import com.project.model.DiaryModel;
import com.project.model.dateModel;

import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutHomepageBinding;
import com.project.response.ArticlesResponse;
import com.project.response.DiaryRespone;
import com.project.response.UserResponse;
import com.project.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Homepage_Activity extends AppCompatActivity {

    private SlideArtikelAdapter rekomendasiartikeladapter;
    private SlideDiaryAdapter slidediaryadaptert;
    private ScrollArtikelAdapter popularArticleAdapter;
    private ScrollDiaryAdapter scrolldiaryadapter;
    private List<ArticleModel> fullArticleList = new ArrayList<>();
    private List<DiaryModel> fullDiaryList = new ArrayList<>();




    private LayoutHomepageBinding binding;
    private dateAdapter tanggalAdapter;
    private List<dateModel> tanggalList = new ArrayList<>();
    private List<ImageView> articleIndicators = new ArrayList<>();

    private boolean isSearchMode = false;


    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = RetrofitClient.getApiService(this);

        SessionManager sm = new SessionManager(this);
        if (!sm.isLoggedIn()) {
            Intent i = new Intent(this, LoginFragmentActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            return;
        }
        Intent loginIntent = getIntent();

        binding = LayoutHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // INISIALISASI ADAPTER POPULER (VERTICAL)
        popularArticleAdapter = new ScrollArtikelAdapter(this);
        binding.recyclerViewPopularArticles.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewPopularArticles.setAdapter(popularArticleAdapter);

        // 🔥 INISIALISASI ADAPTER SLIDER (HORIZONTAL) 🔥
        rekomendasiartikeladapter = new SlideArtikelAdapter(this);
        binding.viewPagerArticleSlider.setAdapter(rekomendasiartikeladapter);

        // INISIALISASI ADAPTER HISTORY (VERTICAL)
        scrolldiaryadapter = new ScrollDiaryAdapter(this);

        binding.recyclerViewDiaryHistory.setAdapter(scrolldiaryadapter);

        slidediaryadaptert = new SlideDiaryAdapter(this);
        binding.viewPagerDiarySlider.setAdapter(slidediaryadaptert);





        setupTanggalList();
        showArtikelView();
        setupTabListeners();
        setupSearchListener();
        setupNavBarListeners();

        fetchUsernameNavatar();





    }

    private void setupTanggalList() {
        // 1. Siapkan data (7 hari, hari ini di tengah)
        tanggalList.clear();
        LocalDate today = LocalDate.now(); // Tanggal hari ini
        Locale localeID = new Locale("id", "ID"); // Format Indonesia

        DateTimeFormatter formatTanggal = DateTimeFormatter.ofPattern("d", localeID);
        DateTimeFormatter formatBulan = DateTimeFormatter.ofPattern("MMM", localeID);

        // Loop 7 hari: 3 hari kebelakang, hari ini, 3 hari kedepan
        for (int i = -3; i <= 3; i++) {
            LocalDate tanggal = today.plusDays(i);

            String angka = tanggal.format(formatTanggal);
            String bulan = tanggal.format(formatBulan);

            // Tandai hari ini (i == 0)
            boolean isToday = (i == 0);

            tanggalList.add(new dateModel(angka, bulan, isToday));
        }

        // 2. Siapkan Adapter
        tanggalAdapter = new dateAdapter(this, tanggalList);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                // Ini akan mematikan fungsi scroll horizontal
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                // (Opsional, tapi bagus untuk konsistensi)
                return false;
            }
        };

        binding.recyclerViewDates.setLayoutManager(manager); // Set manager yang sudah dimodifikasi
        binding.recyclerViewDates.setAdapter(tanggalAdapter);

        // 3. Atur RecyclerView
        // (LayoutManager sudah diatur di XML, tapi ini lebih aman)

    }
    private void setupTabListeners() {
        // Listener untuk tombol Artikel
        binding.btnArtikel.setOnClickListener(v -> showArtikelView());

        // Listener untuk tombol Diary
        binding.btnDiary.setOnClickListener(v -> showDiaryView());
    }
    private void showArtikelView() {
        // 1. ATUR STYLE TOMBOL & HINT
        binding.SearchInput.setHint("Cari Artikel Menarik Hari ini");
        binding.btnArtikel.setBackgroundResource(R.drawable.clickbtn);
        binding.btnArtikel.setTextColor(ContextCompat.getColor(this, R.color.inactive_light_blue));
        binding.btnDiary.setBackgroundResource(R.drawable.frame_home);
        binding.btnDiary.setTextColor(ContextCompat.getColor(this, R.color.main_blue));

        // 2. ATUR VISIBILITY LIST
        binding.recyclerViewPopularArticles.setVisibility(View.VISIBLE);
        binding.recyclerViewDiaryHistory.setVisibility(View.INVISIBLE);

        // 3. PERIKSA MODE SEARCH
        if (isSearchMode) {
            // Jika sedang mencari, JANGAN reset layout.
            // Cukup panggil ulang filter untuk list artikel.
            filterLists(binding.SearchInput.getText().toString());
            return; // Selesai. Jangan jalankan kode di bawah.
        }

        // --- Kode di bawah ini HANYA berjalan jika TIDAK sedang search ---

        isSearchMode = false; // Set ulang (meskipun sudah false)
        binding.SearchInput.setText(""); // Hapus teks pencarian

        // TAMPILKAN semua view Artikel
        binding.recyclerViewDates.setVisibility(View.VISIBLE);
        binding.rekomendasiArtikel.setVisibility(View.VISIBLE);
        binding.viewPagerArticleSlider.setVisibility(View.VISIBLE);
        binding.ArtikelPopuler.setVisibility(View.VISIBLE);
        binding.SlideIndicator.setVisibility(View.VISIBLE);

        // SEMBUNYIKAN semua view Diary
        binding.diaryHariIni.setVisibility(View.INVISIBLE);
        binding.viewPagerDiarySlider.setVisibility(View.INVISIBLE);
        binding.riwayatDiary.setVisibility(View.INVISIBLE);

        // Atur ulang tinggi layout (untuk mode non-search)
        ConstraintLayout.LayoutParams articleParams = (ConstraintLayout.LayoutParams) binding.recyclerViewPopularArticles.getLayoutParams();
        articleParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        binding.recyclerViewPopularArticles.setLayoutParams(articleParams);

        fetchArticlesPopuler();
        fetchArticlesNew();
    }
    private void showDiaryView() {
        // 1. ATUR STYLE TOMBOL & HINT
        binding.SearchInput.setHint("Cari Diary Yang Kamu Inginkan");
        binding.btnArtikel.setBackgroundResource(R.drawable.frame_home);
        binding.btnArtikel.setTextColor(ContextCompat.getColor(this, R.color.main_blue));
        binding.btnDiary.setBackgroundResource(R.drawable.clickbtn);
        binding.btnDiary.setTextColor(ContextCompat.getColor(this, R.color.inactive_light_blue));

        // 2. ATUR VISIBILITY LIST
        binding.recyclerViewPopularArticles.setVisibility(View.INVISIBLE);
        binding.recyclerViewDiaryHistory.setVisibility(View.VISIBLE);

        // 3. PERIKSA MODE SEARCH
        if (isSearchMode) {
            // Jika sedang mencari, JANGAN reset layout.
            // Cukup panggil ulang filter untuk list diary.
            filterLists(binding.SearchInput.getText().toString());
            return; // Selesai. Jangan jalankan kode di bawah.
        }

        // --- Kode di bawah ini HANYA berjalan jika TIDAK sedang search ---

        isSearchMode = false;
        binding.SearchInput.setText("");

        // SEMBUNYIKAN semua view Artikel
        binding.recyclerViewDates.setVisibility(View.VISIBLE);
        binding.rekomendasiArtikel.setVisibility(View.INVISIBLE);
        binding.viewPagerArticleSlider.setVisibility(View.INVISIBLE);
        binding.ArtikelPopuler.setVisibility(View.INVISIBLE);

        // TAMPILKAN semua view Diary
        binding.diaryHariIni.setVisibility(View.VISIBLE);
        binding.viewPagerDiarySlider.setVisibility(View.VISIBLE);
        binding.riwayatDiary.setVisibility(View.VISIBLE);
        binding.SlideIndicator.setVisibility(View.VISIBLE);

        // 🔥 PERBAIKAN BUG TAMBAHAN:
        // Di kode lama, Anda mengubah 'articleParams' padahal ini 'showDiaryView'.
        // Seharusnya Anda mengubah 'diaryParams'.
        ConstraintLayout.LayoutParams diaryParams = (ConstraintLayout.LayoutParams) binding.recyclerViewDiaryHistory.getLayoutParams();
        diaryParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        binding.recyclerViewDiaryHistory.setLayoutParams(diaryParams);

        binding.recyclerViewDiaryHistory.setNestedScrollingEnabled(false);
        binding.recyclerViewDiaryHistory.setHasFixedSize(true);

        fetchDiary();
    }
    // Homepage_Activity.java
    private void fetchArticlesPopuler() {
        Call<ArticlesResponse> call = apiService.getArticlesPopuler();
        // HANYA SATU PANGGILAN API UNTUK MENGISI KEDUA ADAPTER DAN INDIKATOR
        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArticleModel> articles = response.body().getData();
                    fullArticleList = new ArrayList<>(articles);


                        if (articles != null && !articles.isEmpty()) {
                            Log.d("API_SUCCESS", "Total Artikel: " + articles.size());
                            List<ArticleModel> listPopuler = new ArrayList<>(articles);
                            // 2. ISI ADAPTER REKOMENDASI SLIDER (ViewPager)
                            popularArticleAdapter.setData(listPopuler);

                            // 🔥 3. PANGGIL SETUP INDICATOR 🔥
                            setupIndicators(listPopuler.size());

                    } else {
                        Log.w("API_EMPTY", "Data artikel kosong.");
                    }
                } else {
                    // Respons Gagal (misal status 404, 500 dari server)
                    Log.e("API_FAIL", "Respon gagal: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                Log.e("API_ERROR", "Koneksi ke API Artikel gagal: " + t.getMessage());
            }
        });
    }
    private void fetchArticlesNew() {
        Call<ArticlesResponse> call = apiService.getArticlesNew();

        // HANYA SATU PANGGILAN API UNTUK MENGISI KEDUA ADAPTER DAN INDIKATOR
        call.enqueue(new Callback<ArticlesResponse>() {
            @Override
            public void onResponse(Call<ArticlesResponse> call, Response<ArticlesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArticleModel> articles = response.body().getData();


                    if (articles != null && !articles.isEmpty()) {
                        Log.d("API_SUCCESS", "Total Artikel: " + articles.size());
                        List<ArticleModel> listNew = new ArrayList<>(articles);
                        // 2. ISI ADAPTER REKOMENDASI SLIDER (ViewPager)
                        rekomendasiartikeladapter.setData(listNew);

                        // 🔥 3. PANGGIL SETUP INDICATOR 🔥
                        setupIndicators(listNew.size());

                    } else {
                        Log.w("API_EMPTY", "Data artikel kosong.");
                    }
                } else {
                    // Respons Gagal (misal status 404, 500 dari server)
                    Log.e("API_FAIL", "Respon gagal: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ArticlesResponse> call, Throwable t) {
                Log.e("API_ERROR", "Koneksi ke API Artikel gagal: " + t.getMessage());
            }
        });
    }
    private void fetchDiary() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Silakan login ulang (token tidak ditemukan)", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<DiaryRespone> call = apiService.getMyDiary("Bearer " + token);

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String tanggalHariIni = today.format(formatter);

        call.enqueue(new Callback<DiaryRespone>() {
            @Override
            public void onResponse(Call<DiaryRespone> call, Response<DiaryRespone> response) {

                List<DiaryModel> listRiwayat = new ArrayList<>();
                List<DiaryModel> listDiaryHariIni = new ArrayList<>();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                    List<DiaryModel> diaries = response.body().getData();
                    fullDiaryList = new ArrayList<>(diaries);
                    Log.d("API_SUCCESS", "Total Diary: " + fullDiaryList.size());

                    // --- 2. Siapkan data "Riwayat Diary" (Sorted) ---
                    listRiwayat = new ArrayList<>(fullDiaryList);
                    Collections.sort(listRiwayat, (a1, a2) -> {
                        if (a1.getEntryDate() == null || a2.getEntryDate() == null) return 0;
                        return a2.getEntryDate().compareTo(a1.getEntryDate());
                    });

                    // --- 3. Siapkan data "Diary Hari Ini" (Filtered) ---
                    for (DiaryModel diary : fullDiaryList) {
                        if (diary.getEntryDate() != null && diary.getEntryDate().equals(tanggalHariIni)) {
                            listDiaryHariIni.add(diary);
                        }
                    }

                } else if (response.code() != 404) {
                    // Tangani error jika bukan "Not Found"
                    Toast.makeText(Homepage_Activity.this,
                            "Gagal memuat diary. Code: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
                // Jika 404 atau data null, list akan otomatis kosong (sudah benar)


                // --- 4. SET DATA KE ADAPTER ---
                // Adapter akan menangani jika listRiwayat atau listDiaryHariIni kosong
                scrolldiaryadapter.setData(listRiwayat);
                slidediaryadaptert.setData(listDiaryHariIni);


                // 🔥 5. PERBAIKAN: ATUR INDIKATOR
                // Kita hanya ingin indikator muncul jika ada LEBIH DARI 1 diary.
                // Adapter (di langkah 2) akan menampilkan 1 card "kosong",
                // jadi kita cek list aslinya.
                if (listDiaryHariIni.size() > 1) {
                    binding.SlideIndicator.setVisibility(View.VISIBLE);
                    setupIndicators(listDiaryHariIni.size());
                } else {
                    // Sembunyikan jika list kosong ATAU hanya ada 1 item
                    binding.SlideIndicator.setVisibility(View.GONE);
                }

                // Pastikan ViewPager selalu terlihat
                binding.viewPagerDiarySlider.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<DiaryRespone> call, Throwable t) {
                Toast.makeText(Homepage_Activity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("Diary_API", "onFailure: " + t.getMessage());
            }
        });
    }
    private void fetchUsernameNavatar(){
        SessionManager sessionManager = new SessionManager(this);

        binding.UsernameUser.setText("Hallo, " + sessionManager.getUsername());

        String base64ImageString = sessionManager.getAvatar();



        if (base64ImageString == null || base64ImageString.isEmpty()) {
            // Tangani kasus jika string kosong atau null
            Log.d("IMAGE", "IMAGE: " + base64ImageString);
            return;
        }

        try {
            // 1. Mendekode string Base64 ke byte array
            // Menggunakan flag DEFAULT atau NO_WRAP (tergantung cara encoding API)
            byte[] decodedString = Base64.decode(base64ImageString, Base64.DEFAULT);

            // 2. Mengubah byte array menjadi Bitmap
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // 3. Menampilkan Bitmap di ImageView
            binding.avatarImage.setImageBitmap(decodedByte);

        } catch (IllegalArgumentException e) {
            // Tangani jika string Base64 tidak valid
            e.printStackTrace();
        }


    }


    private void updateIndicators(int selectedPosition) {
        for (int i = 0; i < articleIndicators.size(); i++) {
            ImageView indicator = articleIndicators.get(i);
            if (i == selectedPosition) {
                // Set Drawable untuk titik yang aktif
                indicator.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.slide_indikator_active)
                );
            } else {
                // Set Drawable untuk titik yang tidak aktif
                indicator.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.slide_indikator_inactive)
                );
            }
        }
    }


    // Fungsi untuk membuat dots dan mengatur listener ke ViewPager
    private void setupIndicators(int count) {

        // 1. Hapus indikator lama dan list
        binding.SlideIndicator.removeAllViews();
        articleIndicators.clear();

        // 2. Loop dan buat ImageView untuk setiap halaman
        for (int i = 0; i < count; i++) {
            ImageView indicator = new ImageView(this);

            // Atur ukuran dan margin untuk dot
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            binding.SlideIndicator.addView(indicator, params);
            articleIndicators.add(indicator);
        }

        // 3. Atur Listener Perubahan Halaman pada ViewPager2
        binding.viewPagerArticleSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Panggil fungsi update saat halaman berganti
                updateIndicators(position);
            }
        });

        // 4. Atur status awal (default ke item 0)
        if (count > 0) {
            updateIndicators(0);
        }
    }

    private void setupSearchListener() {

        // 🔥 Pemicu utama untuk "Mode Pencarian" adalah saat EditText di-klik (fokus)
        binding.SearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Pengguna baru saja mengklik Search Bar
                showSearchLayout();
            }
        });

        // Listener ini sekarang HANYA bertugas mem-filter list
        binding.SearchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                filterLists(s.toString());
            }
        });

        binding.btnSearch.setOnClickListener(v -> {
            // Saat tombol search di-klik, sembunyikan keyboard
            hideKeyboard();
        });
    }

    /**
     * Memfilter daftar Artikel atau Diary berdasarkan query pencarian.
     */
    private void filterLists(String query) {
        String lowerCaseQuery = query.toLowerCase().trim();

        // Cek tab mana yang aktif
        if (binding.recyclerViewPopularArticles.getVisibility() == View.VISIBLE) {

            // --- 1. FILTER TAB ARTIKEL ---
            List<ArticleModel> filteredList = new ArrayList<>();

            if (lowerCaseQuery.isEmpty()) {
                // Jika search kosong, tampilkan semua data asli
                filteredList = new ArrayList<>(fullArticleList);
            } else {
                // Jika ada query, filter data
                for (ArticleModel article : fullArticleList) {
                    // Filter berdasarkan Judul (getTitle) dan Ringkasan (getExcerpt)
                    if (article.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                            article.getExcerpt().toLowerCase().contains(lowerCaseQuery)) {
                        filteredList.add(article);
                    }
                }
            }
            // Kirim hasil filter ke adapter Artikel
            popularArticleAdapter.setData(filteredList);

        } else if (binding.recyclerViewDiaryHistory.getVisibility() == View.VISIBLE) {

            // --- 2. FILTER TAB DIARY ---
            List<DiaryModel> filteredList = new ArrayList<>();

            if (lowerCaseQuery.isEmpty()) {
                // Jika search kosong, tampilkan semua data asli
                filteredList = new ArrayList<>(fullDiaryList);
            } else {
                // Jika ada query, filter data
                for (DiaryModel diary : fullDiaryList) {
                    // Filter berdasarkan Judul (getTitle) dan Konten (getContent)
                    if (diary.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                            diary.getContent().toLowerCase().contains(lowerCaseQuery)) {
                        filteredList.add(diary);
                    }
                }
            }
            // Kirim hasil filter ke adapter Diary
            scrolldiaryadapter.setData(filteredList);
        }
    }
    private void showSearchLayout() {
        isSearchMode = true;

        // Sembunyikan semua elemen "Rekomendasi" (Artikel & Diary)
        binding.rekomendasiArtikel.setVisibility(View.GONE);
        binding.viewPagerArticleSlider.setVisibility(View.GONE);
        binding.SlideIndicator.setVisibility(View.GONE); // Sembunyikan dots

        binding.diaryHariIni.setVisibility(View.GONE);
        binding.viewPagerDiarySlider.setVisibility(View.GONE);
        // (Tambahkan dots diary di sini jika ada)

        // Sembunyikan List Tanggal
        binding.recyclerViewDates.setVisibility(View.GONE);

        // Sembunyikan Judul List Bawah
        binding.ArtikelPopuler.setVisibility(View.GONE);
        binding.riwayatDiary.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams articleParams = (ConstraintLayout.LayoutParams) binding.recyclerViewPopularArticles.getLayoutParams();
        articleParams.height = 0; // 0dp (Match Constraints)
        binding.recyclerViewPopularArticles.setLayoutParams(articleParams);

        ConstraintLayout.LayoutParams diaryParams = (ConstraintLayout.LayoutParams) binding.recyclerViewDiaryHistory.getLayoutParams();
        diaryParams.height = 0; // 0dp (Match Constraints)
        binding.recyclerViewDiaryHistory.setLayoutParams(diaryParams);
    }
    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.SearchInput.getWindowToken(), 0);
        } catch (Exception e) {
            // abaikan
        }
    }
    @Override
    public void onBackPressed() {
        if (isSearchMode) {
            // --- JIKA SEDANG DALAM MODE PENCARIAN ---
            isSearchMode = false;
            binding.SearchInput.setText(""); // Hapus teks
            binding.SearchInput.clearFocus(); // Hapus fokus (penting)
            hideKeyboard();

            // Kembalikan UI ke normal berdasarkan tab yang aktif
            if (binding.recyclerViewPopularArticles.getVisibility() == View.VISIBLE) {
                // Panggil lagi showArtikelView untuk mereset layout
                // (Ini akan memanggil API lagi, tapi sesuai logic Anda saat ini)
                showArtikelView();
            } else {
                // Panggil lagi showDiaryView untuk mereset layout
                showDiaryView();
            }
        } else {
            // --- JIKA TIDAK DALAM MODE PENCARIAN ---
            // Perilaku "Back" normal (tutup aplikasi)
            super.onBackPressed();
        }
    }
    private void setupNavBarListeners() {

        // Listener untuk Home
        binding.navHome.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Home diklik");
            // Tambahkan intent un tuk berpindah ke Activity Home jika perlu
            // Contoh: startActivity(new Intent(this, Homepage_Activity.class));
        });

        // Listener untuk Calendar/Diary
        binding.navDiary.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Calendar/Diary diklik");
            // Panggil showDiaryView() jika ini seharusnya mengaktifkan tab Diary

        });

        // Listener untuk Chat
        binding.navChat.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Chat diklik");
            startActivity(new Intent(Homepage_Activity.this, ChatbotActivity.class));
            // Tambahkan Intent ke Activity Chat
        });

        // Listener untuk Profile
        binding.navProfile.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Profile diklik");
            startActivity(new Intent(Homepage_Activity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);

            // Tambahkan Intent ke Activity Profile
        });

        // Opsional: Untuk tombol-tombol next yang baru ditambahkan

    }


}


