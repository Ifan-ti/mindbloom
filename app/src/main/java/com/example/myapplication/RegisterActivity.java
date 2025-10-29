package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN_GOOGLE = 101;

    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister, btnGoogleRegister;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        etUsername = findViewById(R.id.txtusername);
        etEmail = findViewById(R.id.txtemail);
        etPassword = findViewById(R.id.txtpassword);
        btnRegister = findViewById(R.id.continue_button);
        btnGoogleRegister = findViewById(R.id.button_google_register);

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || email.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            if(password.length() < 6){
                Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            // Setelah profile update berhasil, kirim email verifikasi
                                            user.sendEmailVerification()
                                                    .addOnCompleteListener(task3 -> {
                                                        if(task3.isSuccessful()){
                                                            Toast.makeText(this,
                                                                    "Register berhasil! Silakan cek email untuk verifikasi.",
                                                                    Toast.LENGTH_LONG).show();
                                                            startActivity(new Intent(this, LoginActivity.class));
                                                            finish();
                                                        } else {
                                                            Toast.makeText(this,
                                                                    "Gagal mengirim email verifikasi: " + task3.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(this,
                                                    "Gagal update profile: " + task2.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(this, "Register gagal: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnGoogleRegister.setOnClickListener(v -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN_GOOGLE){
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(this, "Login/Register Google berhasil: " +
                                        user.getEmail(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HomePageActivity.class));
                                finish();
                            } else {
                                Toast.makeText(this, "Google Sign-In gagal", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e){
                e.printStackTrace();
                Toast.makeText(this, "Google Sign-In error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}





