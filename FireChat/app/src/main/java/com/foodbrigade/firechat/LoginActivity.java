package com.foodbrigade.firechat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    TextView btnclose,btnforgot,btnNewAc;
    EditText etemail,etpassword;
    Button btnlogin,btnphone;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    private static final int Rc_Sign_IN=123;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<AuthUI.IdpConfig>providers= Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.SignUpTheme)
                .build(),Rc_Sign_IN);
//        AlertDialog.Builder ab=new AlertDialog.Builder(this);
//        mAuth=FirebaseAuth.getInstance();
//        final View v= LayoutInflater.from(this).inflate(R.layout.login_dialog,null);
//        ab.setView(v);
//        ab.show();
//        btnclose=v.findViewById(R.id.btnclose);
//        btnforgot =v.findViewById(R.id.forgot);
//        btnNewAc=v.findViewById(R.id.newac);
//        btnNewAc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(LoginActivity.this,Register.class);
//                startActivity(intent);
//            }
//        });
//        etemail=v.findViewById(R.id.etEmail);
//        etpassword=v.findViewById(R.id.etPassword);
//        btnlogin=v.findViewById(R.id.btnlog);
//        btnphone=v.findViewById(R.id.btnphn);
//        btnlogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pd=new ProgressDialog(LoginActivity.this);
//                pd.setTitle("Authenticating...");
//                pd.setMessage("Please Wait...");
//                pd.show();
//                String email=etemail.getText().toString();
//                String pass=etpassword.getText().toString();
//                if (TextUtils.isEmpty(email)){
//                    etemail.setError("this field can't be empty.");
//                    return;
//                }
//                if (TextUtils.isEmpty(pass)){
//                    etpassword.setError("this field can't be empty.");
//                    return;
//                }
//                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        pd.dismiss();
//                        if (task.isSuccessful()){
//                            sendUserToMainActivity();
//                            Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
//                        } else{
//                            Toast.makeText(LoginActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//        btnforgot.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder ab=new AlertDialog.Builder(LoginActivity.this);
//                final EditText resetMail=new EditText(view.getContext());
//                ab.setTitle("Reset Password");
//                ab.setMessage("enter your email to get the link to reset password.");
//                ab.setView(resetMail);
//                ab.setPositiveButton("Done", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String mail=resetMail.getText().toString();
//                        if (mail==""){
//                            resetMail.setError("please enter a valid email id.");
//                            return;
//                        }
//                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(LoginActivity.this, "A link has been sent to your email to reset password.", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                });
//                ab.setNegativeButton("Cancel",new  DialogInterface.OnClickListener(){
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//                ab.show();
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Rc_Sign_IN){
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if (resultCode==RESULT_OK){
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "Sign In Success", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(LoginActivity.this,MainActivity.class);
                startActivity(in);
                finish();
            }
            else{
                Toast.makeText(this, "Sign In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //    private void sendUserToMainActivity() {
//        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
}
