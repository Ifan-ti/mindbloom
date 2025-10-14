package com.project.mindbloom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;

// Mengimplementasikan interface di deklarasi kelas
public class ActivityMain extends AppCompatActivity implements Fragment_Login.OnFragmentInteractionListener {


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

    @Override
    public void onNavigateToRegistration () {
        // 1. Buat instance Fragment tujuan
        Fragment_Registrasi registrationFragment = new Fragment_Registrasi();

        // 2. Lakukan Transaksi Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(CONTAINER_ID, registrationFragment)
                .addToBackStack(null) // PENTING: Untuk kembali ke Login saat Back ditekan
                .commit();
    }

    @Override
    public void onNavigateToLogin () {
        // 1. Buat instance Fragment tujuan
        Fragment_Login LoginFragment = new Fragment_Login();

        // 2. Lakukan Transaksi Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(CONTAINER_ID, LoginFragment)
                .addToBackStack(null)
                .commit();
    }
}