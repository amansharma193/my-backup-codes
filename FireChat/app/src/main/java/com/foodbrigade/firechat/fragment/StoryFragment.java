package com.foodbrigade.firechat.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Activity_create_story;
import com.foodbrigade.firechat.Adapter.FriendsAdapter;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.Setting_Activity;
import com.foodbrigade.firechat.ShowStory;
import com.foodbrigade.firechat.User;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryFragment extends Fragment {
    FloatingActionButton btncreate;
    TextView tv;
    CircleImageView civ;
    String userkey;
    RecyclerView rv;
    DatabaseReference rootReference;
    User curUser;
    FriendsAdapter adapter;
    FirebaseRecyclerOptions<User>options;
    RelativeLayout rl;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v=inflater.inflate(R.layout.fragment_story,container,false);
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference= FirebaseDatabase.getInstance().getReference();
        rootReference.child("Users").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    curUser=snapshot.getValue(User.class);
                    civ=v.findViewById(R.id.civmypic);
                    if (curUser!=null)
                        Picasso.get().load(curUser.getImage()).into(civ);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btncreate=v.findViewById(R.id.addstory);
        tv=v.findViewById(R.id.tv);
        rootReference.child("Stories").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tv.setText("See your story");
                    rl=v.findViewById(R.id.rl);
                    rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in=new Intent(getActivity(), ShowStory.class);
                            in.putExtra("user",userkey);
                            getContext().startActivity(in);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BottomSheetDialogFragment sheet = new StoryBottomSheet(userkey, getContext());
                    sheet.show(getActivity().getSupportFragmentManager(), "tag");
                }
                catch (Exception e){
                    Log.e("exception","   "+e.getMessage());
                }
          }
        });
        rv=v.findViewById(R.id.rv);
        options=new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(rootReference.child("Contacts").child(userkey),rootReference.child("Users"), User.class)
                .build();
        adapter=new FriendsAdapter(options,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.startListening();
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode ==getActivity().RESULT_OK) {
                final Uri imguri = result.getUri();
                Intent in=new Intent(getActivity(), Activity_create_story.class);
                in.putExtra("uri",""+imguri);
                getContext().startActivity(in);
            }
        }
    }
}
