package com.project.mindbloom.Fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.model.ExpertsDetailModel;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutExpertsProfileBinding;
import com.project.response.DefaultResponse;
import com.project.response.ExpertsDetailResponse;
import com.project.response.StatusResponse;
import com.project.service.ApiService;

// Import PUSHER
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.HashMap;
import java.util.Map;

public class ExpertDetailFragment extends Fragment {
    private String roomId;
    private static final String TAG = "ExpertDetailFragment";

    // UI Binding
    private LayoutExpertsProfileBinding binding;

    // Services
    private ApiService apiService;
    private SessionManager sessionManager;

    // Data
    private String txtName, txtJob;
    private int expertId;
    private int userId;

    // Pusher
    private Pusher pusher;
    private Channel userChannel;
    private static final String PUSHER_APP_KEY = "1be69e5ad5a25ed551d6";
    private static final String PUSHER_CLUSTER = "ap1";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutExpertsProfileBinding.inflate(inflater, container, false);

        // Initialize Service
        apiService = RetrofitClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ambil Data dari Argument/Bundle
        if (getArguments() != null) {
            txtName = getArguments().getString("kirim_nama");
            txtJob = getArguments().getString("kirim_job");
            expertId = getArguments().getInt("kirim_id");
        }

        // 2. Setup UI Awal
        setupUI();

        // 3. Load Detail Expert & Status Chat
        fetchDetailExperts(expertId);
        checkStatusFromApi(); // <--- Cek status ke MySQL via API

        // 4. Setup Pusher Listener (Realtime Approval)
        setupPusherListener();

