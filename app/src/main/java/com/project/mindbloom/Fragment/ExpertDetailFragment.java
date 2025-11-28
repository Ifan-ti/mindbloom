package com.project.mindbloom.Fragment;

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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.model.ExpertsDetailModel;
import com.project.model.firebase.ChatRoom;
import com.project.request.ConsultationRequest;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutExpertsProfileBinding;
import com.project.response.ExpertsDetailResponse;
import com.project.service.ApiService;
import com.project.service.FirebaseManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertDetailFragment extends Fragment {
    private static final String TAG = "ExpertDetailFragment";
    private ApiService apiService;
    private ListenerRegistration requestListener;
    private FirebaseManager firebaseManager;
    private SessionManager sessionManager;

    private String txtName, txtJob, txtBio, LicenseNum;
    private int expertId;
    private int userId;
    private LayoutExpertsProfileBinding binding;

    private String pendingRequestId = null; // Track pending request
    private AlertDialog waitingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutExpertsProfileBinding.inflate(inflater, container, false);

        // Initialize services
        apiService = RetrofitClient.getApiService(requireContext());
        firebaseManager = FirebaseManager.getInstance();
        sessionManager = new SessionManager(requireContext());

        // Get user ID from session
        userId = sessionManager.getUserId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            txtName = getArguments().getString("kirim_nama");
            txtJob = getArguments().getString("kirim_job");
            expertId = getArguments().getInt("kirim_id");
        }

        fetchDetailExperts(expertId);
        setupChatButton();
        checkExistingSession();
          // Check if user already has active session
    }

    private void fetchDetailExperts(int id) {
        Call<ExpertsDetailResponse> call = apiService.getExpertsDetail(id);

        call.enqueue(new Callback<ExpertsDetailResponse>(){
            @Override
            public void onResponse(Call<ExpertsDetailResponse> call, Response<ExpertsDetailResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    ExpertsDetailModel expertsDetails = response.body().getData();
                    updateUI(expertsDetails);
                }
            }

            @Override
            public void onFailure(Call<ExpertsDetailResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load expert details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(ExpertsDetailModel expert){
        binding.tvBio.setText(expert.getBio());
        binding.tvLicenseNum.setText(expert.getLicense_number());
        binding.tvName.setText(txtName);
        binding.tvJob.setText(txtJob);
    }

    /**
     * Setup Chat Button Click Listener
     */
    private void setupChatButton() {
        // Sesuaikan dengan ID button di layout kamu
        binding.btnChat.setOnClickListener(v -> {
            requestConsultation();
        });
    }

    /**
     * Check if user already has active chat session
     */
    /**
     * Check if user already has active chat session
     */
    private void checkExistingSession() {
        // 1. Matikan tombol dulu biar user ga asal klik
        binding.btnChat.setEnabled(false);
        binding.btnChat.setText("Memeriksa sesi...");

        // 2. Cek apakah ada Chat Room yang statusnya "active"
        // Perhatikan penambahan parameter expertId disini
        firebaseManager.getActiveChatRoom(userId, expertId, new FirebaseManager.OnChatRoomListener() {
            @Override
            public void onRoomFound(ChatRoom room) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // JIKA ADA: Langsung set tombol ke Chat Room
                        binding.btnChat.setText("Lanjutkan Chat");
                        binding.btnChat.setEnabled(true);

                        // Ganti fungsi klik tombol langsung masuk room
                        binding.btnChat.setOnClickListener(v -> {
                            openChatRoom(room.getRoomId(), expertId);
                        });
                    });
                }
            }

            @Override
            public void onNoActiveRoom() {
                // JIKA TIDAK ADA: Baru cek apakah ada Request Pending
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        checkPendingRequest(); // Pindah ke pengecekan request
                    });
                }
            }

            @Override
            public void onError(String error) {
                // Kalau error, asumsi tidak ada room, coba cek request
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> checkPendingRequest());
                }
            }
        });
    }

    /**
     * Request consultation to expert
     */
    /**
     * Request consultation to expert
     */
    private void requestConsultation() {
        // Validate user login
        if (userId <= 0) {
            Toast. makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button while processing
        binding.btnChat.setEnabled(false);
        binding.btnChat.setText("Memeriksa.. .");

        // ← TAMBAHKAN: Check if already has pending request
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("consultation_requests")
                .whereEqualTo("userId", userId)
                .whereEqualTo("expertId", expertId)
                .whereEqualTo("status", "pending")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (! queryDocumentSnapshots.isEmpty()) {
                        // Already has pending request
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String requestId = doc.getId();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                pendingRequestId = requestId;
                                binding.btnChat.setText("Menunggu konfirmasi...");
                                showWaitingDialog();
                                listenToRequestStatus(requestId);

                                Toast.makeText(requireContext(),
                                        "Anda sudah memiliki request yang sedang diproses",
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        // No pending request, create new one
                        sendNewRequest();
                    }
                })
                .addOnFailureListener(e -> {
                    // If error, proceed with new request
                    sendNewRequest();
                });
    }

    /**
     * Update button state berdasarkan status
     */
    private void updateButtonState(String state) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            switch (state) {
                case "checking":
                    binding.btnChat. setText("Memeriksa sesi...");
                    binding.btnChat.setEnabled(false);
                    // Optional: ubah warna button
                    break;

                case "pending":
                    binding.btnChat.setText("Menunggu konfirmasi...");
                    binding.btnChat.setEnabled(false);
                    break;

                case "active":
                    binding.btnChat. setText("Lanjutkan Chat");
                    binding.btnChat. setEnabled(true);
                    break;

                case "normal":
                default:
                    binding.btnChat.setText("Chat");
                    binding.btnChat.setEnabled(true);
                    break;
            }
        });
    }
    /**
     * Send new consultation request
     */

    private void sendNewRequest() {
        binding.btnChat.setText("Mengirim request...");

        // Create consultation request
        ConsultationRequest request = new ConsultationRequest(userId, expertId, "online");
        request.setUserName(sessionManager.getAuthToken());
        request.setExpertName(txtName);

        // Send request to Firebase
        firebaseManager.requestConsultation(request, new FirebaseManager.OnSuccessListener() {
            @Override
            public void onSuccess(String requestId) {
                pendingRequestId = requestId;

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.btnChat.setText("Menunggu konfirmasi...");
                        showWaitingDialog();
                        listenToRequestStatus(requestId);
                    });
                }
            }

            @Override
            public void onFailure(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.btnChat.setEnabled(true);
                        binding.btnChat.setText("Chat");
                        Toast.makeText(requireContext(),
                                "Gagal mengirim request: " + error,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    /**
     * Show waiting dialog while request is pending
     */
    /**
     * Show waiting dialog while request is pending
     */
    private void showWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Menunggu Konfirmasi");
        builder.setMessage("Request chat Anda sedang menunggu konfirmasi dari " + txtName + ".. .");

        // ← UBAH INI: setCancelable(true) supaya bisa di-dismiss
        builder.setCancelable(true);

        builder. setNegativeButton("Sembunyikan", (dialog, which) -> {
            // Hanya hide dialog, tidak cancel request
            dialog.dismiss();

            // Update button text
            binding.btnChat.setText("Menunggu konfirmasi.. .");
            binding.btnChat.setEnabled(false);

            Toast.makeText(requireContext(),
                    "Request masih berjalan di background",
                    Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Batalkan Request", (dialog, which) -> {
            cancelRequest();
        });

        // ← TAMBAHKAN: Handle ketika user tap di luar dialog
        builder.setOnDismissListener(dialog -> {
            // Keep button showing waiting state
            binding.btnChat.setText("Menunggu konfirmasi...");
            binding.btnChat.setEnabled(false);
        });

        waitingDialog = builder.create();
        waitingDialog.show();
    }

    /**
     * Check if user has pending request for this expert
     */
    private void checkPendingRequest() {
        FirebaseFirestore db = FirebaseFirestore. getInstance();

        db. collection("consultation_requests")
                .whereEqualTo("userId", userId)
                .whereEqualTo("expertId", expertId)
                .whereEqualTo("status", "pending")
                .orderBy("requestedAt", Query. Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(() -> {
                        if (! queryDocumentSnapshots.isEmpty()) {
                            // Ada pending request
                            DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                            String requestId = doc.getId();

                            // Resume listening to this request
                            pendingRequestId = requestId;
                            binding.btnChat.setText("Menunggu konfirmasi...");
                            binding.btnChat.setEnabled(false);

                            // Auto-show dialog (optional)
                            showWaitingDialog();
                            listenToRequestStatus(requestId);

                            Toast.makeText(requireContext(),
                                    "Melanjutkan request sebelumnya...",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // No pending request, show normal button
                            binding.btnChat. setText("Chat");
                            binding.btnChat.setEnabled(true);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.btnChat.setText("Chat");
                            binding.btnChat.setEnabled(true);
                        });
                    }
                });
    }
    /**
     * Listen to consultation request status changes
     */
    /**
     * Listen to consultation request status changes
     */
    //public ListenerRegistration listenToRequestStatus(String requestId, FirebaseManager.OnRequestStatusChangeListener listener) {
      //  Log.d(TAG, "🎧 Setting up listener for request: " + requestId);

        //return db.collection("consultation_requests")  // ← RETURN INI!
          //      .document(requestId)
            //    .addSnapshotListener((snapshot, error) -> {
                    //if (error != null) {
              //          Log.e(TAG, "❌ Listen failed: " + error.getMessage());
                //        listener.onError(error.getMessage());
                  //      return;
                 //   }

                    //if (snapshot != null && snapshot.exists()) {
                      //  String status = snapshot.getString("status");
                       // Log.d(TAG, "📢 Request status: " + status);

                     //   listener.onStatusChanged(status);

                        // If approved, create chat room
                       // if ("approved".equals(status)) {
                           // Log.d(TAG, "✅ Status is APPROVED, creating chat room...");

                            //ConsultationRequest req = snapshot.toObject(ConsultationRequest.class);
                            //if (req != null) {
                              //  Log.d(TAG, "   User ID: " + req.getUserId());
                                //Log.d(TAG, "   Expert ID: " + req.getExpertId());

                               // createChatRoom(req.getUserId(), req.getExpertId(),
                                 //       new OnSuccessListener() {
                                   //         @Override
                                     //       public void onSuccess(String roomId) {
                                       //         Log.d(TAG, "✅ Chat room created: " + roomId);
                                         //       listener.onRoomCreated(roomId) ;
                                           // }

//                                            @Override
  //                                          public void onFailure(String error) {
    //                                            Log.e(TAG, "❌ Failed to create room: " + error);
      //                                          listener.onError(error);
        //                                    }
          //                              });
            //                } else {
              //                  Log.e(TAG, "❌ Failed to parse ConsultationRequest");
                //            }
                  //      }
                    //} else {
                      //  Log.w(TAG, "⚠️ Snapshot is null or doesn't exist");
                    //}
                //});
    //}
    private void listenToRequestStatus(String requestId) {
        if (requestListener != null) {
            requestListener.remove();
            Log.d(TAG, "🗑️ Removed old request listener");
        }
        firebaseManager.listenToRequestStatus(requestId, new FirebaseManager.OnRequestStatusChangeListener() {
                    @Override
                    public void onStatusChanged(String status) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            switch (status) {
                                case "pending":
                                    // Still waiting
                                    break;

                                case "rejected":
                                    dismissWaitingDialog();
                                    Toast.makeText(requireContext(),
                                            "Request ditolak oleh " + txtName,
                                            Toast.LENGTH_SHORT).show();
                                    binding.btnChat.setText("Chat");
                                    binding.btnChat.setEnabled(true);
                                    break;
                            }
                        });
                    }

                    @Override
                    public void onRoomCreated(String roomId) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            dismissWaitingDialog();
                            Toast.makeText(requireContext(),
                                    "Request disetujui!  Membuka chat...",
                                    Toast.LENGTH_SHORT).show();

                            openChatRoom(roomId, expertId);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {
                            dismissWaitingDialog();
                            Toast.makeText(requireContext(),
                                    "Error: " + error,
                                    Toast.LENGTH_SHORT).show();
                            binding.btnChat.setText("Chat");
                            binding.btnChat.setEnabled(true);
                        });
                    }
                });
    }

    /**
     * Cancel pending request
     */
    private void cancelRequest() {
        // TODO: Implement cancel logic in FirebaseManager if needed
        dismissWaitingDialog();
        binding.btnChat.setText("Chat");
        binding.btnChat.setEnabled(true);
        Toast.makeText(requireContext(), "Request dibatalkan", Toast.LENGTH_SHORT).show();
    }

    /**
     * Open chat room
     */
    // Sekitar line 280-310
    private void openChatRoom(String roomId, int expertId) {
        ExpertChatFragment chatFragment = new ExpertChatFragment(); // Panggil fragment yang baru dibuat
        Bundle bundle = new Bundle();

        // Kirim data penting ke ruang chat
        bundle.putString("ROOM_ID", roomId);
        bundle.putInt("USER_ID", userId);
        bundle.putInt("EXPERT_ID", expertId);
        bundle.putString("EXPERT_NAME", txtName);

        chatFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Dismiss waiting dialog safely
     */
    private void dismissWaitingDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissWaitingDialog();
        binding = null;
    }
}