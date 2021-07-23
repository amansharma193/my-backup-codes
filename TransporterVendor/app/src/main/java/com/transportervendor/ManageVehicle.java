package com.transportervendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.adapter.VehicleAdapter;
import com.transportervendor.apis.TransporterService;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.Vehicle;
import com.transportervendor.databinding.ManageVehicleActivityBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageVehicle extends AppCompatActivity {
    ManageVehicleActivityBinding binding;
    VehicleAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ManageVehicleActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Manage Vehicle");
        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(ManageVehicle.this,AddVehicleActivity.class);
                startActivityForResult(in,111);
            }
        });

    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        TransporterService.TransporterApi transporterApi=TransporterService.getTransporterApiInstance();
        Call<Transporter>call=transporterApi.getTransporter(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(NetworkUtil.getConnectivityStatus(ManageVehicle.this)) {
            String s="Please wait...";
            if (checkLanguage()){
                s="कृपया प्रतीक्षा करें...";
            }
            final CustomProgressDialog pd=new CustomProgressDialog(ManageVehicle.this,s);
            pd.show();
            call.enqueue(new Callback<Transporter>() {
                @Override
                public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        ArrayList<Vehicle> al = response.body().getVehicleList();
                        if (al == null)
                            al = new ArrayList<>();
                        adapter = new VehicleAdapter(ManageVehicle.this, al);
                        binding.rv.setAdapter(adapter);
                        binding.rv.setLayoutManager(new LinearLayoutManager(ManageVehicle.this));
                    }
                }

                @Override
                public void onFailure(Call<Transporter> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(ManageVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
        }
        binding.btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(ManageVehicle.this,HomeActivity.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && resultCode==222){
            TransporterService.TransporterApi transporterApi=TransporterService.getTransporterApiInstance();
            Call<Transporter>call1=transporterApi.getTransporter(FirebaseAuth.getInstance().getCurrentUser().getUid());
            if(NetworkUtil.getConnectivityStatus(this)) {
                final CustomProgressDialog pd=new CustomProgressDialog(ManageVehicle.this,"Please wait...");
                pd.show();
                call1.enqueue(new Callback<Transporter>() {
                    @Override
                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            ArrayList<Vehicle> al = response.body().getVehicleList();
                            adapter = new VehicleAdapter(ManageVehicle.this, al);
                            binding.rv.setAdapter(adapter);
                            binding.rv.setLayoutManager(new LinearLayoutManager(ManageVehicle.this));
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(ManageVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
