package com.transportervendor;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.transportervendor.databinding.ContactUsBinding;

public class ContactUs extends AppCompatActivity {
    ContactUsBinding  binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ContactUsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
    }
}
