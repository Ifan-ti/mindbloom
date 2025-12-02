package com.project.mindbloom.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.project.mindbloom.R;

public class RegistrasiActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_registrasi);

        TextView btn_signin = findViewById(R.id.btnsignup);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrasiActivity.this, LoginFragmentActivity.class);

                startActivity(intent);
            }
        });

    }
}
