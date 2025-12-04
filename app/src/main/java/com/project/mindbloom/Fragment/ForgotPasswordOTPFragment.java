package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.project.client.RetrofitClient;
import com.project.mindbloom.Fragment.PatientDetailFragment;
import com.project.mindbloom.databinding.LayoutOtpBinding;
import com.project.request.ForgotPasswordRequest;
import com.project.request.VerifyOTPRequest;
import com.project.response.OTPResponse;
import com.project.response.VerifyOTPResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.project.mindbloom.R;


public class ForgotPasswordOTPFragment extends Fragment {

    private LayoutOtpBinding binding;
    private String email;
    private CountDownTimer countDownTimer;
    private EditText[] otpInputs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutOtpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get email from arguments
        if (getArguments() != null) {
            email = getArguments().getString("email");
        }

        initOTPInputs();
        setupOTPInputs();
        setupClickListeners();
        startTimer();
        setResendButtonState(false);
    }

    private void initOTPInputs() {
        otpInputs = new EditText[]{
                binding.input1,
                binding.input2,
                binding.input3,
                binding.input4,
                binding.input5,
                binding.input6
        };
    }

    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> verifyOTP());
        binding.txtResend.setOnClickListener(v -> resendOTP());
    }

    private void setupOTPInputs() {
        for (int i = 0; i < otpInputs.length; i++) {
            final int index = i;
            final EditText currentInput = otpInputs[i];

            currentInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            currentInput.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentInput.getText().toString().isEmpty() && index > 0) {
                        otpInputs[index - 1].requestFocus();
                        otpInputs[index - 1].setText("");
                    }
                }
                return false;
            });
        }

        otpInputs[0].requestFocus();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                binding.txtTimer.setText(String.format("00:%02d", seconds));
            }

            @Override
            public void onFinish() {
                binding.txtTimer.setText("00:00");
                setResendButtonState(true);
                Toast.makeText(getContext(), "OTP expired", Toast.LENGTH_SHORT).show();
            }
        };

        countDownTimer.start();
    }

    private String getOTPCode() {
        StringBuilder otp = new StringBuilder();
        for (EditText input : otpInputs) {
            otp.append(input.getText().toString());
        }
        return otp.toString();
    }

    private void verifyOTP() {
        String otp = getOTPCode();

        if (otp.length() != 6) {
            Toast.makeText(getContext(),
                    "Please enter complete 6-digit OTP",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        VerifyOTPRequest request = new VerifyOTPRequest(email, otp);

        RetrofitClient.getApiService(requireContext())
                .verifyOTP(request)
                .enqueue(new Callback<VerifyOTPResponse>() {
                    @Override
                    public void onResponse(Call<VerifyOTPResponse> call,
                                           Response<VerifyOTPResponse> response) {
                        setLoadingState(false);

                        if (response.isSuccessful() && response.body() != null) {
                            VerifyOTPResponse verifyResponse = response.body();

                            if (verifyResponse.isSuccess()) {
                                if (countDownTimer != null) {
                                    countDownTimer.cancel();
                                }

                                Toast.makeText(getContext(),
                                        "OTP verified successfully",
                                        Toast.LENGTH_SHORT).show();

                                // Navigate to change password
                                Bundle bundle = new Bundle();
                                bundle.putString("reset_token", verifyResponse.getResetToken());
                                bundle.putString("email", email);

                                ChangePasswordFragment otpFragment = new ChangePasswordFragment();
                                otpFragment.setArguments(bundle);

                                // Pindah ke ForgotPasswordOTPFragment
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R. id.fragment_container, otpFragment)
                                            .addToBackStack(null)
                                            .commit();
                                }

                            } else {
                                Toast.makeText(getContext(),
                                        verifyResponse.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Invalid OTP.Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<VerifyOTPResponse> call, Throwable t) {
                        setLoadingState(false);
                        Toast.makeText(getContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resendOTP() {
        setResendButtonState(false);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        RetrofitClient.getApiService(requireContext())
                .requestOTP(request)
                .enqueue(new Callback<OTPResponse>() {
                    @Override
                    public void onResponse(Call<OTPResponse> call, Response<OTPResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isSuccess()) {
                                clearOTPInputs();
                                startTimer();
                                Toast.makeText(getContext(),
                                        "OTP has been resent to your email",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                setResendButtonState(true);
                                Toast.makeText(getContext(),
                                        response.body().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            setResendButtonState(true);
                            Toast.makeText(getContext(),
                                    "Failed to resend OTP",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OTPResponse> call, Throwable t) {
                        setResendButtonState(true);
                        Toast.makeText(getContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void clearOTPInputs() {
        for (EditText input : otpInputs) {
            input.setText("");
        }
        otpInputs[0].requestFocus();
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnContinue.setEnabled(!isLoading);
        binding.btnContinue.setText(isLoading ?  "Verifying..." : "Verify OTP");

        for (EditText input : otpInputs) {
            input.setEnabled(!isLoading);
        }
    }

    private void setResendButtonState(boolean enabled) {
        binding.txtResend.setEnabled(enabled);
        binding.txtResend.setAlpha(enabled ? 1.0f : 0.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        binding = null;
    }
}