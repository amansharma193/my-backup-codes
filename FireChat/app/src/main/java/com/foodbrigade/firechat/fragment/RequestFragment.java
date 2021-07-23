package com.foodbrigade.firechat.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.FindFriendAdapter;
import com.foodbrigade.firechat.Adapter.FriendRequestAdapter;
import com.foodbrigade.firechat.FindFriendActivity;
import com.foodbrigade.firechat.MainActivity;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RequestFragment extends Fragment {
    DatabaseReference userReference;
    String userkey,sender;
    FirebaseAuth mAuth;
    FirebaseRecyclerAdapter<User,FriendRequestAdapter.FriendRequestViewHolder> adapter;
    RecyclerView.LayoutManager manager;
    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.request, container,false);
        rv = v.findViewById(R.id.recrequest);
        mAuth = FirebaseAuth.getInstance();
        userkey = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference();
        return v;
    }
    @Override
   public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options=new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(userReference.child("Chat Request").child(userkey).orderByChild("Request Type").equalTo("Received"),User.class)
                .build();
        adapter=new FriendRequestAdapter(options,getContext());
        rv.setAdapter(adapter);
        manager=new LinearLayoutManager(getContext());
        rv.setLayoutManager(manager);
        adapter.startListening();
    }

    @Override
   public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
