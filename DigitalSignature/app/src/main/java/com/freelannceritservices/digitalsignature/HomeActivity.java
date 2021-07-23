package com.freelannceritservices.digitalsignature;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import org.apache.commons.io.FileUtils;
import org.json.*;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.freelannceritservices.digitalsignature.databinding.ActivityHomeBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    ActivityHomeBinding binding;
    TabAccessorAdapter adapter;
    String img_url;
    ArrayList<Family>al;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityHomeBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        adapter=new TabAccessorAdapter(getSupportFragmentManager(),1);
        binding.viewpager.setAdapter(adapter);
        binding.tablayout.setupWithViewPager(binding.viewpager);
        setSupportActionBar(binding.toolbar);
        binding.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(HomeActivity.this, binding.options);
                Menu menu=popup.getMenu();
                menu.add("Export");
                menu.add("Log Out");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title=item.getTitle().toString();
                        if (title.equalsIgnoreCase("export")){
                            al=new ArrayList<>();
                            FirebaseFirestore.getInstance().collection("Public").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        Log.e("spanshoe","task");
                                        JSONArray jsonArray=new JSONArray();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            jsonArray.put(document.toObject(Family.class).getJSONObject());
                                        }
                                        try {

                                            File file=new File(getFilesDir(),"JSON.csv");
                                            Log.e("spanhsoe",jsonArray.length()+".............");
                                            String csv = CDL.toString(jsonArray);
                                            FileUtils.writeStringToFile(file, csv);
                                            URI u=file.toURI();
                                            Uri uri=Uri.parse(u.toString());
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                            String key = FirebaseFirestore.getInstance().collection("Public").document().getId();
                                            StorageReference filepath = storageReference.child("JSON.csv");
                                            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            img_url = uri.toString();
                                                            Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(img_url));
                                                            startActivity(in);
                                                            Log.e("spanshoe","............"+img_url);
                                                        }
                                                    });
                                                }
                                            });
                                            Toast.makeText(HomeActivity.this, "Export", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Log.e("spanshoe","......."+e.getMessage());
                                        }
                                    }else
                                        Toast.makeText(HomeActivity.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if (title.equalsIgnoreCase("Log Out")){
                            SharedPreferences mPrefs = getSharedPreferences("Digital", MODE_PRIVATE);
                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                            prefsEditor.putString("user", "");
                            prefsEditor.commit();
                            Intent intent=new Intent(HomeActivity.this,Login.class);
                            startActivity(intent);
                            finish();
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }
}