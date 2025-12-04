package com.project.mindbloom.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.project.client.RetrofitClient;
import com.project.client.SessionManager;

import com.project.model.MoodModel;
import com.project.model.ProfileModel;

import com.project.mindbloom.databinding.LayoutProfileBinding;

import com.project.response.MoodResponse;
import com.project.response.ProfileResponse;

import com.project.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {


    private List<ProfileModel> ProfileList = new ArrayList<>();
    private List<MoodModel> MoodList = new ArrayList<>();
    private LayoutProfileBinding binding;
    private ApiService apiService;

    private String Username, bio, email, fullname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = RetrofitClient.getApiService(this);

        binding = LayoutProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //function
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        setupNavBarListeners();
        Navigation();
        fetchProfile();
        getMoodData();
        setupMoodChart(MoodList);
    }

    private void fetchProfile() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getAuthToken();

        if(token == null){
            Toast.makeText(this, "Silakan login ulang (token tidak ditemukan)", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ProfileResponse> call = apiService.getProfile("Bearer " + token);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response){
                if(response.isSuccessful() && response.body() != null){
                    ProfileResponse profileResponse = response.body();
                    if("success".equals(profileResponse.getStatus())){
                        ProfileModel profile = profileResponse.getData();

                        binding.tvUsername.setText(profile.getFullName());
                        binding.tvName.setText(profile.getUsername());
                        binding.tvDiaryCount.setText(String.valueOf(profile.getDiaryCont()));
                        binding.tvSubtitle.setText(profile.getBio());

                        fullname = profile.getFullName();
                        Username = profile.getUsername();
                        bio = profile.getBio();
                        email = profile.getEmail();


                        String base64ImageString = profile.getAvatar();

                        if (base64ImageString == null || base64ImageString.isEmpty()) {
                            // Tangani kasus jika string kosong atau null
                            Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            // 1. Mendekode string Base64 ke byte array
                            // Menggunakan flag DEFAULT atau NO_WRAP (tergantung cara encoding API)
                            byte[] decodedString = Base64.decode(base64ImageString, Base64.DEFAULT);

                            // 2. Mengubah byte array menjadi Bitmap
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            // 3. Menampilkan Bitmap di ImageView
                            binding.imgAvatar.setImageBitmap(decodedByte);

                        } catch (IllegalArgumentException e) {
                            // Tangani jika string Base64 tidak valid
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(ProfileActivity.this, "Gagal memuat profil", Toast.LENGTH_SHORT).show();
                    }
                }

        }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("API_PROFILE", "Gagal memuat profil", t);
                Toast.makeText(ProfileActivity.this, "Gagal terhubung ke server: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }


        });
    }

    private void Navigation(){
        binding.btnEditProfile.setOnClickListener( v -> {
            Log.d("NAV_EDIT", "Tombol Edit Profil diklik");
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent.putExtra("fullname", fullname);
            intent.putExtra("username", Username);
            intent.putExtra("bio", bio);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        });
        binding.btnLogout.setOnClickListener(v -> {
            Log.d("NAV_LOGOUT", "Tombol Logout diklik");
            // Hapus sesi login
            SessionManager sessionManager = new SessionManager(ProfileActivity.this);
            sessionManager.clearSession();

            // Kembali ke LoginActivity
            Intent intent = new Intent(ProfileActivity.this, LoginFragmentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Hapus semua aktivitas sebelumnya
            startActivity(intent);
            finish(); // Akhiri ProfileActivity
        });
    }
    private void getMoodData() {
        // Ambil User ID dari Session/SharedPreference kamu
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getAuthToken(); // GANTI INI dengan ID user yang login

        Call<MoodResponse> call = apiService.getDiaryMood("Bearer " + token);
        call.enqueue(new Callback<MoodResponse>() {
            @Override
            public void onResponse(Call<MoodResponse> call, Response<MoodResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MoodModel> moodList = response.body().getData();

                    if (moodList != null && !moodList.isEmpty()) {
                        // PANGGIL FUNGSI SETUP GRAFIK YANG KITA BUAT TADI
                        setupMoodChart(moodList);
                    } else {
                        // Data kosong (User belum nulis diary)
                        // Bisa set grafik kosong atau hidden
                    }
                }
            }

            @Override
            public void onFailure(Call<MoodResponse> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Gagal memuat grafik mood", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupMoodChart(List<MoodModel> moodList) {

        LineChart chart = binding.moodChart;

        if (moodList == null || moodList.isEmpty()) {
            chart.clear();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        ArrayList<String> xLabels = new ArrayList<>();

        // Ambil XAxis dulu untuk nambahin garis manual (LimitLine)
        XAxis xAxis = chart.getXAxis();
        xAxis.removeAllLimitLines(); // Bersihkan garis lama sebelum loop

        // --- LOOPING DATA ---
        for (int i = 0; i < moodList.size(); i++) {
            MoodModel item = moodList.get(i);
            float y = item.getMoodScore();

            Drawable icon = ContextCompat.getDrawable(this, item.getMoodIconResId());
            if (icon != null) icon.setBounds(0, 0, 70, 70);

            entries.add(new Entry(i, y, icon));
            xLabels.add(item.getDayLabel());

            // ====================================================
            // TRIK: Membuat Garis HANYA di tempat yang ada datanya
            // ====================================================
            // 1. Buat garis vertikal di posisi i
            com.github.mikephil.charting.components.LimitLine ll =
                    new com.github.mikephil.charting.components.LimitLine(i, "");

            // 2. Atur style supaya mirip grid (Abu-abu tipis)
            ll.setLineColor(Color.LTGRAY);
            ll.setLineWidth(1f);

            // 3. Tambahkan ke X-Axis
            xAxis.addLimitLine(ll);
        }

        // Dataset Setup
        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawIcons(true);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.TRANSPARENT);
        dataSet.setHighlightEnabled(false);

        LineData data = new LineData(dataSet);
        chart.setData(data);

        // --- PENGATURAN PADDING (ExtraOffsets) ---
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setTouchEnabled(false);

        // ATUR DI SINI: (Kiri, Atas, Kanan, Bawah)
        chart.setExtraOffsets(5f, 15f, 5f, 15f);


        // --- X-Axis Styling ---
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        // MATIKAN Grid bawaan (supaya bagian kosong di kanan bersih)
        xAxis.setDrawGridLines(false);

        // Pastikan garis manual (LimitLine) ada di BELAKANG bunga
        xAxis.setDrawLimitLinesBehindData(true);

        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        // Kunci Lebar Chart (Supaya data tetap rapat kiri / tidak melar)
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(6.5f);

        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));

        // Y-Axis Styling
        YAxis left = chart.getAxisLeft();
        left.setDrawGridLines(false);
        left.setDrawLabels(false);
        left.setDrawAxisLine(false);
        left.setAxisMinimum(0.5f);
        left.setAxisMaximum(3.5f);

        chart.getAxisRight().setEnabled(false);

        chart.invalidate();
    }


    private void setupNavBarListeners() {

        // Listener untuk Home
        binding.navHome.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Home diklik");
            Intent intent = new Intent(ProfileActivity.this, Homepage_Activity.class);
            startActivity(intent);
            //menghapus animasi
            overridePendingTransition(0, 0);
            // Tambahkan intent un tuk berpindah ke Activity Home jika perlu
            // Contoh: startActivity(new Intent(this, Homepage_Activity.class));
        });

        // Listener untuk People/Komunitas


        // Listener untuk Calendar/Diary
        binding.navDiary.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Calendar/Diary diklik");
            startActivity(new Intent(ProfileActivity.this, uploadDiaryActivity.class));

        });

        // Listener untuk Chat
        binding.navChat.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Chat diklik");
            startActivity(new Intent(ProfileActivity.this, ChatbotActivity.class));
            overridePendingTransition(0, 0);
            // Tambahkan Intent ke Activity Chat
        });

        // Listener untuk Profile
        binding.navProfile.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Profile diklik");
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);

            // Tambahkan Intent ke Activity Profile
        });

        // Opsional: Untuk tombol-tombol next yang baru ditambahkan

    }



}


