package com.project.mindbloom.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.project.mindbloom.Activity.Homepage_Activity;
import com.project.mindbloom.R;
import com.project.request.LoginRequest;
import com.project.response.LoginResponse;
import com.project.service.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Login extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnContinue;
    private View btnSignUp;

    private ApiService apiService;

    private static final String PREF_NAME = "USER_PREF";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERID = "userId";

    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        // Ambil komponen layout
        etEmail = view.findViewById(R.id.EmailInput);
        etPassword = view.findViewById(R.id.PasswordInput);
        btnContinue = view.findViewById(R.id.btnContinue);
        btnSignUp = view.findViewById(R.id.btnsignup);

        // Tombol login
        btnContinue.setOnClickListener(v -> performLogin());

        // Tombol daftar
        btnSignUp.setOnClickListener(v -> navigateToRegistration());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        return net != null && net.isConnected();
    }

    private void performLogin() {

        if (!isNetworkAvailable()) {
            Toast.makeText(getContext(), "Tidak ada koneksi internet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.login(loginRequest);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if ("success".equals(loginResponse.getStatus())) {
                        // Ambil token & userId
                        String token = loginResponse.getData().getToken();
                        userId = loginResponse.getData().getUser().getId();

                        // Simpan token & userId ke SharedPreferences
                        saveToken(token, userId);

                        Toast.makeText(getContext(), "Login Berhasil!", Toast.LENGTH_SHORT).show();

                        navigateToHomepage();
                        return;
                    }

                    // Login gagal
                    clearToken();
                    Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    clearToken();
                    Toast.makeText(getContext(), "Email atau password salah.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                clearToken();
                Log.e("LOGIN_ERROR", t.getMessage());
                Toast.makeText(getContext(), "Koneksi gagal. Periksa internet atau server.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveToken(String token, int userId) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putInt(KEY_USERID, userId)
                .apply();
    }

    private void clearToken() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USERID).apply();
    }

    private void navigateToHomepage() {
        if (getActivity() == null) return;

        Intent intent = new Intent(getActivity(), Homepage_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("IdUser", userId);
        startActivity(intent);
    }

    private void navigateToRegistration() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new Fragment_Registrasi())
                    .addToBackStack(null)
                    .commit();
        }
    }
}
