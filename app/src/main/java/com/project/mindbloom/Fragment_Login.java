// Lokasi: com/project/mindbloom/Fragment_Login.java
package com.project.mindbloom;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.request.LoginRequest;
import com.project.response.LoginResponse;
import com.project.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Login extends Fragment {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnContinue;
    private View btnSignUp;

    private ApiService apiService;
    private SessionManager sessionManager;
    private int userId;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Pastikan R.layout.layout_signin adalah nama file XML yang benar
        return inflater.inflate(R.layout.layout_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = RetrofitClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());

        // Cek jika sudah login (token masih ada), langsung ke Homepage
        if (sessionManager.isLoggedIn()) {
            if (isNetworkAvailable()) {
                navigateToHomepage();
            } else {
                sessionManager.clearSession(); // hapus sesi lama kalau offline
            }
        }

        // Inisialisasi Views (Pastikan ID di layout_signin.xml benar)
        etEmail = view.findViewById(R.id.EmailInput);
        etPassword = view.findViewById(R.id.PasswordInput);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnSignUp = view.findViewById(R.id.btnsignup);

        btnContinue.setOnClickListener(v -> {
            performLogin();
        });

        btnSignUp.setOnClickListener(v -> {
            navigateToRegistration();
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    private void performLogin() {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
            if (email.isEmpty()) etEmail.requestFocus();
            else etPassword.requestFocus();
            return;
        }

        // 1. Buat Request Body
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        // 2. Panggil API
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    // 3. Cek Status dari Body (sesuai API Node.js: status: 'success')
                    if ("success".equals(loginResponse.getStatus())) {

                        // Ambil data yang dibutuhkan (token, ID, email)
                        String token = loginResponse.getData().getToken();
                        userId = loginResponse.getData().getUser().getId();
                        String userEmail = loginResponse.getData().getUser().getEmail();

                        // 4. Simpan Sesi (Token & ID User)
                        sessionManager.saveLoginSession(token, userId, userEmail);
                        Toast.makeText(getContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();

                        // 5. Navigasi ke Homepage
                            navigateToHomepage();



                    } else {
                        // Kasus login gagal (misal email/password salah, kode 401/404/200, tapi status 'error')
                        Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                        sessionManager.clearSession();
                    }
                } else {
                    // Penanganan Respons HTTP Gagal (misal 401 Unauthorized dari API)
                    sessionManager.clearSession();
                    Log.e("LOGIN_FAIL", "Respon Gagal. Kode: " + response.code());
                    Toast.makeText(getContext(), "Login gagal. Email atau password salah.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Penanganan Koneksi Gagal (misal: internet mati, ngrok offline)
                sessionManager.clearSession();
                Log.e("LOGIN_ERROR", "Koneksi Gagal: " + t.getMessage());
                Toast.makeText(getContext(), "Koneksi gagal. Periksa internet atau server Anda.", Toast.LENGTH_LONG).show();

                etPassword.setText("");
                etPassword.requestFocus();
            }
        });
    }

    private void navigateToHomepage() {
        if (getActivity() == null) return;

        Intent intent = new Intent(getActivity(), Homepage_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("IdUser", userId);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void navigateToRegistration() {
        if (getActivity() != null) {
            // Pindah ke Fragment Registrasi
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Fragment_Registrasi())
                    .addToBackStack(null) // Penting untuk tombol back
                    .commit();
        }
    }
}