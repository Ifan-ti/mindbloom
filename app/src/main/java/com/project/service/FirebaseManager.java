package com.project.service;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google. firebase.firestore.DocumentSnapshot;
import com.google. firebase.firestore.FirebaseFirestore;
import com. google.firebase.firestore.ListenerRegistration;
import com. google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.project.model.firebase.ChatRoom;
import com.project.request.ConsultationRequest;
import com.project.model.firebase.FirebaseChatMessage;
import com.project.model.firebase.UserSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    private final FirebaseFirestore db;
    private ListenerRegistration messageListener;
    private ListenerRegistration roomListener;

    // Singleton pattern
    private static FirebaseManager instance;

    private FirebaseManager() {
        db = FirebaseFirestore. getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // ============================================
    // 1.  CONSULTATION REQUEST
    // ============================================

    /**
     * Request consultation to expert
     */
    public void requestConsultation(ConsultationRequest request, OnSuccessListener listener) {
        db.collection("consultation_requests")
                .add(request)
                .addOnSuccessListener(documentReference -> {
                    String requestId = documentReference.getId();
                    Log.d(TAG, "Consultation requested with ID: " + requestId);
                    listener.onSuccess(requestId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error requesting consultation", e);
                    listener.onFailure(e.getMessage());
                });
    }

    /**
     * Listen to consultation request status changes
     */
    public void listenToRequestStatus(String requestId, OnRequestStatusChangeListener listener) {
        db. collection("consultation_requests")
                .document(requestId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        String status = snapshot.getString("status");
                        listener.onStatusChanged(status);

                        // If approved, create chat room
                        if ("approved".equals(status)) {
                            ConsultationRequest req = snapshot.toObject(ConsultationRequest.class);
                            if (req != null) {
                                createChatRoom(req. getUserId(), req.getExpertId(),
                                        new OnSuccessListener() {
                                            @Override
                                            public void onSuccess(String roomId) {
                                                listener.onRoomCreated(roomId);
                                            }

                                            @Override
                                            public void onFailure(String error) {
                                                listener. onError(error);
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    // ============================================
    // 2. CHAT ROOM MANAGEMENT
    // ============================================

    /**
     * Create new chat room
     */
    private void createChatRoom(int userId, int expertId, OnSuccessListener listener) {
        ChatRoom chatRoom = new ChatRoom(userId, expertId);

        db.collection("chat_rooms")
                .add(chatRoom)
                .addOnSuccessListener(documentReference -> {
                    String roomId = documentReference.getId();
                    Log.d(TAG, "Chat room created with ID: " + roomId);

                    // Update user session
                    updateUserSession(userId, roomId, expertId, "active");

                    listener.onSuccess(roomId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error creating chat room", e);
                    listener.onFailure(e.getMessage());
                });
    }

    /**
     * Get active chat room for user
     */
    public void getActiveChatRoom(int userId, OnChatRoomListener listener) {
        db.collection("chat_rooms")
                . whereEqualTo("userId", userId)
                .whereEqualTo("status", "active")
                .orderBy("createdAt", Query. Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        ChatRoom room = doc.toObject(ChatRoom.class);
                        if (room != null) {
                            room.setRoomId(doc.getId());
                            listener.onRoomFound(room);
                        }
                    } else {
                        listener.onNoActiveRoom();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error getting chat room", e);
                    listener.onError(e.getMessage());
                });
    }

    /**
     * Listen to chat room status changes
     */
    public void listenToChatRoomStatus(String roomId, OnRoomStatusListener listener) {
        roomListener = db.collection("chat_rooms")
                .document(roomId)
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        return;
                    }

                    if (snapshot != null && snapshot.exists()) {
                        String status = snapshot.getString("status");
                        listener.onStatusChanged(status);
                    }
                });
    }

    /**
     * End chat session (by expert)
     */
    public void endChatSession(String roomId, int userId, OnSuccessListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "closed");

        db.collection("chat_rooms")
                .document(roomId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Chat session ended");

                    // Update user session
                    updateUserSession(userId, null, 0, "closed");

                    listener.onSuccess(roomId);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error ending session", e);
                    listener.onFailure(e.getMessage());
                });
    }

    // ============================================
    // 3.  MESSAGING
    // ============================================

    /**
     * Send message in chat room
     */
    public void sendMessage(String roomId, FirebaseChatMessage message, OnSuccessListener listener) {
        db.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .add(message)
                . addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Message sent: " + documentReference.getId());

                    // Update last message in room
                    updateLastMessage(roomId, message.getMessage());

                    listener.onSuccess(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending message", e);
                    listener. onFailure(e.getMessage());
                });
    }

    /**
     * Listen to new messages in real-time
     */
    public void listenToMessages(String roomId, OnMessageListener listener) {
        messageListener = db.collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction. ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Listen failed.", error);
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (snapshots != null) {
                        List<FirebaseChatMessage> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            FirebaseChatMessage msg = doc.toObject(FirebaseChatMessage.class);
                            msg.setMessageId(doc.getId());
                            messages.add(msg);
                        }
                        listener. onMessagesReceived(messages);
                    }
                });
    }

    /**
     * Load chat history
     */
    public void loadChatHistory(String roomId, OnMessageListener listener) {
        db. collection("chat_rooms")
                .document(roomId)
                .collection("messages")
                . orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<FirebaseChatMessage> messages = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        FirebaseChatMessage msg = doc.toObject(FirebaseChatMessage.class);
                        msg.setMessageId(doc. getId());
                        messages.add(msg);
                    }
                    listener.onMessagesReceived(messages);
                })
                . addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading history", e);
                    listener.onError(e.getMessage());
                });
    }

    // ============================================
    // 4.  HELPER METHODS
    // ============================================

    private void updateLastMessage(String roomId, String message) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", message);
        updates.put("lastMessageTime", com.google.firebase.Timestamp. now());

        db. collection("chat_rooms")
                .document(roomId)
                .update(updates);
    }

    private void updateUserSession(int userId, String roomId, int expertId, String status) {
        UserSession session = new UserSession(roomId, expertId, status);

        db.collection("user_sessions")
                .document(String.valueOf(userId))
                . set(session);
    }

    // ============================================
    // 5.  CLEANUP
    // ============================================

    public void removeMessageListener() {
        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
        }
    }

    public void removeRoomListener() {
        if (roomListener != null) {
            roomListener.remove();
            roomListener = null;
        }
    }

    // ============================================
    // INTERFACES / CALLBACKS
    // ============================================

    public interface OnSuccessListener {
        void onSuccess(String id);
        void onFailure(String error);
    }

    public interface OnRequestStatusChangeListener {
        void onStatusChanged(String status);
        void onRoomCreated(String roomId);
        void onError(String error);
    }

    public interface OnChatRoomListener {
        void onRoomFound(ChatRoom room);
        void onNoActiveRoom();
        void onError(String error);
    }

    public interface OnRoomStatusListener {
        void onStatusChanged(String status);
    }

    public interface OnMessageListener {
        void onMessagesReceived(List<FirebaseChatMessage> messages);
        void onError(String error);
    }
}