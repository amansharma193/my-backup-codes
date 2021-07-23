package com.freelannceritservices.digitalsignature;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.freelannceritservices.digitalsignature.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

public class ForgotPassword extends AppCompatActivity {
    ActivitySignUpBinding binding;
//    public URL url=null;
    boolean check=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        binding=ActivitySignUpBinding.inflate(LayoutInflater.from(this));
//        setContentView(binding.getRoot());
//        binding.pass.setHint("New Password");
//        binding.login.setText("Reset Password");
//        binding.login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = binding.username.getText().toString();
//                String email = binding.email.getText().toString();
//                String pass = binding.password.getText().toString();
//                String mobile = binding.mobile.getText().toString();
//                if (TextUtils.isEmpty(name)) {
//                    binding.username.setError("this field can't be empty");
//                    binding.username.setFocusable(true);
//                    return;
//                }
//                if (TextUtils.isEmpty(email)) {
//                    binding.email.setError("this field can't be empty");
//                    binding.email.setFocusable(true);
//                    return;
//                }
//                if (TextUtils.isEmpty(pass)) {
//                    binding.password.setError("this field can't be empty");
//                    binding.password.setFocusable(true);
//                    return;
//                }
//                if (TextUtils.isEmpty(mobile)) {
//                    binding.mobile.setError("this field can't be empty");
//                    binding.mobile.setFocusable(true);
//                    return;
//                }
//                FirebaseFirestore.getInstance().collection("User").whereEqualTo("name",name).whereEqualTo("email",email).whereEqualTo("mobile",mobile).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            Log.e("spanshoe", "complete");
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                check=false;
//                                Log.e("spanshoe", "loop");
//                                User user=document.toObject(User.class);
//                                user.setPassword(pass);
//                                FirebaseFirestore.getInstance().collection("User").document(user.getId()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            Toast.makeText(ForgotPassword.this, "Password Reset Success. Login with New Password.", Toast.LENGTH_SHORT).show();
//                                            finish();
//                                        }else
//                                            Toast.makeText(ForgotPassword.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                            if (check){
//                                Toast.makeText(ForgotPassword.this, "User not found. Please check the filled information", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
////                class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
////                    @Override
////                    protected void onPreExecute() {
////
////                        super.onPreExecute();
////                    }
////
////                    @Override
////                    protected void onPostExecute(String string1) {
////                        super.onPostExecute(string1);
////                        if(string1.equalsIgnoreCase("success")) {
////                            Toast.makeText(ForgotPassword.this, "Success. Login with new Password.", Toast.LENGTH_SHORT).show();
////                            Intent i = new Intent(ForgotPassword  .this, Login.class);
////                            startActivity(i);
////                            finish();
////                        }
////                        else{
////                            Toast.makeText(ForgotPassword.this, ""+string1, Toast.LENGTH_SHORT).show();
////                        }
////                        Log.e("spanshoe", "post execute" + string1);
////                    }
////
////                    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
////
////                        StringBuilder stringBuilderObject;
////
////                        stringBuilderObject = new StringBuilder();
////
////                        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
////
////                            if (check)
////
////                                check = false;
////                            else
////                                stringBuilderObject.append("&");
////
////                            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
////
////                            stringBuilderObject.append("=");
////
////                            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
////                        }
////
////                        return stringBuilderObject.toString();
////                    }
////
////                    @Override
////                    protected String doInBackground(Void... params) {
////                        HashMap<String, String> HashMapParams = new HashMap<String, String>();
////
////                        HashMapParams.put("name", name);
////
////                        HashMapParams.put("mobile", mobile);
////                        HashMapParams.put("email", email);
////                        HashMapParams.put("password", pass);
////                        StringBuilder stringBuilder = new StringBuilder();
////
////                        try {
////                            HttpURLConnection httpURLConnectionObject;
////                            OutputStream OutPutStream;
////                            BufferedWriter bufferedWriterObject;
////                            BufferedReader bufferedReaderObject;
////                            int RC;
////
////                            url = new URL("https://192.168.137.1/aman/password.php");
////                            Log.e("spanshoe", "image upload");
////                            HttpsTrustManager.allowAllSSL();
////                            httpURLConnectionObject = (HttpURLConnection) url.openConnection();
////
////                            httpURLConnectionObject.setReadTimeout(19000);
////
////                            httpURLConnectionObject.setConnectTimeout(19000);
////
////                            httpURLConnectionObject.setRequestMethod("POST");
////
////                            httpURLConnectionObject.setDoInput(true);
////
////                            httpURLConnectionObject.setDoOutput(true);
////
////                            OutPutStream = httpURLConnectionObject.getOutputStream();
////
////                            bufferedWriterObject = new BufferedWriter(
////
////                                    new OutputStreamWriter(OutPutStream, "UTF-8"));
////
////                            bufferedWriterObject.write(bufferedWriterDataFN(HashMapParams));
////
////                            bufferedWriterObject.flush();
////
////                            bufferedWriterObject.close();
////
////                            OutPutStream.close();
////
////                            RC = httpURLConnectionObject.getResponseCode();
////
////                            if (RC == HttpsURLConnection.HTTP_OK) {
////
////                                bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
////
////                                stringBuilder = new StringBuilder();
////
////                                String RC2;
////
////                                while ((RC2 = bufferedReaderObject.readLine()) != null) {
////
////                                    stringBuilder.append(RC2);
////                                }
////                            }
////
////                        } catch (Exception e) {
////                            e.printStackTrace();
////                        }
////                        return stringBuilder.toString();
////                    }
////                }
////                AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
////
////                AsyncTaskUploadClassOBJ.execute();
//            }
//        });
    }
}
