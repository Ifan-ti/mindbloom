package com.project.mindbloom. Fragment;

import android.os. Bundle;
import android.view. LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment. app.Fragment;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.model.ExpertsDetailModel;
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
        checkExistingSession(); // Check if user already has active session
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
    private void checkExistingSession() {
        firebaseManager.getActiveChatRoom(userId, new FirebaseManager.OnChatRoomListener() {
            @Override
            public void onRoomFound(com.project.model.firebase.ChatRoom room) {
                // User sudah punya active session
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Update button text
                        binding.btnChat.setText("Lanjutkan Chat");
                        binding.btnChat.setOnClickListener(v -> {
                            openChatRoom(room.getRoomId(), room.getExpertId());
                        });
                    });
                }
            }

            @Override
            public void onNoActiveRoom() {
                // No active room, normal flow
            }

            @Override
            public void onError(String error) {
                // Error checking, ignore and proceed normal
            }
        });
    }

    /**
     * Request consultation to expert
     */
    private void requestConsultation() {
        // Validate user login
        if (userId <= 0) {
            Toast.makeText(requireContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button while processing
        binding.btnChat.setEnabled(false);
        binding.btnChat.setText("Mengirim request...");

        // Create consultation request
        ConsultationRequest request = new ConsultationRequest(userId, expertId, "online");
        request.setUserName(sessionManager.getAuthToken()); // Optional: set user name if available
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
    private void showWaitingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Menunggu Konfirmasi");
        builder.setMessage("Request chat Anda sedang menunggu konfirmasi dari " + txtName + "...");
        builder.setCancelable(false);
        builder.setNegativeButton("Batalkan", (dialog, which) -> {
            cancelRequest();
        });

        waitingDialog = builder.create();
        waitingDialog.show();
    }

    /**
     * Listen to consultation request status changes
     */
    private void listenToRequestStatus(String requestId) {
        firebaseManager.listenToRequestStatus(requestId,
                new FirebaseManager.OnRequestStatusChangeListener() {
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
    private void openChatRoom(String roomId, int expertId) {
        //ExpertChatFragment chatFragment = new ExpertChatFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ROOM_ID", roomId);
        bundle.putInt("USER_ID", userId);
        bundle.putInt("EXPERT_ID", expertId);
        bundle.putString("EXPERT_NAME", txtName);
        bundle.putString("EXPERT_JOB", txtJob);

        //chatFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                //.replace(R.id.fragment_container, chatFragment)
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