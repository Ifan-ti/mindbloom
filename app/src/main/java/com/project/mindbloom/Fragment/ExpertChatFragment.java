package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project.adapter.ChatAdapter;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutChatBinding;
import com.project.model.firebase.FirebaseChatMessage;
import com.project.modul.ChatMessage;
import com.project.service.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class ExpertChatFragment extends Fragment {
    private static final String TAG = "ExpertChatFragment";

    private LayoutChatBinding binding;
    private List<ChatMessage> messageList;
    private ChatAdapter chatAdapter;

    // Firebase
    private FirebaseManager firebaseManager;
    private String roomId;
    private int userId;
    private int expertId;
    private String expertName;
    private String expertJob;
    private String sessionStatus = "active";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from arguments
        if (getArguments() != null) {
            roomId = getArguments().getString("ROOM_ID");
            userId = getArguments().getInt("USER_ID", 0);
            expertId = getArguments().getInt("EXPERT_ID", 0);
            expertName = getArguments().getString("EXPERT_NAME", "Expert");
            expertJob = getArguments().getString("EXPERT_JOB", "");
        }

        if (TextUtils.isEmpty(roomId) || userId == 0) {
            Toast.makeText(requireContext(), "Invalid room data", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        setupViews();
        setupRecyclerView();
        setupFirebase();
        loadChatHistory();
        setupListeners();
    }

    private void setupViews() {
        // Hide AI/Psikolog toggle for expert chat
        binding.viewSelect.setVisibility(View.GONE);

        // You can set expert name in toolbar if you have it
        // binding.tvExpertName.setText(expertName);
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        // UBAH INI:
        chatAdapter = new ChatAdapter(messageList, true); // ← Parameter true untuk expert chat

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);

        binding.RecyclerViewList.setLayoutManager(layoutManager);
        binding.RecyclerViewList.setAdapter(chatAdapter);
    }

    private void setupFirebase() {
        firebaseManager = FirebaseManager.getInstance();

        // TAMBAHKAN NULL CHECK INI:
        if (firebaseManager == null) {
            Toast.makeText(requireContext(), "Firebase not initialized", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        // Listen to room status changes
        firebaseManager.listenToChatRoomStatus(roomId, status -> {
            // ... rest of code
        });
    }

    private void loadChatHistory() {
        firebaseManager.loadChatHistory(roomId, new FirebaseManager.OnMessageListener() {
            @Override
            public void onMessagesReceived(List<FirebaseChatMessage> messages) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> updateMessageList(messages));
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading history: " + error);
            }
        });
    }

    private void updateMessageList(List<FirebaseChatMessage> firebaseMessages) {
        messageList.clear();

        for (FirebaseChatMessage fbMsg : firebaseMessages) {
            boolean isUser = fbMsg.getSenderType().equals("user");
            // UBAH INI:
            ChatMessage chatMsg = new ChatMessage(fbMsg.getMessage(), isUser, false); // ← Tambahkan parameter false
            messageList.add(chatMsg);
        }

        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void setupListeners() {
        // Send button
        binding.sendButton.setOnClickListener(v -> sendMessage());

        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Enter key to send (optional)
        binding.messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String messageText = binding.messageInput.getText().toString().trim();

        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        if (!"active".equals(sessionStatus)) {
            Toast.makeText(requireContext(), "Sesi chat telah berakhir", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.messageInput.setText("");

        // UBAH INI:
        ChatMessage userMsg = new ChatMessage(messageText, true, false); // ← Tambahkan parameter false
        messageList.add(userMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        scrollToBottom();

        // Send to Firebase...
    }

    private void handleSessionClosed() {
        binding.messageInput.setEnabled(false);
        binding.sendButton.setEnabled(false);

        new AlertDialog.Builder(requireContext())
                .setTitle("Sesi Berakhir")
                .setMessage(expertName + " telah mengakhiri sesi chat ini.")
                .setPositiveButton("OK", (dialog, which) -> {
                    requireActivity().onBackPressed();
                })
                .setCancelable(false)
                .show();
    }

    private void scrollToBottom() {
        if (messageList.size() > 0) {
            binding.RecyclerViewList.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    private void onBackPressed() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Keluar dari Chat")
                .setMessage("Apakah Anda yakin ingin keluar?  Chat akan tetap tersimpan.")
                .setPositiveButton("Ya", (dialog, which) -> {
                    requireActivity().onBackPressed();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up listeners
        if (firebaseManager != null) {
            firebaseManager.removeMessageListener();
            firebaseManager.removeRoomListener();
        }
        binding = null;
    }
}