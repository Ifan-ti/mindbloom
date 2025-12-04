package com.project.mindbloom.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation. Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com. google.gson.JsonObject;
import com.project. mindbloom.R;
import com.project.response.DefaultResponse;
import com.project.response.StatusResponse;
import com.project. adapter.PatientDetailAdapter;
import com.project. client.RetrofitClient;
import com. project.client.SessionManager;
import com.project. mindbloom.databinding. LayoutListpasientBinding;
import com.project. model.PatientDetailModel;
import com.project. response.PatientDetailResponse;
import com.project.service.ApiService;

// ✅ TAMBAHKAN IMPORT PUSHER
import com.pusher. client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher. client.channel.Channel;
import com.pusher. client.channel.PusherEvent;
import com.pusher. client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection. ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailFragment extends Fragment {

    private LayoutListpasientBinding binding;
    private PatientDetailAdapter patientdetailadapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    private boolean isSearchMode = false;

    // ✅ TAMBAHKAN FIELD PUSHER
    private Pusher pusher;
    private Channel expertChannel;
    private int expertUserId;
    private static final String PUSHER_APP_KEY = "1be69e5ad5a25ed551d6";
    private static final String PUSHER_CLUSTER = "ap1";
    private SubscriptionEventListener incomingRequestListener;
    private SubscriptionEventListener approvedExpertListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutListpasientBinding.inflate(inflater, container, false);

        apiService = RetrofitClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
        expertUserId = sessionManager.getUserId(); // ✅ Ambil user_id expert

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        fetchPatientList();

        // ✅ TAMBAHKAN: Setup Pusher untuk listen incoming-request
        setupPusherListener();
        setupSearchFunctionality();
        setupBackPressHandler();
        Nav();
    }

    // Tambahkan di bagian onViewCreated(), setelah setupRecyclerView()

    private void setupSearchFunctionality() {

        // 1. Listener saat EditText mendapat fokus (diklik)
        binding.SearchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showSearchLayout(); // Masuk mode search
            }
        });

        // 2. Listener saat user mengetik
        binding.SearchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // Filter data berdasarkan teks yang diketik
                if (patientdetailadapter != null) {
                    patientdetailadapter.filter(s.toString());
                }
            }
        });

        // 3. Listener tombol search
        binding.btnSearch.setOnClickListener(v -> {
            String query = binding.SearchInput.getText().toString();
            if (patientdetailadapter != null) {
                patientdetailadapter.filter(query);
            }
            hideKeyboard();
        });
    }

    // ✅ METHOD BARU: Tampilkan Layout Search Mode
    private void showSearchLayout() {
        isSearchMode = true;

        // Sembunyikan elemen yang tidak perlu saat search
        // (Sesuaikan dengan layout Anda jika ada elemen lain yang perlu disembunyikan)

        // Ubah tinggi RecyclerView jadi match_parent (memenuhi layar)
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) binding.rvListPasient.getLayoutParams();
        params.height = 0; // 0dp = match_constraint
        binding.rvListPasient.setLayoutParams(params);
    }

    // ✅ METHOD BARU: Kembalikan Layout Normal
    private void hideSearchLayout() {
        isSearchMode = false;

        // Kembalikan tinggi RecyclerView ke wrap_content
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams params =
                (androidx.constraintlayout. widget.ConstraintLayout. LayoutParams) binding.rvListPasient.getLayoutParams();
        params.height = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT;
        binding. rvListPasient.setLayoutParams(params);

        // Reset search input
        binding.SearchInput.setText("");
        binding.SearchInput.clearFocus();
        hideKeyboard();

        // ✅ PERBAIKAN: Panggil filter dengan string kosong (akan restore originalList)
        if (patientdetailadapter != null) {
            patientdetailadapter. filter(""); // Restore data original
        }
    }
    private void setupBackPressHandler() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new androidx.activity.OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (isSearchMode) {
                            // Jika dalam mode search, keluar dari mode search
                            hideSearchLayout();
                        } else {
                            // Jika tidak, lanjutkan behavior back normal
                            setEnabled(false); // Disable callback ini
                            requireActivity().onBackPressed(); // Panggil back normal
                        }
                    }
                }
        );
    }

    // ✅ METHOD BARU: Sembunyikan Keyboard
    private void hideKeyboard() {
        try {
            android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) requireContext()
                            .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null && getView() != null) {
                imm.hideSoftInputFromWindow(binding.SearchInput.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding keyboard: " + e.getMessage());
        }
    }

    // ✅ OVERRIDE: Handle tombol Back

    private void setupRecyclerView() {
        patientdetailadapter = new PatientDetailAdapter(getContext());
        binding. rvListPasient.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListPasient.setAdapter(patientdetailadapter);

        patientdetailadapter.setOnItemClickListener(new PatientDetailAdapter.onItemClickListener() {
            @Override
            public void onItemClick(PatientDetailModel patient) {
                int expertId = sessionManager.getUserId();
                int patientId = 0;

                try {
                    patientId = Integer.parseInt(patient.getUserId());
                } catch (NumberFormatException e) {
                    Log. e(TAG, "Error parsing User ID: " + e.getMessage());
                    return;
                }

                String requestStatus = patient.getRequestStatus();
                String roomId = patient. getRoomId();

                // ✅ HANDLE BERDASARKAN STATUS
                if ("pending".equals(requestStatus)) {
                    // Tampilkan dialog approve/reject
                    showApproveDialog(patientId, expertId, patient.getName());
                } else if ("approved". equals(requestStatus) && roomId != null) {
                    // Buka chat langsung (tanpa cek API lagi)
                    openChatRoom(roomId, patientId, expertId, patient.getName());
                } else {
                    Toast.makeText(getContext(), "Chat room belum aktif", Toast. LENGTH_SHORT).show();
                }
            }
        });
    }
    private void showApproveDialog(int patientId, int expertUserId, String patientName) {
        // ✅ Inflate custom layout
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_patient_request, null);

        // ✅ Ambil referensi UI elements
        android.widget.TextView tvSubtitle = dialogView.findViewById(R.id.tvSubtitle);
        com.google.android.material.button.MaterialButton btnReject = dialogView.findViewById(R.id.btnReject);
        com.google.android.material.button.MaterialButton btnApprove = dialogView.findViewById(R.id.btnApprove);

        // ✅ Set subtitle dengan nama patient
        tvSubtitle.setText(patientName + " Melakukan Request Konsultasi");

        // ✅ Buat AlertDialog dengan custom view
        androidx.appcompat.app.AlertDialog. Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setCancelable(true);

        androidx.appcompat.app.AlertDialog dialog = builder.create();

        // ✅ Set background transparent & animasi
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color. transparent);

            // ✅ Tambahkan animasi (OPSIONAL - hapus jika tidak mau animasi)
            dialog.getWindow().getAttributes().windowAnimations = R. style.DialogAnimation;
        }

        // ✅ Handle button clicks
        btnReject.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(getContext(), "Request ditolak", Toast.LENGTH_SHORT).show();
            // TODO: Implementasi reject request (opsional)
        });

        btnApprove.setOnClickListener(v -> {
            dialog.dismiss();
            approveRequest(patientId, expertUserId, patientName);
        });

        dialog.show();
    }
    private void approveRequest(int patientId, int expertUserId, String patientName) {
        // Tampilkan loading
        Toast.makeText(getContext(), "Menyetujui request.. .", Toast.LENGTH_SHORT).show();

        // Buat payload
        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("user_id", patientId);        // ID Patient
        body.put("expert_id", expertUserId);   // User ID Expert (akan dikonversi di backend)

        // Panggil API
        apiService.approveRequestByUser(body).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DefaultResponse result = response.body();

                    if ("success".equals(result.getStatus())) {
                        Toast.makeText(getContext(),
                                "Request dari " + patientName + " disetujui!",
                                Toast.LENGTH_LONG).show();

                        // ✅ Refresh list setelah 1 detik
                        new android.os.Handler().postDelayed(() -> {
                            fetchPatientList();
                        }, 1000);

                    } else {
                        Toast.makeText(getContext(),
                                "Gagal approve: " + result.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Server error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Koneksi error: " + t.getMessage(),
                        Toast.LENGTH_SHORT). show();
                Log.e(TAG, "Approve failed: " + t.getMessage());
            }
        });
    }


    // ✅ TAMBAHKAN METHOD BARU: Setup Pusher Listener
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

        String channelName = "expert-" + expertUserId;
        expertChannel = pusher.subscribe(channelName);

        Log.d(TAG, "📡 Subscribed to: " + channelName);

        // ✅ Bind event 'incoming-request' HANYA SEKALI
        incomingRequestListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG, "📩 Incoming Request Event: " + event.getData());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        try {
                            Gson gson = new Gson();
                            JsonObject data = gson.fromJson(event.getData(), JsonObject.class);

                            int userId = data.get("user_id").getAsInt();
                            String userName = data.has("name") ? data.get("name"). getAsString() : "User";

                            Log.d(TAG, "🔔 New request from: " + userName + " (ID: " + userId + ")");

                            Toast.makeText(getContext(),
                                    "Request baru dari " + userName + "! ",
                                    Toast.LENGTH_LONG).show();

                            new android.os.Handler().postDelayed(() -> {
                                fetchPatientList();
                            }, 1000);

                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing Pusher event: " + e. getMessage());
                        }
                    });
                }
            }
        };
        expertChannel.bind("incoming-request", incomingRequestListener);

        // ✅ Bind event 'request-approved-expert' HANYA SEKALI
        approvedExpertListener = new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                Log.d(TAG, "✅ Request Approved, refreshing list...");

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        fetchPatientList();
                    });
                }
            }
        };
        expertChannel.bind("request-approved-expert", approvedExpertListener);
    }

    private void fetchPatientList() {
        String token = sessionManager.getAuthToken();

        Call<PatientDetailResponse> call = apiService.getPatienDetail("Bearer " + token);

        call.enqueue(new Callback<PatientDetailResponse>() {
            @Override
            public void onResponse(Call<PatientDetailResponse> call, Response<PatientDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PatientDetailResponse patientResponse = response.body();

                    if ("success".equals(patientResponse. getStatus())) {
                        List<PatientDetailModel> patientList = patientResponse.getData();

                        if (patientList != null && !patientList.isEmpty()) {
                            Log.d(TAG, "📋 Patient List Size: " + patientList.size());

                            // ✅ Cek duplikasi manual (untuk debug)
                            java.util.Set<String> uniqueIds = new java.util.HashSet<>();
                            for (PatientDetailModel p : patientList) {
                                if (!uniqueIds.add(p.getUserId())) {
                                    Log.w(TAG, "⚠️ DUPLICATE PATIENT: ID=" + p.getUserId());
                                }
                            }

                            patientdetailadapter.setData(patientList);
                        } else {
                            Log. w(TAG, "Data patient kosong");
                        }
                    } else {
                        Log.e(TAG, "Status bukan success: " + patientResponse.getStatus());
                    }
                } else {
                    Log.e(TAG, "Gagal memuat detail patient: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PatientDetailResponse> call, Throwable t) {
                Log.e(TAG, "Koneksi gagal: " + t.getMessage());
                Toast.makeText(getContext(), "Gagal memuat data pasien", Toast.LENGTH_SHORT). show();
            }
        });
    }

    private void checkRoomAndNavigate(int patientId, int expertUserId, String patientName) {
        apiService.checkRequestStatusExpert(patientId, expertUserId). enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse. Data data = response.body().getData();

                    if (data != null && data.getRoomId() != null) {
                        String roomId = data.getRoomId();
                        openChatRoom(roomId, patientId, expertUserId, patientName);
                    } else {
                        Toast.makeText(getContext(), "Chat room belum aktif", Toast.LENGTH_SHORT). show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gagal mengambil data room", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Koneksi Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChatRoom(String roomId, int userId, int expertId, String expertName) {
        ExpertChatFragment chatFragment = new ExpertChatFragment();
        Bundle bundle = new Bundle();

        bundle.putString("ROOM_ID", roomId);
        bundle.putInt("USER_ID", userId);
        bundle. putInt("EXPERT_ID", expertId);
        bundle.putString("EXPERT_NAME", expertName);

        chatFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }

    // ✅ TAMBAHKAN: Cleanup Pusher
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (pusher != null && expertChannel != null) {
            try {
                // ✅ Unbind dengan listener yang sama saat bind
                if (incomingRequestListener != null) {
                    expertChannel. unbind("incoming-request", incomingRequestListener);
                }
                if (approvedExpertListener != null) {
                    expertChannel.unbind("request-approved-expert", approvedExpertListener);
                }

                pusher.unsubscribe("expert-" + expertUserId);
                pusher.disconnect();

                pusher = null;
                expertChannel = null;
                incomingRequestListener = null;
                approvedExpertListener = null;

            } catch (Exception e) {
                Log.e(TAG, "Error cleaning up Pusher: " + e.getMessage());
            }
        }

        binding = null;
    }

    public void Nav(){
        binding. navProfile.setOnClickListener(v -> navigateToFragment(new ProfilExpertFragment()));
        binding.navChat.setOnClickListener(v -> navigateToFragment(new PatientDetailFragment()));
    }

    private void navigateToFragment(Fragment fragment) {
        try {
            if (getActivity() != null && isAdded()) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}