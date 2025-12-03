package com.project.mindbloom.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.project.client.RetrofitClient;
import com.project.client.SessionManager;
import com.project.model.ProfileModel;
import com.project.mindbloom.R;
import com.project.mindbloom.databinding.LayoutEditProfileBinding;
import com.project.request.UpdateProfileRequest;
import com.project.response.ProfileResponse;
import com.project.service.ApiService;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    // Data dari Intent (profil lama)
    private String Username, bio, imageUrl, email;

    private LayoutEditProfileBinding binding;
    private ApiService apiService;

    // Untuk menyimpan base64 foto baru (hasil dari cropping)
    private String newBase64Image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = LayoutEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getApiService(this);

        // 1. Ambil data lama dari halaman sebelumnya
        getData();

        // 2. Tampilkan dulu data lama ke UI
        setProfileData();

        // 3. Refresh profil dari server (supaya data selalu terbaru)
        loadProfileFromServer();

        // 4. Setup tombol
        setupNav();
    }


    // =======================
    //  MENGAMBIL DATA INTENT
    // =======================
    private void getData() {
        Username = getIntent().getStringExtra("username");
        bio = getIntent().getStringExtra("bio");
        imageUrl = getIntent().getStringExtra("imageUrl");  // base64 lama
        email = getIntent().getStringExtra("email");
    }


    // ================================
    // MENAMPILKAN DATA AWAL KE UI
    // ================================
    private void setProfileData() {
        binding.tvValueUsername.setText(Username);
        binding.tvValueAccount.setText(email);
        binding.tvValueBio.setText(bio);

        // ---- Menampilkan foto lama (base64) ----
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                byte[] decoded = Base64.decode(imageUrl, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                binding.imgAvatarEdit.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ========================
    // SETUP SEMUA TOMBOL
    // ========================
    private void setupNav() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Ubah foto → buka galeri
        binding.layoutTitle.setOnClickListener(v -> openGallery());

        // Simpan perubahan profil
        binding.btnSave.setOnClickListener(v -> updateProfile());

        // Dialog edit teks
        binding.btnUsernameNext.setOnClickListener(v -> showTextInputDialog(1));
        binding.btnBioNext.setOnClickListener(v -> showTextInputDialog(2));
        binding.btnMoreAccount.setOnClickListener(v -> showTextInputDialog(3));
    }


    // =====================================================
    // DIALOG EDIT USERNAME / BIO / EMAIL
    // =====================================================
    public void showTextInputDialog(int index) {

        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.CustomRoundedDialogTheme
        );

        View customLayout = getLayoutInflater().inflate(R.layout.aset_item_dialog_input, null);
        builder.setView(customLayout);

        EditText input = customLayout.findViewById(R.id.input_field_custom);

        // Pre-chart text
        if (index == 1) input.setText(binding.tvValueUsername.getText());
        if (index == 2) input.setText(binding.tvValueBio.getText());
        if (index == 3) input.setText(binding.tvValueAccount.getText());

        builder.setPositiveButton("Oke", (dialog, which) -> {
            if (index == 1)
                binding.tvValueUsername.setText(input.getText().toString());
            if (index == 2)
                binding.tvValueBio.setText(input.getText().toString());
            if (index == 3)
                binding.tvValueAccount.setText(input.getText().toString());
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    // ==========================================================
    // LOAD PROFILE TERBARU DARI SERVER
    // ==========================================================
    private void loadProfileFromServer() {
        SessionManager sessionManager = new SessionManager(this);
        String token = sessionManager.getAuthToken();

        apiService.getProfile("Bearer " + token).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileModel profile = response.body().getData();

                    // Tampilkan gambar terbaru dari server
                    String base64 = profile.getCoverImage();
                    if (base64 != null && !base64.isEmpty()) {
                        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
                        binding.imgAvatarEdit.setImageBitmap(bitmap);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Gagal load gambar", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // ============================
    // BUKA GALERI
    // ============================
    private void openGallery() {
        getContentLauncher.launch("image/*");
    }

    private final ActivityResultLauncher<String> getContentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) startCrop(uri);
            });


    // ============================
    // MULAI UCROP
    // ============================
    private void startCrop(Uri uri) {
        String fileName = "AvatarCrop_" + System.currentTimeMillis() + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getCacheDir(), fileName));

        UCrop.Options options = new UCrop.Options();
        options.setCircleDimmedLayer(true); // supaya crop bulat
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(80);

        Intent intent = UCrop.of(uri, destinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(800, 800)
                .withOptions(options)
                .getIntent(this);

        cropLauncher.launch(intent);
    }


    private final ActivityResultLauncher<Intent> cropLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri resultUri = UCrop.getOutput(result.getData());

                    if (resultUri != null) {
                        binding.imgAvatarEdit.setImageURI(resultUri);

                        // Proses → resize → compress → encode
                        processImageForUpload(resultUri);
                    }
                }
            });


    // ==========================================================
    // ❗ PROSES FOTO → COMPRESS → BASE64 (tanpa new line)
    // ==========================================================
    private void processImageForUpload(Uri imageUri) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

            // ---- Resize gambar supaya tidak kebesaran ----
            selectedImage = getResizedBitmap(selectedImage, 800);

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            // ---- Kompres dengan loop bertahap ----
            int quality = 100;
            selectedImage.compress(Bitmap.CompressFormat.JPEG, quality, output);

            while (output.toByteArray().length / 1024 > 700 && quality > 60) {
                output.reset();
                quality -= 10;
                selectedImage.compress(Bitmap.CompressFormat.JPEG, quality, output);
            }

            // --- FIX TERPENTING ---
            // Base64.NO_WRAP → menghilangkan \n (newline) dan spasi otomatis
            // Jika tidak pakai ini, server akan gagal decode Base64!
            newBase64Image = Base64.encodeToString(
                    output.toByteArray(),
                    Base64.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
        }
    }


    // ========================================
    // RESIZE BITMAP
    // ========================================
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float ratio = (float) width / (float) height;

        if (ratio > 1) {
            width = maxSize;
            height = (int) (width / ratio);
        } else {
            height = maxSize;
            width = (int) (height * ratio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    // =====================================================
    // KIRIM DATA KE SERVER (UPDATE PROFILE)
    // =====================================================
    private void updateProfile() {

        String newUsername = binding.tvValueUsername.getText().toString().trim();
        String newBio = binding.tvValueBio.getText().toString().trim();
        String newEmail = binding.tvValueAccount.getText().toString().trim();

        if (newUsername.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- Tentukan apakah user mengganti foto atau tidak ---
        String imageToSend = (newBase64Image != null)
                ? newBase64Image   // foto baru
                : imageUrl;        // foto lama

        if (imageToSend == null) imageToSend = "";

        UpdateProfileRequest request = new UpdateProfileRequest(
                newUsername,
                newBio,
                newEmail,
                imageToSend
        );

        apiService.updateProfile(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profil berhasil diupdate!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Gagal: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error koneksi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
