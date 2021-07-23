package com.e.skychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.adapter.CreateGroupAdapter;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupActivity extends AppCompatActivity {
    Toolbar toolBar;
    RecyclerView rv;
    Button btnNext;
    DatabaseReference contactReference,userReference,groupReference;
    StorageReference storageReference;
    String currentUserId;
    CreateGroupAdapter adapter;
    CircleImageView civGroupIcon;
    Uri imageUri;
    ArrayList<User>memberList;
    User currentUserDetails;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initComponent();

        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("Group Icon");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");

        getCurrentUserDetails();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberList  = adapter.getSelectedUserList();
                final AlertDialog ab = new AlertDialog.Builder(CreateGroupActivity.this).create();
                View v = LayoutInflater.from(CreateGroupActivity.this).inflate(R.layout.activity_settings,null);
                Toolbar toolBar = v.findViewById(R.id.settingToolBar);
                civGroupIcon= v.findViewById(R.id.civProfile);
                final EditText etGroupName = v.findViewById(R.id.etSettingsUsename);
                final EditText etGroupDescription = v.findViewById(R.id.etSettingsStatus);
                Button btnCreateGroup = v.findViewById(R.id.btnUpdateStatus);


                etGroupName.setHint("Group name");
                etGroupDescription.setText("");
                etGroupDescription.setHint("Group description");
                btnCreateGroup.setText("Create");
                ab.setView(v);

                civGroupIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(CreateGroupActivity.this);
                    }
                });
                btnCreateGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(imageUri !=null){
                            final String groupName = etGroupName.getText().toString();
                            if(TextUtils.isEmpty(groupName)){
                                etGroupName.setError("enter group name first");
                                return;
                            }
                            final String description = etGroupDescription.getText().toString();

                            final String groupId = groupReference.push().getKey();
                            StorageReference filePath = storageReference.child(groupId+".jpg");
                            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                  taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                      @Override
                                      public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();

                                        groupReference.child(groupId).child(currentUserId).setValue("");

                                        for (User u :memberList){
                                            groupReference.child(groupId).child("member").child(u.getUid()).setValue(u.getName());
                                            groupReference.child(groupId).child(u.getUid()).setValue("");
                                        }
                                        groupReference.child(groupId).child("member").child(currentUserId).setValue(currentUserDetails.getName());


                                        Calendar calendar = Calendar.getInstance();
                                        SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                                        String createdAt = sd.format(calendar.getTime());

                                        HashMap<String,Object> hm = new HashMap<>();
                                        hm.put("groupName",groupName);
                                        hm.put("description",description);
                                        hm.put("createdAt",createdAt);
                                        hm.put("createdBy",currentUserId);
                                        hm.put("icon",imageUrl);
                                        hm.put("groupId",groupId);
                                        groupReference.child(groupId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful()){
                                                  ab.dismiss();
                                                  Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
                                                  finish();
                                              }
                                              else
                                                  Toast.makeText(CreateGroupActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                      }
                                  });
                                }
                            });
                        }
                    }
                });
                ab.show();
            }
        });
    }

    private void getCurrentUserDetails() {
       userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()){
                  currentUserDetails = dataSnapshot.getValue(User.class);
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(contactReference.child(currentUserId),userReference,User.class)
                .build();
        adapter = new CreateGroupAdapter(options);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initComponent(){
        toolBar = findViewById(R.id.createGroupToolBar);
        rv  = findViewById(R.id.rv);
        btnNext = findViewById(R.id.btnNext);

        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                civGroupIcon.setImageURI(imageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }
    }

}
