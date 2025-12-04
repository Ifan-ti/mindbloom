package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.project.client.RetrofitClient;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutGantiPasswordBinding;
import com.project.request.ResetPasswordRequest;
import com.project.response.ApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {

    private LayoutGantiPasswordBinding binding;
    private String resetToken, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = LayoutGantiPasswordBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from arguments
        if (getArguments() != null) {
            resetToken = getArguments().getString("reset_token");
            email = getArguments().getString("email");
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.btnContinue.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = binding.NewPasswordInput.getText().toString().trim();
        String confirmPassword = binding.ConfirmationPasswordInput.getText().toString().trim();

        // Validation
        if (! validateInputs(newPassword, confirmPassword)) {
            return;
        }

        setLoadingState(true);

        ResetPasswordRequest request = new ResetPasswordRequest(
                resetToken,
                newPassword,
                confirmPassword
        );

        RetrofitClient.getApiService(requireContext())
                .resetPassword(request)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        setLoadingState(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse apiResponse = response.body();

                            if (apiResponse.isSuccess()) {
                                Toast.makeText(getContext(),
                                        "Password reset successfully!",
                                        Toast.LENGTH_LONG).show();

                                // Navigate to login

                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R. id.fragment_container,new Fragment_Login())
                                            .addToBackStack(null)
                                            .commit();
                                }

                            } else {
                                Toast.makeText(getContext(),
                                        apiResponse.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Failed to reset password.Please try again.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        setLoadingState(false);
                        Toast.makeText(getContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputs(String newPassword, String confirmPassword) {
        if (newPassword.isEmpty()) {
            binding.NewPasswordInput.setError("Password is required");
            binding.NewPasswordInput.requestFocus();
            return false;
        }

        if (newPassword.length() < 6) {
            binding.NewPasswordInput.setError("Password must be at least 6 characters");
            binding.NewPasswordInput.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            binding.ConfirmationPasswordInput.setError("Please confirm your password");
            binding.ConfirmationPasswordInput.requestFocus();
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            binding.ConfirmationPasswordInput.setError("Passwords do not match");
            binding.ConfirmationPasswordInput.requestFocus();
            Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setLoadingState(boolean isLoading) {
        binding.btnContinue.setEnabled(!isLoading);
        binding.btnContinue.setText(isLoading ?  "Resetting..." : "Reset Password");
        binding.NewPasswordInput.setEnabled(!isLoading);
        binding.ConfirmationPasswordInput.setEnabled(! isLoading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}