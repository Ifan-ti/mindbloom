package com.project.mindbloom;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore; // <-- (1) Import Firestore

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import java.util.zip.Inflater;

public class Fragment_Registrasi extends Fragment {

    private Fragment_Login.OnFragmentInteractionListener mListener;
    private FirebaseAuth Maunt;
    private FirebaseFirestore db;
    private EditText EmailInput, PasswordInput, UsernameInput;
    private Button ContinueBtn;
    private TextView BtnSignIn;

    public Fragment_Registrasi(){
    }

    public interface OnFragmentInteractionListener {
        void onNavigateToRegistration();
        void onNavigateToLogin();
    }
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Fragment_Login.OnFragmentInteractionListener) {
            mListener = (Fragment_Login.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Maunt = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_registrasi, container, false);

        EmailInput = view.findViewById(R.id.EmailInput);
        PasswordInput = view.findViewById(R.id.PasswordInput);
        UsernameInput = view.findViewById(R.id.UsernameInput);
        ContinueBtn = view.findViewById(R.id.btnContinue);
        BtnSignIn = view.findViewById(R.id.btnsignup);

        BtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNavigateToLogin(); // Panggil interface Activity
                }
            }
        });

        ContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttempRegisterUser();
            }
        });
        return view;
    }

    private void AttempRegisterUser(){
        String Username, Email, Password;
        Username = UsernameInput.getText().toString().trim();
        Email = EmailInput.getText().toString().trim();
        Password = PasswordInput.getText().toString().trim();

        if(Username.isEmpty()){
            Toast.makeText(getContext(), "Silihankan Isi Username Anda", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Email.isEmpty()){
            Toast.makeText(getContext(), "Silihankan Isi Email Anda", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Password.isEmpty()){
            Toast.makeText(getContext(), "Silihankan Isi Password Anda", Toast.LENGTH_SHORT).show();
            return;
        }else if (Password.length() < 6) {
            Toast.makeText(getContext(), "Silihankan Isi Password Lebih Dari 6 Karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterUser(Email, Password, Username);

    }
    private void RegisterUser(String Email, String Password, String Username){
        Maunt.createUserWithEmailAndPassword( Email, Password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()){
                        FirebaseUser firebaseUser = Maunt.getCurrentUser();

                        if(firebaseUser != null){
                            saveUserDataToFirestore(firebaseUser.getUid(), Username, Email);
                            if (mListener != null) {
                                mListener.onNavigateToLogin(); // Panggil interface Activity
                            }
                        }else {
                            Toast.makeText(getContext(), "Registrasi Berhasil, tapi Id gagal disimpan", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        Toast.makeText(getContext(), "Registrasi Gagal", Toast.LENGTH_SHORT).show();
                        return;
                    }


                });


    }
    private void saveUserDataToFirestore(String UserId, String username, String email) {

        String DefaultRole = "User";

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("role", DefaultRole);



        db.collection("User")
                .document(UserId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Data gagal disimpan", Toast.LENGTH_SHORT).show();

                });





    }

}

