package com.foodbrigade.firechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.MemberMessagaAdapter;
import com.foodbrigade.firechat.Adapter.ShowMemberAdapter;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringJoiner;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberInfoActivity extends AppCompatActivity {
    RecyclerView rv;
    Button btndelete;
    Toolbar toolbar;
    FloatingActionButton fab;
    String groupkey,userKey;
    DatabaseReference rootReference;
    CircleImageView civ;
    Uri imguri;
    StorageReference storageReference;
    EditText tvname,etdesc;
    Group group;
    public ArrayList<String>al;
    ImageView ivgroupicon;
    RecyclerView.Adapter<ShowMemberAdapter.ShowMemberViewHolder>adapter;
    CollapsingToolbarLayout toolbarLayout;
    FloatingActionButton btnadd,btnexit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_member);
        userKey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rv=findViewById(R.id.rv);
        fab=findViewById(R.id.fab);
        storageReference= FirebaseStorage.getInstance().getReference("Group Icon");
        Intent in=getIntent();
        final Group grup= (Group) in.getSerializableExtra("groupkey");
        groupkey=grup.getGroupid();
        btnadd=findViewById(R.id.btnadd);
        btnexit=findViewById(R.id.btnexit);
        toolbar=findViewById(R.id.toolbar);
        toolbarLayout=findViewById(R.id.collpsingtoolbar);
        setSupportActionBar(toolbar);
        btndelete=findViewById(R.id.btndelete);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootReference= FirebaseDatabase.getInstance().getReference();
        ivgroupicon=findViewById(R.id.ivgroup_icon);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab= new AlertDialog.Builder(MemberInfoActivity.this);
                View v= LayoutInflater.from(MemberInfoActivity.this).inflate(R.layout.group_info_dialog ,null);
                ab.setView(v);
                Button btnupdate=v.findViewById(R.id.btnupdate);
                btnupdate.setText("Update");
                tvname=v.findViewById(R.id.username);
                tvname.setText(grup.getName());
                etdesc=v.findViewById(R.id.status);
                etdesc.setText(grup.getDescription());
                civ=v.findViewById(R.id.civ);
                Picasso.get().load(grup.getIcon()).placeholder(R.drawable.radio).into(civ);
                civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(MemberInfoActivity.this);
                    }
                });
                btnupdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String name = tvname.getText().toString();
                        final String status = etdesc.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            tvname.setError("this field is mandatory");
                            return;
                        }
                        if (TextUtils.isEmpty(status)) {
                            etdesc.setError("this field is mandatory");
                            return;
                        }
                        if (imguri != null){
                            final CustomProgressDialogue obj=new CustomProgressDialogue(MemberInfoActivity.this,"Updating...");
                            obj.show();
                            StorageReference filepath = storageReference.child(group.getGroupid() + ".jpg");
                            filepath.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageurl = uri.toString();
                                        HashMap<String,Object>hm=new HashMap<>();
                                        hm.put("icon",imageurl);
                                        hm.put("name",name);
                                        hm.put("description",status);
                                        rootReference.child("Group").child(groupkey).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                obj.dismiss();
                                                finish();
                                                if (!task.isSuccessful()){
                                                    Toast.makeText(MemberInfoActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
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
        rootReference.child("Group").child(groupkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    group=snapshot.getValue(Group.class);
                    getGroupMember();
                    toolbarLayout.setTitle("");
                    toolbar.setTitle(group.getName());
                    Picasso.get().load(group.getIcon()).into(ivgroupicon);
                    btnexit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (userKey.equals(group.getCreatedBy())){
                                Intent in=new Intent(MemberInfoActivity.this,MakeAdminActivity.class);
                                al.add(0,groupkey);
                                in.putExtra("group",al);
                                startActivity(in);
                            } else{
                                rootReference.child("Group").child(groupkey).child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            rootReference.child("Group").child(groupkey).child("members").child(userKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (!task.isSuccessful()){
                                                        Toast.makeText(MemberInfoActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    finish();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (groupkey!=null){
                    AlertDialog.Builder ab=new AlertDialog.Builder(MemberInfoActivity.this);
                    ab.setTitle("Warning!");
                    ab.setMessage("Are you sure?");
                    ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            rootReference.child("Group").child(groupkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        finish();
                                        Toast.makeText(MemberInfoActivity.this, "Group Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    ab.show();
                }
            }
        });
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inent=new Intent(MemberInfoActivity.this,AddMemberActivity.class);
                inent.putExtra("group",groupkey);
                startActivity(inent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rootReference.child("Group").child(groupkey).child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupMember() {
        al=new ArrayList<>();
        rootReference.child("Group").child(group.getGroupid()).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (userKey.equals(group.getCreatedBy())){
                    new ItemTouchHelper(new MemberInfoActivity.DeleteOnSwipe(0,ItemTouchHelper.RIGHT)).attachToRecyclerView(rv);
                    btndelete.setVisibility(View.VISIBLE);
                    btnadd.setVisibility(View.VISIBLE);
                }
                if (snapshot.exists()){
                    Iterator<DataSnapshot> itr=snapshot.getChildren().iterator();
                    String name;
                    while(itr.hasNext()){
                        DataSnapshot ds=itr.next();
                        if (!userKey.equals(ds.getKey())){
                            String memeberkey=ds.getKey().toString();
                            al.add(memeberkey);
                        }
                    }
                    adapter=new ShowMemberAdapter(MemberInfoActivity.this,al,groupkey);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(MemberInfoActivity.this));
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
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null ){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                imguri=result.getUri();
                civ.setImageURI(imguri);
            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "failed.", Toast.LENGTH_SHORT).show();
            }
        }

    }
    public class DeleteOnSwipe extends ItemTouchHelper.SimpleCallback{
        public DeleteOnSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position= viewHolder.getAdapterPosition();
            final String friendKey=al.get(position);
            rootReference.child("Group").child(groupkey).child("members").child(friendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        rootReference.child("Group").child(groupkey).child(friendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    al.remove(friendKey);
                                    adapter.notifyDataSetChanged();
                                    Snackbar.make(rv,"Member removed",Snackbar.LENGTH_LONG)
                                            .setAction("Undo", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    rootReference.child("Group").child(groupkey).child("members").child(friendKey).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                rootReference.child("Group").child(groupkey).child(friendKey).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            adapter.notifyDataSetChanged();
                                                                            Snackbar.make(rv,"Member Added.",Snackbar.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }).show();
                                }
                            }
                        });
                    }
                }
            });

        }
    }
}
