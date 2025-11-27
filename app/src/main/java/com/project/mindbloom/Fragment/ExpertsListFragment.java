package com.project.mindbloom.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project.adapter.ExpertListAdapter;
import com.project.client.RetrofitClient;
import com.project.model.ExpertsModel;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutExpertsListBinding;
import com.project.response.ExpertsRensponse;
import com.project.service.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpertsListFragment extends Fragment {
    private static final String TAG = "ExpertsListFragment";
    private ApiService apiService;
    private ExpertListAdapter expertlistadapter;
    private LayoutExpertsListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutExpertsListBinding.inflate(inflater, container, false);
        apiService = RetrofitClient.getApiService(requireContext());  // ✅ Gunakan requireContext()
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ✅ Setup RecyclerView
        expertlistadapter = new ExpertListAdapter(requireContext());
        binding.RecyclerViewList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.RecyclerViewList.setAdapter(expertlistadapter);

        expertlistadapter.setOnItemClickListener(new ExpertListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ExpertsModel expert) {
                // Logika pindah fragment
                ExpertDetailFragment detailFragment = new ExpertDetailFragment();
                Bundle bundle = new Bundle();

                // Masukkan data manual (Simple way)
                bundle.putString("kirim_nama", expert.getName());
                bundle.putString("kirim_job", expert.getExpertise_area());
                bundle.putInt("kirim_id", expert.getId());
                // bundle.putString("kirim_desc", expert.getDescription()); // jika ada

                detailFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // ✅ Fetch data
        fetchList();
    }

    private void fetchList() {  // ✅ Ganti nama dari fechList ke fetchList (typo fix)
        Log.d(TAG, "🔵 Fetching experts list...");

        Call<ExpertsRensponse> call = apiService.getExpert();
        call.enqueue(new Callback<ExpertsRensponse>() {
            @Override
            public void onResponse(Call<ExpertsRensponse> call, Response<ExpertsRensponse> response) {
                Log.d(TAG, "🔵 Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    ExpertsRensponse body = response.body();
                    Log.d(TAG, "🔵 Response Status: " + body.getStatus());

                    List<ExpertsModel> expertslist = body.getData();

                    if (expertslist != null && !expertslist.isEmpty()) {
                        Log.d(TAG, "🟢 Total Experts: " + expertslist.size());

                        // ✅ Log setiap expert untuk debugging
                        for (int i = 0; i < expertslist.size(); i++) {
                            ExpertsModel expert = expertslist.get(i);
                            Log.d(TAG, "Expert " + (i+1) + ": " + expert.getName() + " - " + expert.getExpertise_area());
                        }

                        expertlistadapter.setData(expertslist);
                    } else {
                        Log.e(TAG, "🔴 Expert list is empty or null");
                        showToast("No experts found");
                    }
                } else {
                    Log.e(TAG, "🔴 Response failed: " + response.code());
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "🔴 Error Body: " + errorBody);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    showToast("Failed to load experts");
                }
            }

            @Override
            public void onFailure(Call<ExpertsRensponse> call, Throwable t) {
                Log.e(TAG, "🔴 API call FAILED: " + t.getMessage());
                t.printStackTrace();
                showToast("Network error: " + t.getMessage());
            }
        });
    }

    // ✅ Helper method untuk Toast
    private void showToast(String message) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // ✅ Prevent memory leak
    }

    private void navigateToRegistration() {
        if (getActivity() != null) {
            // Di Fragment pertama
            ExpertDetailFragment secondFragment = new ExpertDetailFragment();
            Bundle bundle = new Bundle();
            bundle. putString("nama", "John Doe");
            bundle.putInt("umur", 25);
            bundle.putBoolean("isActive", true);

            secondFragment.setArguments(bundle);



            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ExpertDetailFragment())
                    .addToBackStack(null) // Penting untuk tombol back
                    .commit();
        }
    }
}