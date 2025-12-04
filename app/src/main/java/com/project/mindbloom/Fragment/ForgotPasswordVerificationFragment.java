package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.client.RetrofitClient;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutVerifikasiBinding;
import com.project.request.ForgotPasswordRequest;
import com.project.response.OTPResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordVerificationFragment extends Fragment {

    private LayoutVerifikasiBinding binding;
    String email, resetToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutVerifikasiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            email = getArguments().getString("email");
        }
        if (getArguments() != null) {
            resetToken = getArguments().getString("reset_token");
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> sendOTPRequest());
    }

    private void sendOTPRequest() {
        String email = binding.EmailInput.getText().toString().trim();

        // Validation
        if (email.isEmpty()) {
            binding.EmailInput.setError("Email is required");
            binding.EmailInput.requestFocus();
            return;
        }

        if (!android.util.Patterns. EMAIL_ADDRESS.matcher(email). matches()) {
            binding.EmailInput.setError("Please enter a valid email");
            binding.EmailInput.requestFocus();
            return;
        }

        setLoadingState(true);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        RetrofitClient.getApiService(requireContext()).requestOTP(request)
                .enqueue(new Callback<OTPResponse>() {
                    @Override
                    public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                        setLoadingState(false);

                        if (response. isSuccessful() && response. body() != null) {
                            OTPResponse otpResponse = response.body();

                            if (otpResponse.isSuccess()) {
                                Toast.makeText(getContext(),
                                        "OTP sent to your email",
                                        Toast.LENGTH_SHORT).show();

                                // ✅ BENAR - Kirim ke ForgotPasswordOTPFragment
                                Bundle bundle = new Bundle();
                                bundle.putString("email", email);

                                ForgotPasswordOTPFragment otpFragment = new ForgotPasswordOTPFragment();
                                otpFragment.setArguments(bundle);

                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, otpFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }

                            } else {
                                Toast.makeText(getContext(),
                                        otpResponse. getMessage(),
                                        Toast. LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to send OTP.  Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OTPResponse> call, Throwable t) {
                        setLoadingState(false);
                        Toast.makeText(getContext(),
                                "Network error: " + t. getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnContinue.setEnabled(!isLoading);
        binding.btnContinue.setText(isLoading ? "Sending..." : "Send OTP Code");
        binding.EmailInput.setEnabled(!isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}