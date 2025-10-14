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

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Fragment_Login extends Fragment {
    public Fragment_Login(){
    }

    public interface OnFragmentInteractionListener {
        void onNavigateToRegistration();
        void onNavigateToLogin();
    }



    private OnFragmentInteractionListener mListener;
    private FirebaseAuth mAuth;
    private EditText EmailInput, PasswordInput;
    private Button ConfirmatisiButton;
    private TextView btnSignUp;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_signin, container, false);

        EmailInput = view.findViewById(R.id.EmailInput);
        PasswordInput = view.findViewById(R.id.PasswordInput);
        ConfirmatisiButton = view.findViewById(R.id.btnContinue);
        btnSignUp = view.findViewById(R.id.btnsignup);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onNavigateToRegistration(); // Panggil interface Activity
                }
            }
        });

        ConfirmatisiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    private void LoginUser(){
        String email = EmailInput.getText().toString();
        String password = PasswordInput.getText().toString();

        if(email.isEmpty()){
            Toast.makeText(getContext(), "Silihankan Isi Email Anda", Toast.LENGTH_SHORT).show();
        }
        if(password.isEmpty()){
            Toast.makeText(getContext(), "Silahkan Isi Password Anda", Toast.LENGTH_SHORT).show();
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Login Gagal", Toast.LENGTH_SHORT).show();
                    }
                });





    }


}


