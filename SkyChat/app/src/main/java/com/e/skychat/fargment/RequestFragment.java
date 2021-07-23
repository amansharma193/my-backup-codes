package com.e.skychat.fargment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.R;
import com.e.skychat.adapter.ChatRequestAdapter;
import com.e.skychat.beans.ChatRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestFragment extends Fragment {
    RecyclerView rv;
    String currentUserId;
    DatabaseReference chatRequestReference;
    FirebaseRecyclerAdapter<ChatRequest, ChatRequestAdapter.ChatRequestViewHolder>adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request,null);
        rv = v.findViewById(R.id.rvRequestFragment);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRequestReference = FirebaseDatabase.getInstance().getReference().child("ChatRequest");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<ChatRequest>options = new FirebaseRecyclerOptions.Builder<ChatRequest>()
        .setQuery(chatRequestReference.child(currentUserId).orderByChild("request_type").equalTo("received"),ChatRequest.class)
        .build();
        adapter = new ChatRequestAdapter(options,getContext());
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
