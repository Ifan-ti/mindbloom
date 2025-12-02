package com.project.mindbloom.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.project.mindbloom.Fragment.ExpertsListFragment;
import com.project.mindbloom.Fragment.Fragment_Login;
import com.project.mindbloom.R;

public class ExpertFragmentActivity extends AppCompatActivity {

    private static final int CONTAINER_ID = R.id.fragment_container;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if(savedInstanceState == null){
            ExpertsListFragment expertsFragment = new ExpertsListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(CONTAINER_ID, expertsFragment);
            transaction.commit();
        }
    }
}
