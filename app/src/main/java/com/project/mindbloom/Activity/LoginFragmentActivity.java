package com.project.mindbloom.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.project.mindbloom.Fragment.Fragment_Login;
import com.project.mindbloom.R;


import android.os.Bundle;

// Mengimplementasikan interface di deklarasi kelas
public class LoginFragmentActivity extends AppCompatActivity {


    // Asumsi ID container yang benar (R.id.fragment_container) sudah ada di layout XML
    private static final int CONTAINER_ID = R.id.fragment_container;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Pastikan nama layout file adalah activity_main.xml
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            // Pastikan nama kelas Fragment Anda adalah LoginFragment
            Fragment_Login loginFragment = new Fragment_Login();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(CONTAINER_ID, loginFragment);
            transaction.commit();
        }
    }




}