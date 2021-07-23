package com.freelannceritservices.digitalsignature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.freelannceritservices.digitalsignature.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

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

public class SignUpAsAdmin extends AppCompatActivity {
    ActivitySignUpBinding binding;
    boolean check = true;
    //URL url = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.username.getText().toString();
                String pass = binding.password.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    binding.username.setError("this field can't be empty");
                    binding.username.setFocusable(true);
                    return;
                }
                if (TextUtils.isEmpty(pass)) {
                    binding.password.setError("this field can't be empty");
                    binding.password.setFocusable(true);
                    return;
                }
//                User user = new User(name, "", mobile, pass, email,"0");
                FirebaseFirestore.getInstance().collection("Admin").whereEqualTo("name",name).whereEqualTo("password",pass).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> hm = document.getData();
                                String mobile1 = (String) hm.get("mobile");
                                String name1 = (String) hm.get("name");
                                String pass1 = (String) hm.get("password");
                                String email1 = (String) hm.get("email");
                                if (name.equalsIgnoreCase(name1)) {
                                    if (pass.equalsIgnoreCase(pass1)) {
                                        SharedPreferences mPrefs = getSharedPreferences("Digital", MODE_PRIVATE);
                                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                        prefsEditor.putString("user", "admin");
                                        prefsEditor.commit();
                                        check = false;
                                        Intent i = new Intent(SignUpAsAdmin.this, HomeActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            }
                            if (check)
                                Toast.makeText(SignUpAsAdmin.this, "Admin not found. Please check the filled information.", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(SignUpAsAdmin.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
//                FirebaseFirestore.getInstance().collection("Admin").document(id).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()){
//
//                        }else{
//                            Toast.makeText(SignUpAsAdmin.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//                class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
//                    @Override
//                    protected void onPreExecute() {
//
//                        super.onPreExecute();
//                    }
//
//                    @Override
//                    protected void onPostExecute(String string1) {
//                        super.onPostExecute(string1);
//                        if(string1.equalsIgnoreCase("1")) {
//                            SharedPreferences mPrefs=getSharedPreferences("Digital",MODE_PRIVATE);
//                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
//                            prefsEditor.putString("user", "admin");
//                            prefsEditor.commit();
//                            Intent i = new Intent(SignUpAsAdmin.this, HomeActivity.class);
//                            startActivity(i);
//                            finish();
//                        }
//                        else{
//                            Toast.makeText(SignUpAsAdmin.this, "You are not an admin", Toast.LENGTH_SHORT).show();
//                        }
//                        Log.e("spanshoe", "post execute" + string1);
//                    }
//
//                    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
//
//                        StringBuilder stringBuilderObject;
//
//                        stringBuilderObject = new StringBuilder();
//
//                        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
//
//                            if (check)
//
//                                check = false;
//                            else
//                                stringBuilderObject.append("&");
//
//                            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
//
//                            stringBuilderObject.append("=");
//
//                            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
//                        }
//
//                        return stringBuilderObject.toString();
//                    }
//
//                    @Override
//                    protected String doInBackground(Void... params) {
//                        HashMap<String, String> HashMapParams = new HashMap<String, String>();
//
//                        HashMapParams.put("name", name);
//
//                        HashMapParams.put("mobile", mobile);
//                        HashMapParams.put("email", email);
//                        HashMapParams.put("password", pass);
//                        StringBuilder stringBuilder = new StringBuilder();
//
//                        try {
//                            HttpURLConnection httpURLConnectionObject;
//                            OutputStream OutPutStream;
//                            BufferedWriter bufferedWriterObject;
//                            BufferedReader bufferedReaderObject;
//                            int RC;
//
//                            url = new URL("https://192.168.137.1/aman/loginasadmin.php");
//                            Log.e("spanshoe", "image upload");
//                            HttpsTrustManager.allowAllSSL();
//                            httpURLConnectionObject = (HttpURLConnection) url.openConnection();
//
//                            httpURLConnectionObject.setReadTimeout(19000);
//
//                            httpURLConnectionObject.setConnectTimeout(19000);
//
//                            httpURLConnectionObject.setRequestMethod("POST");
//
//                            httpURLConnectionObject.setDoInput(true);
//
//                            httpURLConnectionObject.setDoOutput(true);
//
//                            OutPutStream = httpURLConnectionObject.getOutputStream();
//
//                            bufferedWriterObject = new BufferedWriter(
//
//                                    new OutputStreamWriter(OutPutStream, "UTF-8"));
//
//                            bufferedWriterObject.write(bufferedWriterDataFN(HashMapParams));
//
//                            bufferedWriterObject.flush();
//
//                            bufferedWriterObject.close();
//
//                            OutPutStream.close();
//
//                            RC = httpURLConnectionObject.getResponseCode();
//
//                            if (RC == HttpsURLConnection.HTTP_OK) {
//
//                                bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
//
//                                stringBuilder = new StringBuilder();
//
//                                String RC2;
//
//                                while ((RC2 = bufferedReaderObject.readLine()) != null) {
//
//                                    stringBuilder.append(RC2);
//                                }
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return stringBuilder.toString();
//                    }
//                }
//                AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
//
//                AsyncTaskUploadClassOBJ.execute();
            }
        });
    }
}