package com.e.skychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    EditText etUsername,etStatus;
    CircleImageView civProfile;
    Button btnUpdateStatus;
    DatabaseReference rootReference;
    FirebaseAuth mAuth;
    String currentUserId;
    StorageReference storageReference;
    User user;
    Toolbar toolBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initComponent();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        rootReference = FirebaseDatabase.getInstance().getReference();

        reteriveUserStatus();

        storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");

        civProfile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               CropImage.activity()
                       .setGuidelines(CropImageView.Guidelines.ON)
                       .setAspectRatio(1,1)
                       .start(SettingsActivity.this);
           }
        });

        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etUsername.getText().toString();
                String status = etStatus.getText().toString();

                if(TextUtils.isEmpty(name)){
                    etUsername.setError("Username required");
                    return;
                }
                if(TextUtils.isEmpty(status)){
                    status = "Hi, I am using firechat";
                }

                HashMap<String,String> hm = new HashMap<>();
                hm.put("name",name);
                hm.put("status",status);
                hm.put("uid",currentUserId);
                if(user!=null) {
                    if (user.getImage() != null) {
                        hm.put("image", user.getImage());
                    }
                }
                rootReference.child("Users").child(currentUserId).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SettingsActivity.this, "Status Updated", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else{
                            String message =task.getException().toString();
                            Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void reteriveUserStatus() {
       rootReference.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               Log.e("Datasnap shot",""+dataSnapshot);
               if(dataSnapshot.exists()){
                  user =   dataSnapshot.getValue(User.class);
                  etUsername.setText(user.getName());
                  etStatus.setText(user.getStatus());
                  Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(civProfile);


               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri imageUri = result.getUri();
                StorageReference filePath = storageReference.child(currentUserId+".jpg");
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(SettingsActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageurl = uri.toString();
                                rootReference.child("Users").child(currentUserId).child("image").setValue(imageurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingsActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                                civProfile.setImageURI(imageUri);
                                            }
                                            else{
                                                String message = task.getException().toString();
                                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                    }
                                });

                            }
                        });
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }
    }

    private void initComponent(){
        etUsername = findViewById(R.id.etSettingsUsename);
        etStatus = findViewById(R.id.etSettingsStatus);
        civProfile = findViewById(R.id.civProfile);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        toolBar = findViewById(R.id.settingToolBar);
        toolBar.setTitle("Profile setting");
        toolBar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
