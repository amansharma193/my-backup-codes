package com.transporteruser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;


import com.transporteruser.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicy extends AppCompatActivity {
    ActivityPrivacyPolicyBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

}
