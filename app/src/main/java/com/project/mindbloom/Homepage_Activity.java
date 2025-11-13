package com.project.mindbloom;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.project.data.ArticleModel;
import com.project.data.DiaryModel;
import com.project.data.dateModel;

import com.project.mindbloom.databinding.LayoutHomepageBinding;
import com.project.response.ArticlePopulerResponse;
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


    ApiService apiService = RetrofitClient.getApiService(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sm = new SessionManager(this);
        if (!sm.isLoggedIn()) {
            Intent i = new Intent(this, ActivityMain.class);
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

        fetchUsername();

        binding.navProfile.setOnClickListener(v -> {
            startActivity(new Intent(Homepage_Activity.this, ProfileActivity.class));
        });


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
        // 1. TAMPILKAN semua view Artikel
        isSearchMode = false;

        binding.recyclerViewDates.setVisibility(View.VISIBLE);

        binding.rekomendasiArtikel.setVisibility(View.VISIBLE);
        binding.viewPagerArticleSlider.setVisibility(View.VISIBLE);
        binding.ArtikelPopuler.setVisibility(View.VISIBLE);
        binding.recyclerViewPopularArticles.setVisibility(View.VISIBLE);

        // 2. SEMBUNYIKAN semua view Diary (Menggunakan INVISIBLE)
        binding.diaryHariIni.setVisibility(View.INVISIBLE);
        binding.viewPagerDiarySlider.setVisibility(View.INVISIBLE);
        binding.riwayatDiary.setVisibility(View.INVISIBLE);
        binding.recyclerViewDiaryHistory.setVisibility(View.INVISIBLE);
        binding.SlideIndicator.setVisibility(View.VISIBLE);

        // 3. ATUR STYLE TOMBOL
        binding.SearchInput.setText("");
        binding.SearchInput.setHint("Cari Artikel Menarik Hari ini");
        binding.btnArtikel.setBackgroundResource(R.drawable.clickbtn);
        binding.btnArtikel.setTextColor(ContextCompat.getColor(this, R.color.inactive_light_blue));

        binding.btnDiary.setBackgroundResource(R.drawable.frame_home);
        binding.btnDiary.setTextColor(ContextCompat.getColor(this, R.color.main_blue));

        ConstraintLayout.LayoutParams articleParams = (ConstraintLayout.LayoutParams) binding.recyclerViewPopularArticles.getLayoutParams();
        articleParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        binding.recyclerViewPopularArticles.setLayoutParams(articleParams);

        fetchArticles();
    }

    private void showDiaryView() {
        // 1. SEMBUNYIKAN semua view Artikel (Menggunakan INVISIBLE)
        isSearchMode = false;

        binding.recyclerViewDates.setVisibility(View.VISIBLE);

        binding.rekomendasiArtikel.setVisibility(View.INVISIBLE);
        binding.viewPagerArticleSlider.setVisibility(View.INVISIBLE);
        binding.ArtikelPopuler.setVisibility(View.INVISIBLE);
        binding.recyclerViewPopularArticles.setVisibility(View.INVISIBLE);

        // 2. TAMPILKAN semua view Diary
        binding.diaryHariIni.setVisibility(View.VISIBLE);
        binding.viewPagerDiarySlider.setVisibility(View.VISIBLE);
        binding.riwayatDiary.setVisibility(View.VISIBLE);
        binding.recyclerViewDiaryHistory.setVisibility(View.VISIBLE);
        binding.SlideIndicator.setVisibility(View.VISIBLE);

        // 3. ATUR STYLE TOMBOL
        binding.SearchInput.setText("");
        binding.SearchInput.setHint("Cari Diary Yang Kamu Inginkan");
        binding.btnArtikel.setBackgroundResource(R.drawable.frame_home);
        binding.btnArtikel.setTextColor(ContextCompat.getColor(this, R.color.main_blue));

        binding.btnDiary.setBackgroundResource(R.drawable.clickbtn);
        binding.btnDiary.setTextColor(ContextCompat.getColor(this, R.color.inactive_light_blue));

        ConstraintLayout.LayoutParams articleParams = (ConstraintLayout.LayoutParams) binding.recyclerViewPopularArticles.getLayoutParams();
        articleParams.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        binding.recyclerViewPopularArticles.setLayoutParams(articleParams);

        fetchDiary();
    }

    // Homepage_Activity.java

    private void fetchArticles() {
        Call<ArticlePopulerResponse> call = apiService.getArticles();

        // HANYA SATU PANGGILAN API UNTUK MENGISI KEDUA ADAPTER DAN INDIKATOR
        call.enqueue(new Callback<ArticlePopulerResponse>() {
            @Override
            public void onResponse(Call<ArticlePopulerResponse> call, Response<ArticlePopulerResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ArticleModel> articles = response.body().getData();


                    if (articles != null && !articles.isEmpty()) {

                        Log.d("API_SUCCESS", "Total Artikel: " + articles.size());



                        List<ArticleModel> listPopuler = new ArrayList<>(articles);


                        Collections.sort(listPopuler, new Comparator<ArticleModel>() {
                                    @Override
                                    public int compare(ArticleModel a1, ArticleModel a2) {
                                        // Ganti getReadCount() sesuai nama method di ArticleModel Anda
                                        // Ini mengurutkan descending (a2 dibanding a1)
                                        return Integer.compare(a2.getReadCount(), a1.getReadCount());
                                    }

                                });

                        List<ArticleModel> listRekomendasi = new ArrayList<>();

                        // Loop semua artikel
                        // ⬇️ ASUMSI: ArticleModel punya getTags() & tag-nya "rekomendasi"
                        for (ArticleModel article : articles) {
                            // Ganti getTags() dan "rekomendasi" sesuai data Anda
                            if (article.getNamaTag() != null && article.getNamaTag().contains("Rekomendasi")) {
                                listRekomendasi.add(article);
                            }
                        }

                        fullArticleList = new ArrayList<>(listPopuler);
                        // 1. ISI ADAPTER ARTIKEL POPULER (List Vertikal)
                        popularArticleAdapter.setData(listPopuler);

                        // 2. ISI ADAPTER REKOMENDASI SLIDER (ViewPager)
                        rekomendasiartikeladapter.setData(listRekomendasi);

                        // 🔥 3. PANGGIL SETUP INDICATOR 🔥
                        setupIndicators(listRekomendasi.size());

                    } else {
                        Log.w("API_EMPTY", "Data artikel kosong.");
                    }
                } else {
                    // Respons Gagal (misal status 404, 500 dari server)
                    Log.e("API_FAIL", "Respon gagal: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<ArticlePopulerResponse> call, Throwable t) {
                Log.e("API_ERROR", "Koneksi ke API Artikel gagal: " + t.getMessage());
            }
        });
    }
// ASUMSI: Anda punya variabel global ini di Homepage_Activity
// private ScrollDiaryAdapter scrollDiaryAdapter; // (untuk slider)
// private DiaryHistoryAdapter historyDiaryAdapter; // (untuk list riwayat)

    // Homepage_Activity.java

// --- Variabel Global yang Diasumsikan ---
// private ScrollDiaryAdapter diarySliderAdapter;
// private DiaryHistoryAdapter diaryHistoryAdapter; // (Anda harus membuat class adapter ini)
// private List<ImageView> diaryIndicators = new ArrayList<>(); // (List terpisah untuk indikator diary)

// ...

    private void fetchDiary() {
        // Ambil token dari SessionManager
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getAuthToken();   // pastikan method ini ada di SessionManager

        if (token == null) {
            Toast.makeText(this, "Silakan login ulang (token tidak ditemukan)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pakai ApiService global yang sudah kamu deklarasikan di atas
        // ApiService apiService = RetrofitClient.getApiService(this); // boleh dihapus kalau sudah jadi field global

        Call<DiaryRespone> call = apiService.getMyDiary();

        call.enqueue(new Callback<DiaryRespone>() {
            @Override
            public void onResponse(Call<DiaryRespone> call, Response<DiaryRespone> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DiaryModel> diaries = response.body().getData();
                    if (diaries != null && !diaries.isEmpty()) {

                        // Simpan ke fullDiaryList supaya fitur SEARCH diary jalan
                        fullDiaryList = new ArrayList<>(diaries);

                        // isi adapter untuk history & slider
                        scrolldiaryadapter.setData(diaries);
                        slidediaryadaptert.setData(diaries);

                    } else {
                        Toast.makeText(Homepage_Activity.this, "Diary kosong", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Homepage_Activity.this,
                            "Gagal memuat diary. Code: " + response.code(),
                            Toast.LENGTH_SHORT).show();

                    Log.e("Diary_API", "Error body: " + response.code());
                }
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


    private void fetchUsername()  {
        SessionManager sessionManager = new SessionManager(this);

        int userId = sessionManager.getUserId();

        Call<UserResponse> call = apiService.getUserById(userId);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    if ("success".equals(userResponse.getStatus())) {
                        String username = userResponse.getData().getUsername();
                        // Misal tampilkan di TextView
                        binding.UsernameUser.setText("Hallo, " + username);
                    } else {
                        Toast.makeText(Homepage_Activity.this,
                                "Gagal ambil username: " + userResponse.getStatus(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Homepage_Activity.this,
                            "Gagal memuat data user. Kode: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    sessionManager.clearSession();
                    Intent intent = new Intent(Homepage_Activity.this, ActivityMain.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(Homepage_Activity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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

        // Listener untuk People/Komunitas
        binding.navPeople.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol People diklik");
            Intent intent = new Intent(Homepage_Activity.this, Community_Activity.class);
            startActivity(intent);
        });

        // Listener untuk Calendar/Diary
        binding.navCalendar.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Calendar/Diary diklik");
            // Panggil showDiaryView() jika ini seharusnya mengaktifkan tab Diary

        });

        // Listener untuk Chat
        binding.navChat.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Chat diklik");
            // Tambahkan Intent ke Activity Chat
        });

        // Listener untuk Profile
        binding.navProfile.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Profile diklik");
            // Tambahkan Intent ke Activity Profile
        });

        // Opsional: Untuk tombol-tombol next yang baru ditambahkan

    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Ganti R.id.fragment_container dengan ID FrameLayout Anda
        transaction.replace(R.id.fragment_container, fragment);

        // (Opsional: tambahkan ke back stack jika ingin tombol 'back' mengembalikan fragment)
        // transaction.addToBackStack(null);

        transaction.commit();
    }

}


