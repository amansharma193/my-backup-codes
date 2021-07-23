package com.transportervendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.apis.VehicleService;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.Vehicle;
import com.transportervendor.databinding.AddVehicleActivityBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.transportervendor.apis.VehicleService.vehicleApi;

public class EditVehicle extends AppCompatActivity {
    AddVehicleActivityBinding binding;
    Uri imgUri;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=AddVehicleActivityBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        setSupportActionBar(binding.tbToolBar);
        getSupportActionBar().setTitle("Edit Vehicle");
        Intent in=getIntent();
        VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
        Call<ArrayList<Object>>call=vehicleApi.getCategory();
        call.enqueue(new Callback<ArrayList<Object>>() {
            @Override
            public void onResponse(Call<ArrayList<Object>> call, Response<ArrayList<Object>> response) {
                if(response.isSuccessful()){
                    ArrayList al=response.body();
                    al= (ArrayList) al.get(0);
                    Collections.sort(al);
                    al.add(0,"Select Vehicle");
                    final ArrayAdapter<String> ad = new ArrayAdapter<String>(EditVehicle.this,android.R.layout.simple_spinner_item,(ArrayList<String>)al);
                    ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.sp.setAdapter(ad);
                }else{
                    Toast.makeText(EditVehicle.this, ""+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Object>> call, Throwable t) {

            }
        });
        final Vehicle vehicle= (Vehicle) in.getSerializableExtra("vehicle");
        if(NetworkUtil.getConnectivityStatus(this)) {
            Picasso.get().load(vehicle.getImageUrl()).placeholder(R.drawable.transporter_logo).into(binding.ivVehicleImage);
        }else
            Toast.makeText(this, "please enable internet connection", Toast.LENGTH_SHORT).show();
        binding.etcount.setText(vehicle.getCount());
        binding.btnDone.setText("Update");
        binding.ivBackErroe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.ivVehicleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgUri!=null){
                    File file = FileUtils.getFile(EditVehicle.this, imgUri);
                    RequestBody requestFile =
                            RequestBody.create(
                                    MediaType.parse(getContentResolver().getType(imgUri)),
                                    file
                            );
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                    VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
                    RequestBody id = RequestBody.create(okhttp3.MultipartBody.FORM, vehicle.getVehicleId());
                    RequestBody transporterId = RequestBody.create(okhttp3.MultipartBody.FORM, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    Call<Transporter>call=vehicleApi.updateImage(id,transporterId,body);
                    if(NetworkUtil.getConnectivityStatus(EditVehicle.this)) {
                        String s="Please wait...";
                        if (checkLanguage()){
                            s="कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd=new CustomProgressDialog(EditVehicle.this,s);
                        pd.show();
                        call.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    Transporter t = response.body();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Toast.makeText(EditVehicle.this, "image updated", Toast.LENGTH_SHORT).show();
                                    imgUri=null;
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(EditVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(EditVehicle.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    String etcount=binding.etcount.getText().toString();
                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                    String json=mPrefs.getString("Transporter","");
                    Gson gson = new Gson();
                    Transporter transporter=gson.fromJson(json,Transporter.class);
                    ArrayList<Vehicle>al=transporter.getVehicleList();
                    for(int i=0;i<al.size();i++){
                        Vehicle ve=al.get(i);
                        if(vehicle.getVehicleId().equals(ve.getVehicleId())){
                            ve.setName((String) binding.sp.getSelectedItem());
                            ve.setCount(etcount);
                            al.set(i,ve);
                            break;
                        }
                    }
                    transporter.setVehicleList(al);
                    VehicleService.VehicleApi vehicleApi = VehicleService.getVehicleApiInstance();
                    Call<Transporter> cal = vehicleApi.updateVehicle(transporter);
                    if(NetworkUtil.getConnectivityStatus(EditVehicle.this)) {
                        String s="Please wait...";
                        if (checkLanguage()){
                            s="कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd=new CustomProgressDialog(EditVehicle.this,s);
                        pd.show();
                        cal.enqueue(new Callback<Transporter>() {
                            @Override
                            public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                pd.dismiss();
                                if (response.isSuccessful()) {
                                    Transporter t = response.body();
                                    SharedPreferences mPrefs = getSharedPreferences("Transporter", MODE_PRIVATE);
                                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(t);
                                    prefsEditor.putString("Transporter", json);
                                    prefsEditor.commit();
                                    Toast.makeText(EditVehicle.this, "updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<Transporter> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(EditVehicle.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                        Toast.makeText(EditVehicle.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==111 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imgUri=data.getData();
            binding.ivVehicleImage.setImageURI(imgUri);
        }
    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}
