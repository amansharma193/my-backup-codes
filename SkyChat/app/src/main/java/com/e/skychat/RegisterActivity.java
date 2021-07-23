package com.e.skychat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText etEmail,etPassword;
    Button btnCreateAccount;
    FirebaseAuth maAuth;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        maAuth = FirebaseAuth.getInstance();

        AlertDialog.Builder ab = new AlertDialog.Builder(this);

        View v = LayoutInflater.from(this).inflate(R.layout.activity_register,null);
        etEmail = v.findViewById(R.id.etEmail);
        etPassword = v.findViewById(R.id.etPassword);
        btnCreateAccount = v.findViewById(R.id.btnCreateAccount);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String email = etEmail.getText().toString();
               String password = etPassword.getText().toString();
               if(TextUtils.isEmpty(email)){
                   etEmail.setError("Email id required");
                   return;
               }
               if(TextUtils.isEmpty(password)){
                   etPassword.setError("Password required");
                   return;
               }
               maAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){
                              sendUserToMainActivity();
                          }
                          else{
                              String message = task.getException().toString();
                              Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                          }
                   }
               });
            }
        });
        ab.setView(v);
        ab.show();
    }

    private void sendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}
