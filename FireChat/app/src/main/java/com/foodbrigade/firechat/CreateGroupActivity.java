package com.foodbrigade.firechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
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
    Toolbar toolbar;
    RecyclerView rv;
    Button btnNext;
    String userkey;
    CircleImageView civ;
    DatabaseReference rootReference;
    FirebaseRecyclerOptions<User> options;
    StorageReference storageReference;
    Uri imguri;
    User currentUser;
    ArrayList<User>memberlist;
    CreateGroupAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_group_activity);
        init();
        options=new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(rootReference.child("Contacts").child(userkey),rootReference.child("Users"),User.class)
                .build();
        adapter=new CreateGroupAdapter(options);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(CreateGroupActivity.this));
        adapter.startListening();
        getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("Group Icon");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                memberlist=adapter.getSelectedUserList();
                AlertDialog.Builder ab= new AlertDialog.Builder(CreateGroupActivity.this);
                View v= LayoutInflater.from(CreateGroupActivity.this).inflate(R.layout.group_info_dialog ,null);
                ab.setView(v);
                Button btnCreate=v.findViewById(R.id.btnupdate);
                btnCreate.setText("Create Group");
                final EditText etname=v.findViewById(R.id.username);
                etname.setHint("Group Name");
                final EditText etstatus=v.findViewById(R.id.status);
                etstatus.setHint("Group Description");
                civ=v.findViewById(R.id.civ);
                civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(CreateGroupActivity.this);
                    }
                });
                btnCreate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (imguri!=null){
                            final String name=etname.getText().toString();
                            final String status=etstatus.getText().toString();
                            if (TextUtils.isEmpty(name)){
                                etname.setError("this field is mandatory");
                                return;
                            }
                            if (TextUtils.isEmpty(status)){
                                etstatus.setError("this field is mandatory");
                                return;
                            }
                            final CustomProgressDialogue pd=new CustomProgressDialogue(CreateGroupActivity.this,"Creating...");
                            pd.show();
                            final String groupid=rootReference.child("Group").push().getKey();
                            StorageReference filepath=storageReference.child(groupid+".jpg");
                            filepath.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageurl=uri.toString();
                                            rootReference.child("Group").child(groupid).child(userkey).setValue("");
                                            rootReference.child("Group").child(groupid).child("members").child(userkey).setValue(currentUser.getUsername());
                                            for (User u:memberlist){
                                                rootReference.child("Group").child(groupid).child(u.getKey()).setValue("");
                                                rootReference.child("Group").child(groupid).child("members").child(u.getKey()).setValue(u.getUsername());
                                            }
                                            Calendar c=Calendar.getInstance();
                                            SimpleDateFormat sd=new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                                            String date=sd.format(c.getTime());
                                            HashMap<String,Object>hm=new HashMap<>();
                                            hm.put("name",name);
                                            hm.put("description",status);
                                            hm.put("createdAt",date);
                                            hm.put("createdBy",userkey);
                                            hm.put("icon",imageurl);
                                            hm.put("groupid",groupid);
                                            rootReference.child("Group").child(groupid).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    pd.dismiss();
                                                    if (task.isSuccessful()){
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
//                Toolbar toolbar1=v.findViewById(R.id.tool)
                ab.show();
            }
        });
    }

    private void getCurrentUser() {
        rootReference.child("Users").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    currentUser= (User) snapshot.getValue(User.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CustomProgressDialogue object=new CustomProgressDialogue(CreateGroupActivity.this,"Uploading...");
            object.show();
//           final AlertDialog pd=new SpotsDialog(Setting_Activity.this,R.style.Custom);
//            pd.show();
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                imguri=result.getUri();
                civ.setImageURI(imguri);
            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "failed.", Toast.LENGTH_SHORT).show();
            }
            object.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init() {
        toolbar=findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Select Member");
        rv=findViewById(R.id.rv1);
        btnNext=findViewById(R.id.btnnext);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            userkey=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        rootReference=FirebaseDatabase.getInstance().getReference();
    }
}
