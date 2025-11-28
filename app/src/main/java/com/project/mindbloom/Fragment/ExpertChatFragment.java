package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.project.adapter.ChatExpertsAdapter;
import com.project.mindbloom.databinding.LayoutChatBinding; // Pastikan nama file XML layout_chat kamu benar
import com.project.model.MessageModel;
import com.project.model.firebase.ChatRoom;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExpertChatFragment extends Fragment {

    private LayoutChatBinding binding;
    private FirebaseFirestore db;
    private ChatExpertsAdapter ChatExpertsAdapter;
    private ListenerRegistration chatListener;

    private String roomId;
    private int userId;
    private int expertId;
    private String expertName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        // 1. Ambil Data dari Bundle (dikirim dari ExpertDetailFragment)
        if (getArguments() != null) {
            roomId = getArguments().getString("ROOM_ID");
            userId = getArguments().getInt("USER_ID");
            expertId = getArguments().getInt("EXPERT_ID");
            expertName = getArguments().getString("EXPERT_NAME");
        }

        // 2. Setup RecyclerView
        setupRecyclerView();

        // 3. Tombol Kirim
        binding.sendButton.setOnClickListener(v -> sendMessage());

        // 4. Tombol Back
        binding.btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // 5. Mulai dengarkan chat
        listenToMessages();
    }

    private void setupRecyclerView() {
        ChatExpertsAdapter = new ChatExpertsAdapter(requireContext(), userId);
        binding.RecyclerViewList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.RecyclerViewList.setAdapter(ChatExpertsAdapter);
    }

    private void sendMessage() {
        String messageText = binding.messageInput.getText().toString().trim();
        if (messageText.isEmpty()) return;

        // Buat object pesan
        MessageModel message = new MessageModel(
                String.valueOf(userId),
                messageText,
                new Timestamp(new Date())
        );

        // Simpan ke Sub-collection "messages" di dalam dokumen Room
        db.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    binding.messageInput.setText(""); // Kosongkan input
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                });
    }

    public void getActiveChatRoom(int userId, int expertId, OnChatRoomListener listener) {
        db.collection("chat_rooms")
                .whereEqualTo("userId", userId)
                .whereEqualTo("expertId", expertId)
                .whereEqualTo("status", "active") // Kita cari yang statusnya masih active
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Ditemukan room aktif!
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        ChatRoom room = doc.toObject(ChatRoom.class);
                        room.setRoomId(doc.getId()); // Simpan ID dokumen
                        listener.onRoomFound(room);
                    } else {
                        // Tidak ada room aktif
                        listener.onNoActiveRoom();
                    }
                })
                .addOnFailureListener(e -> listener.onError(e.getMessage()));
    }

    // Interface untuk listener (jika belum ada)
    public interface OnChatRoomListener {
        void onRoomFound(ChatRoom room);
        void onNoActiveRoom();
        void onError(String error);
    }
    private void listenToMessages() {
        // Real-time listener ke sub-collection "messages"
        chatListener = db.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        for (DocumentChange change : value.getDocumentChanges()) {
                            if (change.getType() == DocumentChange.Type.ADDED) {
                                MessageModel message = change.getDocument().toObject(MessageModel.class);
                                ChatExpertsAdapter.addMessage(message);
                                // Scroll ke paling bawah saat ada pesan baru
                                binding.RecyclerViewList.smoothScrollToPosition(ChatExpertsAdapter.getItemCount() - 1);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (chatListener != null) chatListener.remove(); // Hentikan listener biar hemat baterai
        binding = null;
    }
}