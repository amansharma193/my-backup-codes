package com.transportervendor;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final int Rc_Sign_IN=123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
             if(NetworkUtil.getConnectivityStatus(this)) {
                 List<AuthUI.IdpConfig> providers = Arrays.asList(
                         new AuthUI.IdpConfig.PhoneBuilder().build(),
                         new AuthUI.IdpConfig.EmailBuilder().build()
                 );
                 startActivityForResult(AuthUI.getInstance()
                         .createSignInIntentBuilder()
                         .setAvailableProviders(providers)
                         .setTheme(R.style.SignUpTheme)
                         .build(), Rc_Sign_IN);
             }
             else
                 Toast.makeText(this, "please enable internet connection", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Rc_Sign_IN){
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if (resultCode==RESULT_OK){
                Toast.makeText(this, "Sign In Success", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(LoginActivity.this,HomeActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
            }
            else
                Toast.makeText(this, "Sign In failed", Toast.LENGTH_SHORT).show();
        }
    }

}
