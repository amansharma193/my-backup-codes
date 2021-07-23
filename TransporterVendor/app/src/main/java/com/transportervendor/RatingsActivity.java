package com.transportervendor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.transportervendor.adapter.RatingsAdapter;
import com.transportervendor.apis.TransporterService;
import com.transportervendor.beans.Rating;
import com.transportervendor.databinding.ActivityRatingsBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingsActivity extends AppCompatActivity {
    ActivityRatingsBinding binding;
    ArrayList<Rating>al;
    RatingsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRatingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        al=new ArrayList<>();
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(new RatingsAdapter(RatingsActivity.this,al));
    }
    @Override
    protected void onStart() {
        super.onStart();
        TransporterService.TransporterApi transporterApi= TransporterService.getTransporterApiInstance();
        Call<ArrayList<Rating>> call=transporterApi.getRating(FirebaseAuth.getInstance().getCurrentUser().getUid());
        call.enqueue(new Callback<ArrayList<Rating>>() {
            @Override
            public void onResponse(Call<ArrayList<Rating>> call, Response<ArrayList<Rating>> response) {
                if (response.isSuccessful()){
                    al=response.body();
                    if (al==null)
                        al=new ArrayList<>();
                    Toast.makeText(RatingsActivity.this, ""+al.size(), Toast.LENGTH_SHORT).show();
                    adapter=new RatingsAdapter(RatingsActivity.this,al);
                    binding.rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(RatingsActivity.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Rating>> call, Throwable t) {

            }
        });
    }
}