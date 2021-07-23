package com.transportervendor;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.transportervendor.databinding.ActivitySplashBinding;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SplashScreen extends AppCompatActivity {
    ActivitySplashBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivitySplashBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        if(!NetworkUtil.getConnectivityStatus(this))
            Toast.makeText(this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void run() {
                    //ActivityCompat.requestPermissions(SplashScreen.this, new String[]{ READ_PHONE_STATE,READ_EXTERNAL_STORAGE,READ_SMS,ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,INTERNET,RECORD_AUDIO,ACCESS_NETWORK_STATE,WRITE_EXTERNAL_STORAGE}, 101);
                    boolean isConnected = NetworkUtil.getConnectivityStatus(SplashScreen.this);
                    if (isConnected) {
                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                        if(user==null) {
                            Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }else
                        run();

                }
            },5000);

    }

    private boolean checkLanguage() {
        SharedPreferences mprefs =getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}
