package com.freelannceritservices.digitalsignature;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.freelannceritservices.digitalsignature.databinding.ActivitySearchBinding;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class SearchActivity extends AppCompatActivity {
    private URL url=null;
    ActivitySearchBinding binding;
    private boolean check=true;
    ArrayList<Family>al;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ward=binding.ward.getText().toString();
                String name=binding.name.getText().toString();
                String colony=binding.colony.getText().toString();
                class AsyncTaskUploadClass extends AsyncTask<Void, Void, String> {
                    @Override
                    protected void onPreExecute() {

                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(String string1) {
                        super.onPostExecute(string1);
                        if (string1.equalsIgnoreCase("0")){
                            Toast.makeText(SearchActivity.this, ""+string1, Toast.LENGTH_SHORT).show();
                        }
                        al=new ArrayList<>();
                        Log.e("spanshoe", "post execute" + string1);
                        Gson gson= new Gson();
                        try {
                            JSONArray js=new JSONArray(string1);
                            for(int i=0;i<js.length();i++){
                                Family family=gson.fromJson(js.get(i).toString(),Family.class);
                                al.add(family);
                            }
                        } catch (JSONException e) {
                            Log.e("spanshoe",".........."+e.getMessage());
                            Toast.makeText(SearchActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        binding.rv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        SearchAdapter adapter=new SearchAdapter(al,SearchActivity.this);
                        binding.rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }

                    private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

                        StringBuilder stringBuilderObject;

                        stringBuilderObject = new StringBuilder();

                        for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                            if (check)

                                check = false;
                            else
                                stringBuilderObject.append("&");

                            stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                            stringBuilderObject.append("=");

                            stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
                        }

                        return stringBuilderObject.toString();
                    }

                    @Override
                    protected String doInBackground(Void... params) {
                        HashMap<String, String> HashMapParams = new HashMap<String, String>();

                        HashMapParams.put("name", name);

                        HashMapParams.put("ward", ward);
                        HashMapParams.put("colony", colony);
                        StringBuilder stringBuilder = new StringBuilder();

                        try {
                            HttpURLConnection httpURLConnectionObject;
                            OutputStream OutPutStream;
                            BufferedWriter bufferedWriterObject;
                            BufferedReader bufferedReaderObject;
                            int RC;

                            url = new URL("https://192.168.137.1/aman/search.php");
                            Log.e("spanshoe", "image upload");
                            HttpsTrustManager.allowAllSSL();
                            httpURLConnectionObject = (HttpURLConnection) url.openConnection();

                            httpURLConnectionObject.setReadTimeout(19000);

                            httpURLConnectionObject.setConnectTimeout(19000);

                            httpURLConnectionObject.setRequestMethod("POST");

                            httpURLConnectionObject.setDoInput(true);

                            httpURLConnectionObject.setDoOutput(true);

                            OutPutStream = httpURLConnectionObject.getOutputStream();

                            bufferedWriterObject = new BufferedWriter(

                                    new OutputStreamWriter(OutPutStream, "UTF-8"));

                            bufferedWriterObject.write(bufferedWriterDataFN(HashMapParams));

                            bufferedWriterObject.flush();

                            bufferedWriterObject.close();

                            OutPutStream.close();

                            RC = httpURLConnectionObject.getResponseCode();

                            if (RC == HttpsURLConnection.HTTP_OK) {

                                bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));

                                stringBuilder = new StringBuilder();

                                String RC2;

                                while ((RC2 = bufferedReaderObject.readLine()) != null) {

                                    stringBuilder.append(RC2);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return stringBuilder.toString();
                    }
                }
                AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();

                AsyncTaskUploadClassOBJ.execute();
            }
        });
    }
}