package com.foodbrigade.firechat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class Setting_Activity extends AppCompatActivity {
    EditText etusername,etstatus;
    Button btnupdate;
    CircleImageView civ;
    DatabaseReference rootreference;
    FirebaseAuth mAuth;
    String userkey;
    StorageReference storageReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        init();
        etstatus.setText("hi, i am using firebase");
        mAuth=FirebaseAuth.getInstance();
        userkey=mAuth.getCurrentUser().getUid();
        rootreference= FirebaseDatabase.getInstance().getReference();
        retrieveUserData();
        storageReference= FirebaseStorage.getInstance().getReference().child("Profile Images");
        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(Setting_Activity.this);
            }
        });
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etusername.getText().toString();
                final String status = etstatus.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    etusername.setError("this field can't be empty");
                    return;
                }
                if (TextUtils.isEmpty(status)) {
                    etstatus.setError("this field can't be empty");
                    return;
                }
                rootreference.child("Users").child(userkey).child("username").setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Setting_Activity.this, "profile updated", Toast.LENGTH_SHORT).show();
                            rootreference.child("Users").child(userkey).child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        rootreference.child("Users").child(userkey).child("key").setValue(userkey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(Setting_Activity.this, "profile updated", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(Setting_Activity.this, "updated failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                            sendUserToMainActivity();
                        }
                        else{
                            Toast.makeText(Setting_Activity.this, "update failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void retrieveUserData() {
        rootreference.child("Users").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(Setting_Activity.this, "here is the data", Toast.LENGTH_SHORT).show();
                    etusername.setText(dataSnapshot.child("username").getValue(String.class));
                    etstatus.setText(dataSnapshot.child("status").getValue(String.class));
                    Picasso.get().load(dataSnapshot.child("image").getValue(String.class)).placeholder(R.drawable.radio).into(civ);
                }
                else{
                    Toast.makeText(Setting_Activity.this, "no data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToMainActivity() {
        Intent Logintent=new Intent(Setting_Activity.this,MainActivity.class);
        Logintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Logintent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null ){
            final CustomProgressDialogue object=new CustomProgressDialogue(Setting_Activity.this,"Uploading...");
            object.show();
//           final AlertDialog pd=new SpotsDialog(Setting_Activity.this,R.style.Custom);
//            pd.show();
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                 final Uri imguri=result.getUri();
                StorageReference filepath=storageReference.child(userkey+".jpg");
                filepath.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imgurl=uri.toString();
                                rootreference.child("Users").child(userkey).child("image").setValue(imgurl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(Setting_Activity.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                            civ.setImageURI(imguri);
                                        }
//                                        pd.dismiss();
                                        object.dismiss();

                            }
                        });
                            }
                        });
                    }
                });
            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void init() {
        etusername=findViewById(R.id.username);
        etstatus=findViewById(R.id.status);
        btnupdate=findViewById(R.id.btnupdate);
        civ=findViewById(R.id.civ);

    }
}