        // 5. Listener Tombol Back
        binding.btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        binding.bottomNavBar.setVisibility(GONE);

    }

    private void setupUI() {
        binding.tvName.setText(txtName);
        binding.tvJob.setText(txtJob);
        binding.bottomNavBar.setVisibility(VISIBLE);
        // Default tombol disable dulu sampe loading selesai
        binding.btnChat.setEnabled(false);
        binding.btnChat.setText("Memuat...");
    }

    // =========================================================================
    // 1. FETCH DETAIL PROFIL (BIO, LICENSE, DLL)
    // =========================================================================
    private void fetchDetailExperts(int id) {
        apiService.getExpertsDetail(id).enqueue(new Callback<ExpertsDetailResponse>(){
            @Override
            public void onResponse(Call<ExpertsDetailResponse> call, Response<ExpertsDetailResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    ExpertsDetailModel expertsDetails = response.body().getData();
                    if(expertsDetails != null) {
                        binding.tvBio.setText(expertsDetails.getBio());
                        binding.tvLicenseNum.setText(expertsDetails.getLicense_number());

                        String base64String = expertsDetails.getAvatar();
                        if (binding.imgAvatarDetail != null) {
                            if (base64String != null && !base64String.isEmpty()) {
                                try {
                                    byte[] imageBytes = android.util.Base64.decode(base64String, android.util. Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    binding.imgAvatarDetail. setImageBitmap(decodedImage);
                                } catch (IllegalArgumentException e) {
                                    binding.imgAvatarDetail.setImageResource(R.drawable.add_icon);
                                }
                            } else {
                                binding.imgAvatarDetail.setImageResource(R.drawable.add_icon);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ExpertsDetailResponse> call, Throwable t) {
                // Ignore error detail, focus to chat func
            }
        });
    }

    // =========================================================================
    // 2. LOGIKA UTAMA: CEK STATUS & HANDLE TOMBOL
    // =========================================================================
    // Di dalam ExpertDetailFragment.java

    // Line 89-130
    private void checkStatusFromApi() {
        // ✅ TAMBAHKAN LOG untuk debug
        Log.d(TAG, "Calling API checkRequestStatus with userId=" + userId + ", expertId=" + expertId);

        apiService.checkRequestStatus(userId, expertId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                // ✅ LOG HTTP STATUS CODE
                Log.d(TAG, "HTTP Status Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body(). getData().getRequestStatus();
                    roomId = response.body().getData().getRoomId();

                    Log.d(TAG, "Status API: " + status);
                    Log.d(TAG, "Room ID dari API: " + roomId);

                    updateButtonState(status);
                } else {
                    // ✅ HANDLE RESPONSE BODY NULL atau HTTP ERROR
                    Log.e(TAG, "Response failed or body null.  Code: " + response.code());

                    // Tampilkan error message dari server (jika ada)
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Error Body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log. e(TAG, "Error reading error body: " + e.getMessage());
                    }

                    // ✅ SET TOMBOL KE STATE DEFAULT
                    updateButtonState("none");

                    if (getContext() != null) {
                        Toast.makeText(getContext(),
                                "Gagal memuat status.  Coba lagi.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                // ✅ HANDLE KONEKSI ERROR
                Log.e(TAG, "API Call Failed: " + t.getMessage());

                // ✅ SET TOMBOL KE STATE DEFAULT
                updateButtonState("none");

                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Koneksi error: " + t.getMessage(),
                            Toast.LENGTH_SHORT). show();
                }
            }
        });
    }

    private void updateButtonState(String status) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            // Hapus listener lama biar gak numpuk
            binding.btnChat.setOnClickListener(null);

            switch (status) {
                case "approved":
                    // SUDAH DISETUJUI -> Chat Aktif
                    binding.btnChat.setText("Chat Sekarang");
                    binding.btnChat.setEnabled(true);
                    binding.btnChat.setBackgroundColor(getResources().getColor(R.color.default_blue)); // Opsional ganti warna
                    binding.btnChat.setOnClickListener(v -> {
                        if (roomId != null) {
                            openChatRoom(roomId); // ✅ Perbaikan: pastikan memanggil yang benar
                        } else {
                            Toast.makeText(getContext(), "Room ID kosong setelah refresh!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case "pending":
                    // MASIH MENUNGGU KONFIRMASI
                    binding.btnChat.setText("Menunggu Konfirmasi...");
                    binding.btnChat.setEnabled(false); // Tombol mati
                    break;

                default: // "none" atau "rejected"
                    // BELUM ADA REQUEST
                    binding.btnChat.setText("Minta Konsultasi");
                    binding.btnChat.setEnabled(true);
                    binding.btnChat.setOnClickListener(v -> sendConsultationRequest());
                    break;
            }
        });
    }

    // =========================================================================
    // 3. MENGIRIM REQUEST KONSULTASI (API)
    // =========================================================================
    private void sendConsultationRequest() {
        binding.btnChat.setText("Mengirim...");
        binding.btnChat.setEnabled(false);

        // Buat Body Request (Map atau Class Model)
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("expert_id", expertId);

        apiService.sendConsultationRequest(body).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(getContext(), "Request terkirim!", Toast.LENGTH_SHORT).show();
                        // Update tombol jadi Pending
                        updateButtonState("pending");
                    } else {
                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        updateButtonState("none");
                    }
                } else {
                    updateButtonState("none");
                    Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                updateButtonState("none");
                Toast.makeText(getContext(), "Koneksi Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================================================================
    // 4. PUSHER LISTENER (REALTIME APPROVAL)
    // =========================================================================
    private void setupPusherListener() {
        PusherOptions options = new PusherOptions();
        options.setCluster(PUSHER_CLUSTER);

        pusher = new Pusher(PUSHER_APP_KEY, options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.d(TAG, "Pusher State: " + change.getCurrentState());
            }
            @Override
            public void onError(String message, String code, Exception e) {
                Log.e(TAG, "Pusher Error: " + message);
            }
        });

        // Subscribe ke channel user sendiri
        // Channel: 'user-{userId}' (Harus sama dengan di PHP)
        userChannel = pusher.subscribe("user-" + userId);

        // Bind event 'request-approved'
        userChannel.bind("request-approved", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG, "Event Received: " + event.getData());

                // Event berjalan di background, update UI di MainThread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Parse JSON Data
                        Gson gson = new Gson();
                        try {
                            JsonObject data = gson.fromJson(event.getData(), JsonObject.class);
                            String status = data.get("status").getAsString();
                            String roomId = data.get("room_id").getAsString(); // Pastikan PHP kirim room_id

                            if ("approved".equals(status)) {
                                Toast.makeText(getContext(), "Consultation Approved!", Toast.LENGTH_LONG).show();
                                updateButtonState("approved");
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "JSON Parse Error: " + e.getMessage());
                        }
                    });
                }
            }
        });
    }

    // =========================================================================
    // 5. NAVIGASI KE CHAT ROOM
    // =========================================================================
    private void openChatRoom(String roomId) {
        ExpertChatFragment chatFragment = new ExpertChatFragment();
        Bundle bundle = new Bundle();

        bundle.putString("ROOM_ID", roomId);
        bundle.putInt("USER_ID", userId);
        bundle.putInt("EXPERT_ID", expertId);
        bundle.putString("EXPERT_NAME", txtName);
        bundle.putString("EXPERT_JOB", txtJob);

        bundle.putString("ROOM_ID", roomId);
        bundle.putInt("USER_ID", userId);
        chatFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Disconnect Pusher biar hemat baterai
        if (pusher != null) {
            pusher.unsubscribe("user-" + userId);
            pusher.disconnect();
        }
        binding = null;
    }
}