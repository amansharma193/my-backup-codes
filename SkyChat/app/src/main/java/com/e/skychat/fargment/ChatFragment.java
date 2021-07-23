package com.e.skychat.fargment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.ChatMessageActivity;
import com.e.skychat.R;
import com.e.skychat.adapter.ChatAdapter;
import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatFragment extends Fragment {
    String currentUserId;
    DatabaseReference contactReference,userReference,messageCounterReference;
    RecyclerView rv;
    ChatAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat,null);
        rv = v.findViewById(R.id.rv);
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        messageCounterReference = FirebaseDatabase.getInstance().getReference("MessageCounter");

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(contactReference.child(currentUserId),userReference,User.class)
                .build();
        adapter = new ChatAdapter(options,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter.setOnItemClick(new ChatAdapter.OnRecyclerViewClick() {
            @Override
            public void onItemClick(User user, int position) {
                messageCounterReference.child(currentUserId).child(user.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(!task.isSuccessful())
                           Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                Intent in = new Intent(getContext(), ChatMessageActivity.class);
                in.putExtra("friend",user);
                startActivity(in);
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();


        adapter.startListening();

       
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
