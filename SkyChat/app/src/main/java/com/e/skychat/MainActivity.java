package com.e.skychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.e.skychat.adapter.TabAccessorAdapter;
import com.e.skychat.beans.Stories;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    Toolbar toolBar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tvTitle;
    TabAccessorAdapter adapter;
    FirebaseAuth mAuth;
    FirebaseUser  currentUser;
    DatabaseReference usersReference,contactReference,storiesReference;
    String currentUserId;
    CircleImageView civCurrentUser;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponent();
        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference("Users");

        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        storiesReference = FirebaseDatabase.getInstance().getReference("Stories");
        sp = getSharedPreferences("device_token",MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
           sendUserToLoginActivity(); 
        }
        else{
            updateToken();
            updateUserOnlineState("online");
            checkUserStatus();
            getFriendsID();
            delete24hoursOldStory(currentUser.getUid());
        }
    }

    private void updateToken(){
        String currentUserId = currentUser.getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        HashMap<String,Object> hm = new HashMap<>();
        hm.put("token",token);
        usersReference.child(currentUserId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(!task.isSuccessful())
                   Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getFriendsID() {
       String currentUserId = currentUser.getUid();
       contactReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()){
                  Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                  while(itr.hasNext()){
                      DataSnapshot ds = itr.next();
                      String friendId = ds.getKey();
                      delete24hoursOldStory(friendId);
                  }
              }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    private void delete24hoursOldStory(final String userId) {
      storiesReference.child(userId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if(dataSnapshot.exists()){
                  Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                  while(itr.hasNext()){
                      DataSnapshot ds = itr.next();
                      Stories story = ds.getValue(Stories.class);

                      long stime = story.getTimestamp();

                      Calendar calendar = Calendar.getInstance();
                      long ctime = calendar.getTimeInMillis();

                      if((ctime - stime) >= 86400000){
                         storiesReference.child(userId).child(story.getStoryId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                             }
                         });
                      }
                  }
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentUser!=null){
            updateUserOnlineState("offline");
        }
    }

    private void checkUserStatus() {
      currentUserId = currentUser.getUid();
      usersReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.child("name").exists()){
                 tvTitle.setText(dataSnapshot.child("name").getValue().toString());
                 Animation anim = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate_demo);
                 tvTitle.setAnimation(anim);
                 if(dataSnapshot.child("image").exists()){
                     String imageUrl = dataSnapshot.child("image").getValue(String.class);
                     Picasso.get().load(imageUrl).into(civCurrentUser);
                     Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate_demo);
                     civCurrentUser.setAnimation(animation);
                 }
             }
             else{
                 sendUserToSettingsActivity();
             }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Find friend");
        menu.add("Create group");
        menu.add("Settings");
        menu.add("Logout");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        if(title.equals("Find friend")){
          Intent in = new Intent(MainActivity.this,FindFriendActivity.class);
          startActivity(in);
        }
        else if(title.equals("Create group")){
          Intent in = new Intent(MainActivity.this,CreateGroupActivity.class);
          startActivity(in);
        }
        else if(title.equals("Settings")){
           sendUserToSettingsActivity();
        }
        else if(title.equals("Logout")){
           updateUserOnlineState("offline");
           mAuth.signOut();
           sendUserToLoginActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendUserToSettingsActivity() {
       Intent settingActivityIntent = new Intent(MainActivity.this,SettingsActivity.class);
       startActivity(settingActivityIntent);
    }

    private void initComponent(){
        toolBar = findViewById(R.id.mainToolBar);
        //toolBar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolBar);
        //getSupportActionBar().setTitle("Firechat");
        tvTitle = findViewById(R.id.tvTitle);
        civCurrentUser = findViewById(R.id.civCurrentUser);

        tabLayout = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewPager);

        adapter = new TabAccessorAdapter(getSupportFragmentManager(),1);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);

        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(loginIntent);

        finish();
    }

    public void updateUserOnlineState(String state){
       FirebaseUser currentUser =   FirebaseAuth.getInstance().getCurrentUser();
       if(currentUser != null){
           String currentUserId = currentUser.getUid();

           Calendar calendar = Calendar.getInstance();
           SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyy");
           String date = sd.format(calendar.getTime());

           sd = new SimpleDateFormat("hh:mm a");
           String time = sd.format(calendar.getTime());

           HashMap<String,Object> hm = new HashMap<String,Object>();
           hm.put("date",date);
           hm.put("time",time);
           hm.put("state",state);

           DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
           usersReference.child(currentUserId).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                  if(!task.isSuccessful()){
                      String errorMessage = task.getException().toString();
                      Log.e("Error","=>"+errorMessage);
                  }
               }
           });
       }
    }
}