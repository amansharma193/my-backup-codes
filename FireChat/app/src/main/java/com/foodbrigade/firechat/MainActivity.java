package com.foodbrigade.firechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.foodbrigade.firechat.Adapter.TabAccessorAdapter;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabAccessorAdapter adapter;
    FirebaseAuth mAuth;
    String user;
    FirebaseUser us;
    DatabaseReference userReference,rootReference;
    CircleImageView civ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 23);
        }
        setContentView(R.layout.activity_main);
        init();
        mAuth=FirebaseAuth.getInstance();
        us=mAuth.getCurrentUser();
        userReference=FirebaseDatabase.getInstance().getReference().child("Users");
        rootReference=FirebaseDatabase.getInstance().getReference();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (us==null){
            sendUserToLogin();
        }  else{
            user=mAuth.getCurrentUser().getUid();
            updateToken();
            UpdateUserOnlineState("online");
            checkUserStatus();
            statusdelete(user);
            getfriends(user);
        }

    }

    private void updateToken() {
        String token= FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object>hm=new HashMap<>();
        hm.put("token",token);
        userReference.child(user).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getfriends(String key){
        rootReference.child("Contacts").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Iterator<DataSnapshot>itr= snapshot.getChildren().iterator();
                    while(itr.hasNext()){
                        DataSnapshot ds=itr.next();
                        statusdelete(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void statusdelete(final String key) {
        rootReference.child("Stories").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Iterator<DataSnapshot>itr= snapshot.getChildren().iterator();
                    while (itr.hasNext()){
                        DataSnapshot ds=itr.next();
                        String storyid = ds.getKey();
                        Story story=ds.getValue(Story.class);
                        Calendar calendar=Calendar.getInstance();
                        long curtime=calendar.getTimeInMillis();
                        long uptime=story.getTimestamp();
                        if ((curtime-uptime)>=86400000){
                            rootReference.child("Stories").child(key).child(storyid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (!task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (us!=null){
            UpdateUserOnlineState("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (us!=null){
            UpdateUserOnlineState("offline");
        }
    }

    private void checkUserStatus() {
        userReference.child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("username").exists()){
                    if (dataSnapshot.child("image").exists()){
                        Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.radio).into(civ);
                    }
                    civ.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder ab=new AlertDialog.Builder(MainActivity.this);
                            View v= LayoutInflater.from(MainActivity.this).inflate(R.layout.profile_picture,null);
                            ab.setView(v);
                            ImageView iv=v.findViewById(R.id.iv);
                            iv.requestLayout();
                            iv.getLayoutParams().height=500;
                            iv.getLayoutParams().width=500;
                            iv.setScaleType(ImageView.ScaleType.FIT_XY);
                            Picasso.get().load(dataSnapshot.child("image").getValue().toString()).placeholder(R.drawable.radio).into(iv);
                            ab.show();
                        }
                    });
                }
                else  {
                    Intent intent=new Intent(MainActivity.this,Setting_Activity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Cancelled.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void UpdateUserOnlineState(String state){
        if (us!=null) {
            String userkey = us.getUid();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
            String date = sd.format(calendar.getTime());
            sd = new SimpleDateFormat("hh:mm a");
            String time = sd.format(calendar.getTime());
            HashMap<String, Object> hm = new HashMap<>();
            hm.put("date", date);
            hm.put("time", time);
            hm.put("state", state);
            userReference.child(userkey).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Find Friend");
        menu.add("Create Group");
        menu.add("Settings");
        menu.add("Log Out");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title=item.getTitle().toString();
        if (title.equalsIgnoreCase("Find Friend")){
            Intent intent=new Intent(MainActivity.this,FindFriendActivity.class);
            startActivity(intent);
        }
        else if (title.equals("Create Group")){
            Intent in=new Intent(MainActivity.this,CreateGroupActivity.class);
            startActivity(in);
        }
        else if (title.equals("Settings")){
            Intent intent=new Intent(MainActivity.this,Setting_Activity.class);
            startActivity(intent);
        }
        else if (title.equals("Log Out")){
            UpdateUserOnlineState("offline");
            mAuth.signOut();
            sendUserToLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        adapter=new TabAccessorAdapter(getSupportFragmentManager(),1);
        civ=findViewById(R.id.civ_profile);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void sendUserToLogin() {
        Intent Logintent=new Intent(MainActivity.this,LoginActivity.class);
        Logintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Logintent);
        finish();
    }
}