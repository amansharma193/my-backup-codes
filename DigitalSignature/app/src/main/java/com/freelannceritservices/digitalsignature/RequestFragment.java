package com.freelannceritservices.digitalsignature;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.freelannceritservices.digitalsignature.databinding.RequestFragmentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RequestFragment extends Fragment {
    ArrayList<User>al;
    RequestFragmentBinding binding;
    //private final String url = "https://192.168.137.1/aman/getAllRequests.php";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=RequestFragmentBinding.inflate(LayoutInflater.from(getContext()));
        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        al=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("User").whereEqualTo("status","0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Log.e("spanshoe","task");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        al.add(document.toObject(User.class));
                    }
                    Log.e("spanshoe","task"+al.size());
                    binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    RequestAdapter adapter=new RequestAdapter(getContext(),al);
                    binding.rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else
                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
//        class DbClass extends AsyncTask<String, String, String> {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected String doInBackground(String... s) {
//                try {
//                    URL url = new URL(s[0]);
//                    HttpsTrustManager.allowAllSSL();
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    BufferedReader bf = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//                    return bf.readLine();
//                } catch (Exception e) {
//                    Log.e("spanshoe", "" + e.getMessage());
//                    return e.getMessage();
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//                Log.e("spanshoe", "............"+s);
//                Gson gson= new Gson();
//                try {
//                    JSONArray js=new JSONArray(s.toString());
//                    for(int i=0;i<js.length();i++){
//                        User user=gson.fromJson(js.get(i).toString(),User.class);
//                        al.add(user);
//                    }
//                } catch (JSONException e) {
//                    Log.e("spanshoe",".........."+e.getMessage());
//                    e.printStackTrace();
//                }
//                //User[]u=gson.fromJson(s.toString(), User[].class);
//                //al= (ArrayList<User>) Arrays.asList();
//                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
//                RequestAdapter adapter=new RequestAdapter(getContext(),al);
//                binding.rv.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//            }
//        }
//        DbClass obj = new DbClass();
//        obj.execute(url);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        al=new ArrayList<>();
//        FirebaseFirestore.getInstance().collection("User").whereEqualTo("status","0").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        al.add(document.toObject(User.class));
//                    }
//                    binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
//                    RequestAdapter adapter=new RequestAdapter(getContext(),al);
//                    adapter.notifyDataSetChanged();
//                }else
//                    Toast.makeText(getContext(), ""+task.getException(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
