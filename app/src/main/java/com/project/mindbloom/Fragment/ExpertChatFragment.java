package com.project.mindbloom.Fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.project.adapter.ChatExpertsAdapter;
import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.mindbloom.databinding.LayoutChatBinding;
import com.project.model.MessageModel;
import com.project.request.ChatExpertsRequest; // Pastikan request model ini ada untuk POST
import com.project.response.ChatHistoryResponse;
import com.project.response.DefaultResponse; // Pastikan response model ada
import com.project.service.ApiService;

// Import Pusher
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertChatFragment extends Fragment {

    private static final String TAG = "PusherChat";
    private LayoutChatBinding binding;
    private ChatExpertsAdapter chatExpertsAdapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    // Variable Pusher
    private Pusher pusher;
    private Channel channel;
    private boolean isPusherSetupDone = false;

    // Data Room
    private String roomId;
    private int currentUserId;
    private int expertId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutChatBinding.inflate(inflater, container, false);

        sessionManager = new SessionManager(requireContext());
        apiService = RetrofitClient.getApiService(requireContext());

        currentUserId = sessionManager.getUserId();
        // Ambil Data dari Bundle (dikirim dari ExpertDetailFragment)
        if (getArguments() != null) {
            // Pastikan key "ROOM_ID" sama persis dengan yang dikirim
            roomId = getArguments().getString("ROOM_ID");
            expertId = getArguments().getInt("EXPERT_ID");
            String expertName = getArguments().getString("EXPERT_NAME");

            Log.d(TAG, "ROOM ID DITERIMA DI CHAT: " + roomId); // Verifikasi data masuk
        } else {
            Log.e(TAG, "Error: getArguments() is NULL. No data received.");
        }

        // ... (setup RecyclerView, Pusher, dll)

        // PENTING: Lanjutkan hanya jika Room ID ada.
        if (roomId != null) {
            setupPusher();
        } else {
            Toast.makeText(requireContext(), "Error: Gagal mendapatkan Room ID untuk Chat.", Toast.LENGTH_LONG).show();
        }
        if (! isPusherSetupDone) {
            setupPusher();
            isPusherSetupDone = true;
        }

        if (roomId != null) {
            loadChatHistory();
        }

        binding.viewSelect.setVisibility(GONE);
        binding.btnInactiveAi.setVisibility(GONE);
        binding.btnActivePsikolog.setVisibility(GONE);
        binding.btnInactivePsikolog.setVisibility(GONE);
        binding.btnActiveAi.setVisibility(GONE);

        setupRecyclerView();
        setupPusher(); // <--- SETUP PUSHER DI SINI
        setupListeners();

        return binding.getRoot();
    }

    public void hideUI(){
        binding.viewSelect.setVisibility(INVISIBLE);
        binding.btnActiveAi.setVisibility(INVISIBLE);
        binding.btnInactiveAi.setVisibility(INVISIBLE);
        binding.btnActivePsikolog.setVisibility(INVISIBLE);
        binding.btnInactivePsikolog.setVisibility(INVISIBLE);

    }
    private void setupRecyclerView() {
        chatExpertsAdapter = new ChatExpertsAdapter(requireContext(), currentUserId);
        binding.RecyclerViewList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.RecyclerViewList.setAdapter(chatExpertsAdapter);
    }

    // --- LOGIC PUSHER ---
    // File: ExpertChatFragment.java

    private void loadChatHistory() {
        apiService.getChatHistory(roomId). enqueue(new Callback<ChatHistoryResponse>() {
            @Override
            public void onResponse(Call<ChatHistoryResponse> call, Response<ChatHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MessageModel> messages = response. body().getData();

                    if (messages != null && !messages.isEmpty()) {
                        chatExpertsAdapter.setMessages(messages);
                        binding.RecyclerViewList.scrollToPosition(messages.size() - 1);
                    }

                    Log.d(TAG, "History loaded: " + messages.size() + " messages");
                } else {
                    Log.e(TAG, "Failed to load history: " + response. code());
                }
            }

            @Override
            public void onFailure(Call<ChatHistoryResponse> call, Throwable t) {
                Log.e(TAG, "Error loading history: " + t.getMessage());
            }
        });
    }
    private void setupPusher() {
        // 1. Cek apakah Pusher sudah aktif
        if (pusher != null && pusher.getConnection(). getState() == ConnectionState. CONNECTED) {
            Log.w(TAG, "Pusher already connected, skip setup");
            return;
        }

        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");

        pusher = new Pusher("1be69e5ad5a25ed551d6", options);

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.d(TAG, "Status Koneksi: " + change.getCurrentState());

                // ✅ Subscribe HANYA setelah CONNECTED
                if (change.getCurrentState() == ConnectionState.CONNECTED) {
                    subscribeToChannel();
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
                Log. e(TAG, "Pusher Error: " + message);
            }
        }, ConnectionState.ALL);
    }
    private void subscribeToChannel() {
        if (roomId == null || roomId.isEmpty()) {
            Log.e(TAG, "Room ID null, cannot subscribe");
            return;
        }

        String channelName = "room-" + roomId;

        // ✅ Cek apakah sudah subscribe
        if (pusher.getChannel(channelName) != null) {
            Log. w(TAG, "Already subscribed to " + channelName);
            return;
        }

        Log. d(TAG, "Subscribing to channel: " + channelName);
        channel = pusher. subscribe(channelName);

        // ✅ Bind event (pastikan hanya sekali)
        channel.bind("new-message", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                if (getActivity() == null) return;

                requireActivity().runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        MessageModel message = gson.fromJson(event.getData(), MessageModel.class);

                        Log.d(TAG, "📩 Pusher Event: ID=" + message.getSenderId() + ", Msg=" + message.getMessage());

                        // ✅ Cek duplikasi by ID
                        if (! isMessageDuplicate(message. getSenderId())) {
                            chatExpertsAdapter.addMessage(message);
                            binding.RecyclerViewList.smoothScrollToPosition(
                                    chatExpertsAdapter.getItemCount() - 1
                            );
                        } else {
                            Log.w(TAG, "⚠️ Duplicate message ID: " + message.getSenderId());
                        }

                    } catch (Exception e) {
                        Log. e(TAG, "Gagal Parsing Pusher JSON: " + e.getMessage());
                    }
                });
            }
        });
    }

    // ✅ HELPER: Cek duplikasi by message ID
    private boolean isMessageDuplicate(int messageId) {
        if (messageId == 0) return false; // ID tidak valid atau belum ada

        return chatExpertsAdapter.hasMessageWithId(messageId);
    }
    private void bindPusherChannel(String channelName) {
        // ✅ Cek apakah channel sudah ada
        if (pusher.getChannel(channelName) != null) {
            Log.w(TAG, "Channel " + channelName + " already subscribed");
            return;
        }

        Log.d(TAG, "Subscribing to channel: " + channelName);
        channel = pusher.subscribe(channelName);

        // ✅ Bind event (hanya sekali)
        channel. bind("new-message", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                requireActivity().runOnUiThread(() -> {
                    try {
                        Gson gson = new Gson();
                        MessageModel message = gson.fromJson(event.getData(), MessageModel.class);

                        Log.d(TAG, "📩 Pusher Event: ID=" + message. getId() + ", SenderID=" + message.getSenderId() + ", Msg=" + message. getMessage());

                        // ✅ Cek apakah message sudah ada (prevent duplicate)
                        if (!isMessageDuplicate(message.getId())) {
                            chatExpertsAdapter.addMessage(message);
                            binding.RecyclerViewList.smoothScrollToPosition(
                                    chatExpertsAdapter. getItemCount() - 1
                            );
                        } else {
                            Log.w(TAG, "⚠️ Duplicate message ID: " + message.getId());
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "Gagal Parsing Pusher JSON: " + e.getMessage());
                    }
                });
            }
        });
    }
    private boolean isMessageExists(MessageModel newMessage) {
        // Implementasi sederhana: cek berdasarkan timestamp + isi pesan
        // (Sesuaikan dengan struktur MessageModel Anda)

        // Cara lebih baik: Kirim message ID dari server, lalu cek by ID
        return false; // Sementara return false dulu
    }

    private void setupListeners() {
        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessageToApi(messageText);
                binding.messageInput.setText(""); // Kosongkan input
            }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    // --- KIRIM PESAN LEWAT API PHP (BUKAN LEWAT PUSHER LANGSUNG) ---
    // Di dalam ExpertChatFragment.java

    // --- KIRIM PESAN LEWAT API PHP (BUKAN LEWAT PUSHER LANGSUNG) ---
    // File: ExpertChatFragment.java

    // --- KIRIM PESAN LEWAT API PHP ---
    private void sendMessageToApi(String message) {

        String messageText = binding.messageInput.getText().toString().trim();

        // Pastikan semua data ada sebelum membuat payload
        if (roomId == null || roomId.isEmpty() || currentUserId == 0 || messageText.isEmpty()) {
            Log.e(TAG, "FATAL: Data tidak lengkap saat kirim. Room ID: " + roomId + ", User ID: " + currentUserId + ", Pesan Kosong: " + messageText.isEmpty());
            Toast.makeText(getContext(), "Error: Pesan tidak boleh kosong.", Toast.LENGTH_LONG).show();
            return;
        }

        // 🔥🔥 PERBAIKAN: Gunakan String untuk ID di payload (untuk keamanan PHP) 🔥🔥
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("room_id", roomId);
        // Konversi int ke String agar PHP tidak salah menganggap 0 sebagai false
        payload.put("sender_id", String.valueOf(currentUserId));
        payload.put("message", messageText);
        payload.put("role", "user"); // Role optional, tapi amannya dikirim

        // Log payload sebelum dikirim
        Log.d(TAG, "Payload sent: " + payload.toString());

        // Panggil Retrofit
        apiService.sendMessage(payload).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                binding.messageInput.setText(""); // Kosongkan input

                if (!response.isSuccessful()) {
                    String errorBody = "N/A";
                    try {
                        // Coba ambil error body (yang berisi JSON {"status":"error", "message":"Data chat tidak lengkap"})
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        errorBody = e.getMessage();
                    }
                    Log.e(TAG, "Gagal kirim. Code: " + response.code() + ", Error: " + errorBody);
                    Toast.makeText(getContext(), "Gagal kirim pesan: " + response.code(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Pesan sukses terkirim ke API (Menunggu Pusher)");
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.e(TAG, "Koneksi Error: " + t.getMessage());
                Toast.makeText(getContext(), "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "🧹 onDestroyView() called");

        // ✅ CLEANUP PUSHER
        if (pusher != null) {
            try {
                if (channel != null) {
                    String channelName = "room-" + roomId;

                    // ✅ Unbind ALL events (lebih aman)

                    // ✅ Unsubscribe channel
                    pusher.unsubscribe(channelName);

                    Log.d(TAG, "✅ Channel unsubscribed: " + channelName);
                }

                // ✅ Disconnect Pusher
                pusher.disconnect();
                pusher = null;
                channel = null;

            } catch (Exception e) {
                Log.e(TAG, "Error cleaning up Pusher: " + e.getMessage());
            }
        }

        // ✅ Reset flag
        isPusherSetupDone = false;

        binding = null;
    }
}