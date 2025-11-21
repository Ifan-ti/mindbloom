package com.project.mindbloom;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.adapter.NotificationAdapter;
import com.project.adapter.PostAdapter;
import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.data.NotificationModel;
import com.project.data.PostModel;
import com.project.mindbloom.databinding.LayoutKomunitasBinding;
import com.project.response.NotificationResponse;
import com.project.response.PostResponse;
import com.project.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Community_Activity extends AppCompatActivity {

    private TextView tabKomunitas, tabNotifikasi;
    private RecyclerView recyclerView;

    private PostAdapter postAdapter;
    private NotificationAdapter notificationAdapter;

    private List<PostModel> postList = new ArrayList<>();
    private List<NotificationModel> notificationList = new ArrayList<>();

    private ApiService apiService;
    private SessionManager sessionManager;
    private LayoutKomunitasBinding binding;



    @Override
    protected void onResume() {
        super.onResume();

        // Panggil data SETIAP KALI layar ini kembali aktif.
        // Ini akan mengambil postingan baru DAN gambar yang baru diubah.
        showCommunityTab();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LayoutKomunitasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // pakai layout_komunitas

        // Inisialisasi view
        tabKomunitas = binding.tabKomunitas;
        tabNotifikasi = binding.tabNotifikasi;
        recyclerView = binding.recyclerViewPosts;

        apiService = RetrofitClient.getApiService(this);
        sessionManager = new SessionManager(this);

        setupRecyclerView();
        setupTabs();

        setupNavBarListeners();
        // Default: tampilkan tab komunitas
     }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postAdapter = new PostAdapter(this, postList);
        notificationAdapter = new NotificationAdapter(this, notificationList);

        recyclerView.setAdapter(postAdapter);
    }

    private void setupTabs() {
        tabKomunitas.setOnClickListener(v -> showCommunityTab());
        tabNotifikasi.setOnClickListener(v -> showNotificationTab());
    }

    private void showCommunityTab() {
        // Style tab
        tabKomunitas.setTypeface(null, Typeface.BOLD);
        tabKomunitas.setTextColor(ContextCompat.getColor(this, R.color.black));

        tabNotifikasi.setTypeface(null, Typeface.NORMAL);
        tabNotifikasi.setTextColor(ContextCompat.getColor(this, R.color.gray));

        // Ganti adapter ke post
        recyclerView.setAdapter(postAdapter);
        loadCommunityPosts();
    }

    private void showNotificationTab() {
        // Style tab
        tabNotifikasi.setTypeface(null, Typeface.BOLD);
        tabNotifikasi.setTextColor(ContextCompat.getColor(this, R.color.black));

        tabKomunitas.setTypeface(null, Typeface.NORMAL);
        tabKomunitas.setTextColor(ContextCompat.getColor(this, R.color.gray));

        // Ganti adapter ke notification
        recyclerView.setAdapter(notificationAdapter);
        loadNotifications();
    }

    private void loadCommunityPosts() {
        apiService.getPosts().enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    postList = response.body().getData();
                    postAdapter.updatePosts(postList);
                } else {
                    Log.e("CommunityActivity", "Gagal load posts: " + response.code());
                    Toast.makeText(Community_Activity.this,
                            "Gagal memuat postingan (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.e("CommunityActivity", "API Failure: " + t.getMessage());
                Toast.makeText(Community_Activity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadNotifications() {
        String token = sessionManager.getAuthToken();
        if (token == null) {
            Toast.makeText(this, "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        apiService.getNotifications("Bearer " + token).enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList = response.body().getData();
                    notificationAdapter.updateNotifications(notificationList);
                } else {
                    Log.e("CommunityActivity", "Gagal load notif: " + response.code());
                    Toast.makeText(Community_Activity.this,
                            "Gagal memuat notifikasi (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();

                    if (response.code() == 401 || response.code() == 403) {
                        Toast.makeText(Community_Activity.this,
                                "Sesi berakhir, silakan login ulang",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                Log.e("CommunityActivity", "API Failure: " + t.getMessage());
                Toast.makeText(Community_Activity.this,
                        "Koneksi gagal: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupNavBarListeners() {

        // Listener untuk Home
        binding.navHome.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol Home diklik");
            Intent intent = new Intent(Community_Activity.this, Homepage_Activity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            // Tambahkan intent un tuk berpindah ke Activity Home jika perlu
            // Contoh: startActivity(new Intent(this, Homepage_Activity.class));
        });

        // Listener untuk People/Komunitas
        binding.navPeople.setOnClickListener(v -> {
            Log.d("NAV_BAR", "Tombol People diklik");
            Intent intent = new Intent(Community_Activity.this, Community_Activity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
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
            startActivity(new Intent(Community_Activity.this, ProfileActivity.class));
            overridePendingTransition(0, 0);

        });

        // Opsional: Untuk tombol-tombol next yang baru ditambahkan

    }
}
