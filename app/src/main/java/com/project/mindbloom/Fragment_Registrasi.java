package com.project.mindbloom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager; // Pastikan import ini ada
import com.project.request.RegisterRequest;
import com.project.response.LoginResponse; // Penting: Menggunakan LoginResponse
import com.project.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Registrasi extends Fragment {

    private ApiService apiService;
    private SessionManager sessionManager;

    private EditText EmailInput, PasswordInput, UsernameInput;
    private Button ContinueBtn;
    private TextView BtnSignIn;

    public Fragment_Registrasi() {
    }

    // --- Hapus semua kode OnFragmentInteractionListener dan onAttach di sini ---

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_registrasi, container, false);

        EmailInput = view.findViewById(R.id.EmailInput);
        PasswordInput = view.findViewById(R.id.PasswordInput);
        UsernameInput = view.findViewById(R.id.UsernameInput);
        ContinueBtn = view.findViewById(R.id.btnContinue);
        BtnSignIn = view.findViewById(R.id.btnsignup);

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigasi lokal: Kembali ke Fragment Login
                navigateToLogin();
            }
        });

        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegisterWithRetrofit();
            }
        });
        return view;
    }

    private void attemptRegisterWithRetrofit() {
        String username = UsernameInput.getText().toString().trim();
        String email = EmailInput.getText().toString().trim();
        String password = PasswordInput.getText().toString().trim();

        // Validasi
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            PasswordInput.setError("Password minimal 6 karakter");
            PasswordInput.requestFocus();
            return;
        }

        registerUser(username, email, password);
    }

    private void registerUser(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest(username, email, password);

        // Panggilan API mengharapkan LoginResponse (sesuai backend baru)
        apiService.register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if ("success".equals(loginResponse.getStatus())) {
                        // --- LOGIKA AUTO-LOGIN DAN SIMPAN ID/TOKEN ---
                        String token = loginResponse.getData().getToken();
                        int userId = loginResponse.getData().getUser().getId(); // ID user berhasil dibawa
                        String userEmail = loginResponse.getData().getUser().getEmail();

                        sessionManager.saveLoginSession(token, userId, userEmail);

                        Toast.makeText(getContext(), "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                        navigateToHomepage(); // Langsung pindah ke Homepage

                    } else {
                        // Pesan error dari server (misal: "Email sudah terdaftar" jika kode bukan 409)
                        Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Penanganan Respons Gagal HTTP (misal 409 Conflict)
                    String errorMessage = "Registrasi gagal. Cek kembali data Anda.";
                    if (response.code() == 409) {
                        errorMessage = "Email sudah terdaftar. Silakan login.";
                    } else if (response.code() == 400) {
                        errorMessage = "Data tidak valid. Periksa isian.";
                    }
                    Log.e("REGISTER_FAIL", "Kode: " + response.code() + ", Pesan: " + errorMessage);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("REGISTER_FATAL", "Koneksi Gagal: " + t.getMessage());
                Toast.makeText(getContext(), "Koneksi gagal. Periksa internet atau server Anda.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToHomepage() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), Homepage_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            // Kembali ke Fragment Login
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}