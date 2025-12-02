package com.project.mindbloom;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class uploadPostingan_Activity extends AppCompatActivity {

    private ImageView imgPreview;
    private EditText etCaption;
    private Button btnPost;
    private ImageButton btnBack;

    private Uri selectedImageUri = null;

    // Launcher untuk memilih gambar
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Launcher untuk permission
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_postingan);

        imgPreview = findViewById(R.id.imgAddPhoto);
        etCaption = findViewById(R.id.etCaption);
        btnPost = findViewById(R.id.btnPosting);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        // ==== 1. Register permission launcher ====
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Izin akses foto ditolak", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // ==== 2. Register gallery picker ====
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgPreview.setImageURI(selectedImageUri);
                    }
                }
        );

        // Klik untuk memilih foto
        imgPreview.setOnClickListener(v -> checkPermissionAndOpenGallery());

        // Upload post
        btnPost.setOnClickListener(v -> uploadPost());
    }

    // ==== CEK PERMISSION ====
    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }

    // ==== BUKA GALERI ====
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    // ==== SIMPAN DATA POST SEMENTARA ====
    private void uploadPost() {
        String caption = etCaption.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Pilih foto terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (caption.isEmpty()) {
            Toast.makeText(this, "Tulis caption terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        savePostData(caption, selectedImageUri.toString());

        Toast.makeText(this, "Postingan siap diupload!", Toast.LENGTH_SHORT).show();

        finish();
    }

    private void savePostData(String caption, String imageUri) {
        SharedPreferences prefs = getSharedPreferences("postData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("caption", caption);
        editor.putString("imageUri", imageUri);
        editor.apply();
    }
}
