package com.e.skychat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        new SplashThread().start();
    }
    class SplashThread extends Thread{
        public void run(){
            try{
                Thread.sleep(5000);
                Intent in = new Intent(SplashActivity.this,MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
            }
            catch (Exception e){

            }
        }
    }
}
