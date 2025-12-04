package com.project.mindbloom.Fragment;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.mindbloom.R;
import com.project.model.ExpertsDetailModel;
import com.project.model.ExpertsModel;
import com.project.response.ExpertsDetailResponse;

import com.project.mindbloom.databinding.LayoutExpertsProfileBinding;
import com.project.response.ExpertsRensponse;
import com.project.service.ApiService;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilExpertFragment extends Fragment {

    private LayoutExpertsProfileBinding binding;
    private ApiService apiService;
    private SessionManager sessionManager;
    private int userId, expertId;
    private String  username, job, bio, license, photoUrl;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutExpertsProfileBinding.inflate(inflater, container, false);

        // Initialize Service
        apiService = RetrofitClient.getApiService(requireContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Ambil Data dari Argument/Bundle

        // 2. Setup UI Awal


        // 3. Load Detail Expert & Status Chat
        fetchDetailExperts();
        Nav();

        binding.btnChat.setText("LogOut");
        binding.btnChat.setOnClickListener(v -> {
            sessionManager.clearSession();
            navigateToFragment(new Fragment_Login());
        });


        // 5. Listener Tombol Back
    }

    private void fetchDetailExperts() {
        Call<ExpertsRensponse> call = apiService. getExpert();

        call. enqueue(new Callback<ExpertsRensponse>() {
            @Override
            public void onResponse(Call<ExpertsRensponse> call, Response<ExpertsRensponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ExpertsRensponse body = response.body();
                    List<ExpertsModel> expertslist = body.getData();

                    if (expertslist != null && ! expertslist.isEmpty()) {
                        int currentUserId = sessionManager.getUserId(); // ✅ Ambil user_id (misalnya 4)

                        // ✅ LOOP untuk cari expert yang user_id-nya = 4
                        boolean found = false;
                        for (ExpertsModel expert : expertslist) {
                            // Sesuaikan dengan nama method di ExpertsModel
                            if (expert.getUser_id() == currentUserId) {  // Atau expert.getUserId()
                                expertId = expert.getId();
                                username = expert.getName();
                                job = expert.getExpertise_area();

                                // ✅ Fetch detail setelah dapat expertId
                                fetchDetailExperts(expertId);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Log.e("ProfilExpert", "Expert dengan user_id " + currentUserId + " tidak ditemukan");
                            // TODO: Tampilkan pesan error ke user
                        }
                    } else {
                        Log.w("ProfilExpert", "Data expert kosong");
                    }
                } else {
                    Log.e("ProfilExpert", "Response gagal: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ExpertsRensponse> call, Throwable t) {
                Log.e("ProfilExpert", "Error: " + t.getMessage());
            }
        });
    }
    private void fetchDetailExperts(int id) {
        apiService.getExpertsDetail(id).enqueue(new Callback<ExpertsDetailResponse>(){
            @Override
            public void onResponse(Call<ExpertsDetailResponse> call, Response<ExpertsDetailResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    ExpertsDetailModel expertsDetails = response.body().getData();
                    if(expertsDetails != null) {
                        binding.tvName.setText(username);
                        binding.tvJob.setText(job);
                        binding.tvBio.setText(expertsDetails.getBio());
                        binding.tvLicenseNum.setText(expertsDetails.getLicense_number());

                        String base64String = expertsDetails.getAvatar();
                        if (binding.imgAvatarDetail != null) {
                            if (base64String != null && !base64String.isEmpty()) {
                                try {
                                    byte[] imageBytes = android.util.Base64.decode(base64String, android.util. Base64.DEFAULT);
                                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    binding.imgAvatarDetail. setImageBitmap(decodedImage);
                                } catch (IllegalArgumentException e) {
                                    binding.imgAvatarDetail.setImageResource(R.drawable.add_icon);
                                }
                            } else {
                                binding.imgAvatarDetail.setImageResource(R.drawable.add_icon);
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ExpertsDetailResponse> call, Throwable t) {
                // Ignore error detail, focus to chat func
            }
        });
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



