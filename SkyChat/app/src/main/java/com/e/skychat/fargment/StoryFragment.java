package com.e.skychat.fargment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.e.skychat.R;
import com.e.skychat.SendStoryActivity;
import com.e.skychat.ViewStoryActivity;
import com.e.skychat.adapter.FriendStoryListAdapter;
import com.e.skychat.beans.Stories;
import com.e.skychat.beans.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryFragment extends Fragment {
    FloatingActionButton btnCreateStory;
    CircleImageView civProfile;
    TextView tvText;
    RelativeLayout rl;
    String currentUserId;
    User currentUserDetails;
    DatabaseReference storiesReference,contactReference,userReference;
    ArrayList<Stories> storiesList;
    ArrayList<User> userList;
    RecyclerView rv;
    FriendStoryListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_story,null);
        btnCreateStory = v.findViewById(R.id.btnCreateStory);
        civProfile = v.findViewById(R.id.civProfile);
        tvText = v.findViewById(R.id.tvText);
        rl = v.findViewById(R.id.rl);
        rv = v.findViewById(R.id.rv);


        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storiesReference = FirebaseDatabase.getInstance().getReference("Stories");
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        userReference = FirebaseDatabase.getInstance().getReference("Users");

        getCurrentUserDetails();
        checkStories();



        btnCreateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(getContext(),StoryFragment.this);
            }
        });
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(tvText.getText().toString().equalsIgnoreCase("View your story")){
                   viewStory(currentUserId);
               }
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        userList = new ArrayList<User>();

        adapter = new FriendStoryListAdapter(getContext(),userList);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setOnItemClickListener(new FriendStoryListAdapter.OnRecyclerViewClick() {
            @Override
            public void onItemClick(User user, int postion) {
                 viewStory(user.getUid());
            }
        });
        getFriendStoryList();
    }

    private void getFriendStoryList() {
      contactReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();

                while(itr.hasNext()){
                    DataSnapshot ds = itr.next();
                    final String userId = ds.getKey();
                    storiesReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                userReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if(dataSnapshot.exists()){
                                           User user = dataSnapshot.getValue(User.class);
                                           userList.add(user);
                                           adapter.notifyDataSetChanged();
                                       }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }

    private void viewStory(String id) {
     storiesReference.child(id).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.exists()){
                 storiesList = new ArrayList<>();
                 Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                 while(itr.hasNext()){
                     DataSnapshot ds = itr.next();
                     Stories story = ds.getValue(Stories.class);
                     storiesList.add(story);
                 }
                 Intent in = new Intent(getContext(), ViewStoryActivity.class);
                 in.putExtra("storyList",storiesList);
                 getContext().startActivity(in);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
      });
    }

    private void getCurrentUserDetails() {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentUserDetails = dataSnapshot.getValue(User.class);
                    Picasso.get().load(currentUserDetails.getImage()).placeholder(R.drawable.firechaticon).into(civProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkStories() {

       storiesReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
                   tvText.setText("View your story");
               else
                   tvText.setText("Create your story");
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == getActivity().RESULT_OK){
                Uri imageuri = result.getUri();
                Intent in = new Intent(getContext(), SendStoryActivity.class);
                in.putExtra("imageUri",""+imageuri);
                getContext().startActivity(in);
            }
        }
    }
}
