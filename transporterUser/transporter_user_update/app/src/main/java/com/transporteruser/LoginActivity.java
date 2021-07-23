package com.transporteruser;

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
    private  static  final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isInternetConnected = NetworkUtility.checkInternetConnection(LoginActivity.this);
        if(isInternetConnected) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.PhoneBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build());

            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setLogo(R.drawable.applogo)
                            .setTheme(R.style.AppTheme)

                            .build(),
                    RC_SIGN_IN);
        }
        else{
            Toast.makeText(this, "Please enable internet connection", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Toast.makeText(this,"signin Success",Toast.LENGTH_SHORT).show();
                Intent in=new Intent(LoginActivity.this,MainActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(in);
                finish();
            } else {
                Toast.makeText(this,"SignIn Failed",Toast.LENGTH_SHORT).show();

            }
        }
    }

}
