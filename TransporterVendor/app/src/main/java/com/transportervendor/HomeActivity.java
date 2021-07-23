package com.transportervendor;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.adapter.TabAccessorAdapter;
import com.transportervendor.apis.TransporterService;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.Vehicle;
import com.transportervendor.databinding.ActivityHomeBinding;
import com.transportervendor.databinding.HeaderDrawerBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;

public class HomeActivity extends AppCompatActivity {
    TabAccessorAdapter adapter;
    View view;
    ActivityHomeBinding homeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding =ActivityHomeBinding.inflate(LayoutInflater.from(HomeActivity.this));
        setContentView(homeBinding.getRoot());
        setSupportActionBar(homeBinding.toolbar);
        getSupportActionBar().setTitle("Home");
        adapter=new TabAccessorAdapter(getSupportFragmentManager(),1, getSharedPreferences("Transporter", Context.MODE_PRIVATE));
        homeBinding.drawicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeBinding.drawerlayout.openDrawer(Gravity.RIGHT);
            }
        });
        String token = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
        String json=shared.getString("Transporter","");
        Transporter transporter;
        if(json.equals("")){
            transporter=new Transporter(" "," "," "," "," "," "," "," "," "," ",new ArrayList<Vehicle>());
        }else {
             Gson gson = new Gson();
             transporter = gson.fromJson(json, Transporter.class);
        }
//        if (ActivityCompat.checkSelfPermission(HomeActivity.this, READ_PHONE_NUMBERS) ==
//                PermissionChecker.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(HomeActivity.this,
//                READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_PHONE_NUMBERS},101);
//            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_PHONE_NUMBERS},101);
//        }
        if(transporter.getTransporterId().equals(" ")){
            String user=FirebaseAuth.getInstance().getCurrentUser().getUid();
            TransporterService.TransporterApi transporterApi=TransporterService.getTransporterApiInstance();
            Call<Transporter>call=transporterApi.getTransporter(user);
            String s="Please wait...";
            if (checkLanguage()){
                s="कृपया प्रतीक्षा करें...";
            }
            final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,s);
            pd.show();
            call.enqueue(new Callback<Transporter>() {
                @Override
                public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                    pd.dismiss();
                    if(!response.isSuccessful()){
                        Intent in=new Intent(HomeActivity.this,CreateProfile.class);
                        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        in.putExtra("code",1);
                        startActivity(in);
                        finish();
                    }else if(response.code()==200){
                        Transporter t=response.body();
                        SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(t);
                        prefsEditor.putString("Transporter", json);
                        prefsEditor.commit();
                    }
                }

                @Override
                public void onFailure(Call<Transporter> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(HomeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else if(transporter.getTransporterId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            if(!(token.equals(transporter.getToken()))){
                transporter.setToken(token);
                TransporterService.TransporterApi transporterApi=TransporterService.getTransporterApiInstance();
                Call<Transporter>call=transporterApi.updateTransporter(transporter);
                String s="Please wait...";
                if (checkLanguage()){
                    s="कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd=new CustomProgressDialog(HomeActivity.this,s);
                pd.show();
                call.enqueue(new Callback<Transporter>() {
                    @Override
                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                        pd.dismiss();
                        if(response.isSuccessful()){
                            Transporter t=response.body();
                            SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(t);
                            prefsEditor.putString("Transporter", json);
                            prefsEditor.commit();
                            Toast.makeText(HomeActivity.this, "token updated", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Transporter> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(HomeActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        view=homeBinding.navigationView.inflateHeaderView(R.layout.header_drawer);
        ImageView iv=view.findViewById(R.id.btnback);
        ImageView civ=view.findViewById(R.id.logo);
        TextView name=view.findViewById(R.id.name);
        json=shared.getString("Transporter","");
        if (!json.equalsIgnoreCase("")){
            Gson gson=new Gson();
            transporter=gson.fromJson(json,Transporter.class);
            Picasso.get().load(transporter.getImage()).placeholder(R.drawable.transporter_logo).into(civ);
            name.setText(transporter.getName());
        }
        HeaderDrawerBinding header= HeaderDrawerBinding.inflate(LayoutInflater.from(HomeActivity.this));
                iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                homeBinding.drawerlayout.closeDrawer(Gravity.RIGHT);

            }
        });

        homeBinding.viewPager.setAdapter(adapter);
        homeBinding.tabLayout.setupWithViewPager(homeBinding.viewPager);
        homeBinding.navigationView.setItemIconTintList(null);
        homeBinding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if(id==R.id.profile){
                    Intent in =new Intent(HomeActivity.this,CreateProfile.class);
                    in.putExtra("code",2);
                    startActivity(in);
                }
                else if(id==R.id.vehicle){
                    Intent in =new Intent(HomeActivity.this,ManageVehicle.class);
                    startActivity(in);
                }
                else if(id==R.id.terms){
                    Intent in=new Intent(HomeActivity.this,TermsnConditions.class);
                    startActivity(in);
                }else if(id==R.id.privacy){
                    Intent in=new Intent(HomeActivity.this,PrivacyPolicy.class);
                    startActivity(in);
                }else if(id==R.id.about){
                    Intent in=new Intent(HomeActivity.this,AboutUs.class);
                    startActivity(in);
                }else if(id==R.id.contact){
                    Intent in=new Intent(HomeActivity.this,ContactUs.class);
                    startActivity(in);
                }else if (id==R.id.rating) {
                    Intent in=new Intent(HomeActivity.this, RatingsActivity.class);
                    startActivity(in);
                } else if(id==R.id.logout){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    SharedPreferences mPrefs=getSharedPreferences("Transporter",MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = mPrefs.edit();
                    prefsEditor.putString("Transporter","");
                    prefsEditor.commit();
                    finish();
                }
                else if (id==R.id.hindi){
                    SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = shared.edit();
                    prefsEditor.putString("language","hindi");
                    prefsEditor.commit();
                    Toast.makeText(HomeActivity.this, "परिवर्तनों को देखने के लिए कृपया एप्लिकेशन पुनः आरंभ करें।", Toast.LENGTH_SHORT).show();
                    triggerRebirth(HomeActivity.this);
                }
                else if (id==R.id.english){
                    SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                    SharedPreferences.Editor prefsEditor = shared.edit();
                    prefsEditor.putString("language","english");
                    prefsEditor.commit();
                    Toast.makeText(HomeActivity.this, "Please restart the application to see changes.", Toast.LENGTH_SHORT).show();
                    triggerRebirth(HomeActivity.this);
                }
                return true;
            }
        });
        if (checkLanguage()){
            getSupportActionBar().setTitle("होम");
            homeBinding.navigationView.inflateMenu(R.menu.menu_hindi);
            ((TextView)view.findViewById(R.id.menu)).setText("मेन्यू");
        }else{
            homeBinding.navigationView.inflateMenu(R.menu.navigation_menu_item);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    public  boolean checkLanguage() {
        SharedPreferences mprefs =getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
    public static void triggerRebirth(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}