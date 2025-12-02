package com.project.mindbloom.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.adapter.ChatBotAdapter;
import com.project.client.RetrofitClient; // 👈 GANTI
import com.project.client.SessionManager; // 👈 BARU
import com.project.mindbloom.R;
import com.project.modul.ChatMessage;
import com.project.request.ChatSaveRequest; // 👈 BARU
import com.project.request.DeepSeekRequest;
import com.project.response.ChatHistoryChatBot; // 👈 BARU
import com.project.response.DeepSeekResponse;
import com.project.response.DefaultResponse; // 👈 BARU
import com.project.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private ImageButton sendButton, btn_inactive_experts;

    private ChatBotAdapter chatBotAdapter;
    private List<ChatMessage> messageList;

    private ApiService apiService; // 👈 Dulu: deepSeekService
    private SessionManager sessionManager; // 👈 BARU
    private int userId; // 👈 BARU
    private int loadingIndex = -1;

    // ⛔ Kunci API Dihapus. Sekarang aman di server.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);

        chatRecyclerView = findViewById(R.id.RecyclerViewList);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        btn_inactive_experts = findViewById(R.id.btn_inactive_psikolog);

        // 1. Setup Klien API ke server NGROK Anda
        sessionManager = new SessionManager(this);
        apiService = RetrofitClient.getApiService(this); // 👈 GANTI

        // 2. Ambil User ID dari Sesi
        // Pastikan user sudah login sebelum masuk ke activity ini
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Sesi berakhir, silakan login ulang.", Toast.LENGTH_LONG).show();
            // TODO: Arahkan kembali ke Login / ActivityMain
            // finish();
            // return;

            // HACK: Jika testing tanpa login, pakai ID 1
            userId = 1;
        } else {
            userId = sessionManager.getUserId();
        }


        setupRecyclerView(); // Setup adapter
        // 3. Muat History Chat (Fitur "Seperti WA")
        loadChatHistory();
        setUpBtn();
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatBotAdapter = new ChatBotAdapter(messageList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Pesan baru selalu di bawah

        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatBotAdapter);
    }
    private void setUpBtn(){
        btn_inactive_experts.setOnClickListener(v -> {
        startActivity(new Intent(ChatbotActivity.this, ExpertFragmentActivity.class));
        });
        sendButton.setOnClickListener(v -> sendMessage());

    }

    /**
     * BARU: Memuat history chat dari server
     */
    private void loadChatHistory() {
        showLoading(); // Tampilkan loading saat memuat history

        apiService.getChatHistory(userId).enqueue(new Callback<List<ChatHistoryChatBot>>() {
            @Override
            public void onResponse(Call<List<ChatHistoryChatBot>> call, Response<List<ChatHistoryChatBot>> response) {
                hideLoading(); // Sembunyikan loading

                if (response.isSuccessful() && response.body() != null) {
                    List<ChatHistoryChatBot> historyList = response.body();

                    if (historyList.isEmpty()) {
                        // Jika tidak ada history, sapa
                        addMessage("Hai, aku Boomy!", false);
                        addMessage("Ceritakan apa yang ada di hatimu.", false);
                    } else {
                        // Tampilkan semua history
                        for (ChatHistoryChatBot chat : historyList) {
                            addMessage(chat.getPrompt(), true); // Pesan User
                            addMessage(chat.getResponse(), false); // Pesan Bot
                        }
                    }
                } else {
                    // Gagal memuat history
                    addMessage("Hai, aku Boomy!", false);
                    addMessage("Gagal memuat history chatmu. Ceritakan lagi ya.", false);
                    Log.e("CHAT_HISTORY", "Gagal memuat history: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ChatHistoryChatBot>> call, Throwable t) {
                hideLoading();
                Log.e("CHAT_HISTORY", "Koneksi history gagal: " + t.getMessage());
                addMessage("Hai, aku Boomy!", false);
                addMessage("Koneksi bermasalah. Coba lagi nanti ya.", false);
            }
        });
    }

    private void sendMessage() {
        String userMessage = messageInput.getText().toString().trim();
        if (userMessage.isEmpty()) return;

        addMessage(userMessage, true);
        messageInput.setText("");

        showLoading();
        disableSendButton();
        callChatProxy(userMessage); // 👈 GANTI nama fungsi
    }

    /**
     * DIPERBARUI: Memanggil server proxy NGROK Anda
     */
    private void callChatProxy(String userMessage) {
        DeepSeekRequest request = new DeepSeekRequest(userMessage);

        // Panggil endpoint proxy di server Anda
        apiService.sendChatToProxy(request).enqueue(new Callback<DeepSeekResponse>() {
            @Override
            public void onResponse(Call<DeepSeekResponse> call, Response<DeepSeekResponse> response) {
                hideLoading();
                enableSendButton();

                if (response.isSuccessful() && response.body() != null) {
                    String botReply = response.body().getResponseText();
                    addMessage(botReply, false);

                    // BARU: Simpan chat ini ke database
                    saveChatToDatabase(userMessage, botReply);

                } else {
                    try {
                        Log.e("CHAT_PROXY_ERROR", "Error Body: " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                    addMessage("Boomy sedang istirahat... (API Error " + response.code() + ")", false);
                }
            }

            @Override
            public void onFailure(Call<DeepSeekResponse> call, Throwable t) {
                hideLoading();
                enableSendButton();
                Log.e("CHAT_PROXY_FAIL", "Request gagal: " + t.getMessage());
                addMessage("Koneksi bermasalah. Coba lagi nanti.", false);
            }
        });
    }

    /**
     * BARU: Menyimpan percakapan ke database via server
     */
    private void saveChatToDatabase(String userMsg, String botMsg) {
        // Buat request body untuk disimpan
        ChatSaveRequest saveRequest = new ChatSaveRequest(
                userId,
                userMsg,
                botMsg,
                "deepseek/deepseek-chat" // (Sesuai model di DeepSeekRequest)
        );

        apiService.saveChat(saveRequest).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && "success".equals(response.body().getStatus())) {
                    Log.d("CHAT_SAVE", "Chat berhasil disimpan ke DB.");
                } else {
                    Log.e("CHAT_SAVE", "Gagal menyimpan chat ke DB: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Log.e("CHAT_SAVE", "Koneksi save chat gagal: " + t.getMessage());

            }
        });
    }

    // --- Fungsi Helper (addMessage, showLoading, hideLoading) ---
    // (Tidak ada perubahan di fungsi-fungsi ini, Anda bisa pakai kode lama Anda)

    private void addMessage(String message, boolean isUser) {
        if (message == null || message.trim().isEmpty()) return;
        messageList.add(new ChatMessage(message, isUser));
        chatBotAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);
    }

    private void showLoading() {
        ChatMessage loadingMessage = new ChatMessage(true);
        messageList.add(loadingMessage);
        loadingIndex = messageList.size() - 1;
        chatBotAdapter.notifyItemInserted(loadingIndex);
        chatRecyclerView.scrollToPosition(loadingIndex);
    }

    private void hideLoading() {
        if (loadingIndex != -1 && loadingIndex < messageList.size()) {
            messageList.remove(loadingIndex);
            chatBotAdapter.notifyItemRemoved(loadingIndex);
            loadingIndex = -1;
        }
    }
    private void disableSendButton() {
        sendButton.setEnabled(false);
        sendButton.setAlpha(0.5f); // Efek visual "non-aktif"
    }

    /**
     * Aktifkan kembali tombol kirim
     */
    private void enableSendButton() {
        sendButton.setEnabled(true);
        sendButton.setAlpha(1.0f); // Efek visual "aktif"
    }
}