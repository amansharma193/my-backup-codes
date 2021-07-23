package com.freelannceritservices.digitalsignature;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.AcceptPendingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    Context context;
    //public URL url=null;
    ArrayList<User> al;
    //boolean check=true;
    public RequestAdapter(Context context, ArrayList al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding binding= com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding.inflate(LayoutInflater.from(context));
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        User user=al.get(position);
        holder.binding.name.setText(user.getName());
        holder.binding.colony.setText(user.getId());
        holder.binding.accept.setVisibility(View.VISIBLE);
        holder.binding.reject.setVisibility(View.VISIBLE);
        holder.binding.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Accept(user,position);
            }
        });
        holder.binding.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reject(user,position);
            }
        });
    }

    private void Accept(User user,int pos) {
        //user.setStatus("1");
        FirebaseFirestore.getInstance().collection("User").document(user.getId()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    al.remove(pos);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else
                    Toast.makeText(context, ""+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
//        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String string1) {
//                super.onPostExecute(string1);
//                if(string1.equalsIgnoreCase("success")) {
//                    notifyDataSetChanged();
//                }
//                else{
//                    Toast.makeText(context, ""+string1, Toast.LENGTH_SHORT).show();
//                }
//                Log.e("spanshoe", "post execute" + string1);
//            }
//
//            private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
//
//                StringBuilder stringBuilderObject;
//
//                stringBuilderObject = new StringBuilder();
//
//                for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
//
//                    if (check)
//
//                        check = false;
//                    else
//                        stringBuilderObject.append("&");
//
//                    stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
//
//                    stringBuilderObject.append("=");
//
//                    stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
//                }
//
//                return stringBuilderObject.toString();
//            }
//
//            @Override
//            protected String doInBackground(Void... params) {
//                HashMap<String, String> HashMapParams = new HashMap<String, String>();
//
//                HashMapParams.put("name", user.getName());
//
//                HashMapParams.put("mobile", user.getMobile());
//                HashMapParams.put("email", user.getEmail());
//                HashMapParams.put("password", user.getPassword());
//                StringBuilder stringBuilder = new StringBuilder();
//
//                try {
//                    HttpURLConnection httpURLConnectionObject;
//                    OutputStream OutPutStream;
//                    BufferedWriter bufferedWriterObject;
//                    BufferedReader bufferedReaderObject;
//                    int RC;
//
//                    url = new URL("https://192.168.137.1/aman/accept.php");
//                    Log.e("spanshoe", "image upload");
//                    HttpsTrustManager.allowAllSSL();
//                    httpURLConnectionObject = (HttpURLConnection) url.openConnection();
//
//                    httpURLConnectionObject.setReadTimeout(19000);
//
//                    httpURLConnectionObject.setConnectTimeout(19000);
//
//                    httpURLConnectionObject.setRequestMethod("POST");
//
//                    httpURLConnectionObject.setDoInput(true);
//
//                    httpURLConnectionObject.setDoOutput(true);
//
//                    OutPutStream = httpURLConnectionObject.getOutputStream();
//
//                    bufferedWriterObject = new BufferedWriter(
//
//                            new OutputStreamWriter(OutPutStream, "UTF-8"));
//
//                    bufferedWriterObject.write(bufferedWriterDataFN(HashMapParams));
//
//                    bufferedWriterObject.flush();
//
//                    bufferedWriterObject.close();
//
//                    OutPutStream.close();
//
//                    RC = httpURLConnectionObject.getResponseCode();
//
//                    if (RC == HttpsURLConnection.HTTP_OK) {
//
//                        bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
//
//                        stringBuilder = new StringBuilder();
//
//                        String RC2;
//
//                        while ((RC2 = bufferedReaderObject.readLine()) != null) {
//
//                            stringBuilder.append(RC2);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return stringBuilder.toString();
//            }
//        }
//        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
//
//        AsyncTaskUploadClassOBJ.execute();
    }

    private void Reject(User user, int position) {
        FirebaseFirestore.getInstance().collection("User").document(user.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    al.remove(position);
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else
                    Toast.makeText(context, ""+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
//        class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
//            @Override
//            protected void onPreExecute() {
//
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String string1) {
//                super.onPostExecute(string1);
//                if(string1.equalsIgnoreCase("success")) {
//                    notifyDataSetChanged();
//                }
//                else{
//                    Toast.makeText(context, ""+string1, Toast.LENGTH_SHORT).show();
//                }
//                Log.e("spanshoe", "post execute" + string1);
//            }
//
//            private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
//
//                StringBuilder stringBuilderObject;
//
//                stringBuilderObject = new StringBuilder();
//
//                for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
//
//                    if (check)
//
//                        check = false;
//                    else
//                        stringBuilderObject.append("&");
//
//                    stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
//
//                    stringBuilderObject.append("=");
//
//                    stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
//                }
//
//                return stringBuilderObject.toString();
//            }
//
//            @Override
//            protected String doInBackground(Void... params) {
//                HashMap<String, String> HashMapParams = new HashMap<String, String>();
//
//                HashMapParams.put("name", user.getName());
//
//                HashMapParams.put("mobile", user.getMobile());
//                HashMapParams.put("email", user.getEmail());
//                HashMapParams.put("password", user.getPassword());
//                StringBuilder stringBuilder = new StringBuilder();
//
//                try {
//                    HttpURLConnection httpURLConnectionObject;
//                    OutputStream OutPutStream;
//                    BufferedWriter bufferedWriterObject;
//                    BufferedReader bufferedReaderObject;
//                    int RC;
//
//                    url = new URL("https://192.168.137.1/aman/reject.php");
//                    Log.e("spanshoe", "image upload");
//                    HttpsTrustManager.allowAllSSL();
//                    httpURLConnectionObject = (HttpURLConnection) url.openConnection();
//
//                    httpURLConnectionObject.setReadTimeout(19000);
//
//                    httpURLConnectionObject.setConnectTimeout(19000);
//
//                    httpURLConnectionObject.setRequestMethod("POST");
//
//                    httpURLConnectionObject.setDoInput(true);
//
//                    httpURLConnectionObject.setDoOutput(true);
//
//                    OutPutStream = httpURLConnectionObject.getOutputStream();
//
//                    bufferedWriterObject = new BufferedWriter(
//
//                            new OutputStreamWriter(OutPutStream, "UTF-8"));
//
//                    bufferedWriterObject.write(bufferedWriterDataFN(HashMapParams));
//
//                    bufferedWriterObject.flush();
//
//                    bufferedWriterObject.close();
//
//                    OutPutStream.close();
//
//                    RC = httpURLConnectionObject.getResponseCode();
//
//                    if (RC == HttpsURLConnection.HTTP_OK) {
//
//                        bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
//
//                        stringBuilder = new StringBuilder();
//
//                        String RC2;
//
//                        while ((RC2 = bufferedReaderObject.readLine()) != null) {
//
//                            stringBuilder.append(RC2);
//                        }
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return stringBuilder.toString();
//            }
//        }
//        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
//
//        AsyncTaskUploadClassOBJ.execute();
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding binding;
        public RequestViewHolder(@NonNull com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
