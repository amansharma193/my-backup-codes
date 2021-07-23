package com.foodbrigade.firechat;

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

import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    EditText etemail,etpassword;
    Button btncreate;
    FirebaseAuth mAuth;
    String user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder ab=new AlertDialog.Builder(this);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            user=mAuth.getCurrentUser().getUid();
        }
        View v= LayoutInflater.from(this).inflate(R.layout.register_dialog,null);
        etemail=v.findViewById(R.id.logEmail);
        etpassword=v.findViewById(R.id.logPassword);
        btncreate=v.findViewById(R.id.btncreate);
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=etemail.getText().toString();
                String pass=etpassword.getText().toString();
                if (TextUtils.isEmpty(email)){
                    etemail.setError("this field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    etpassword.setError("this field can't be empty");
                    return;
                }
                final CustomProgressDialogue object=new CustomProgressDialogue(Register.this,"Creating New Account...");
                object.show();
                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "success", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                        }
                        else
                            Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        object.dismiss();
                    }
                });

            }
        });
        ab.setView(v);
        ab.show();
    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(Register.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
