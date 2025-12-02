package com.project.mindbloom;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class uploadPostingan_Activity extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE = 100;

    private ImageView imgPreview;
    private EditText etCaption;
    private Button btnPost;
    private ImageButton btnBack;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_postingan); // pastikan nama layout sesuai XML kamu

        imgPreview = findViewById(R.id.imgAddPhoto);
        etCaption = findViewById(R.id.etCaption);
        btnPost = findViewById(R.id.btnPosting);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        requestImagePermission();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgPreview.setImageURI(selectedImageUri);
                    }
                }
        );

        imgPreview.setOnClickListener(v -> openGallery());

        btnPost.setOnClickListener(v -> {
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
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgPreview.setImageURI(selectedImageUri);
        }
    }

    private void savePostData(String caption, String imageUri) {
        SharedPreferences prefs = getSharedPreferences("postData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("caption", caption);
        editor.putString("imageUri", imageUri);
        editor.apply();
    }
}
