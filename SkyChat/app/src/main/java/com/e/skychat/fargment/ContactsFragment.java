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
import com.e.skychat.adapter.ContactAdapter;
import com.e.skychat.beans.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContactsFragment extends Fragment {
    RecyclerView rv;
    String currentUserId;
    DatabaseReference contactReference,userReference;
    FirebaseRecyclerAdapter<User, ContactAdapter.ContactViewHolder>adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts,null);
        rv = v.findViewById(R.id.rv);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(contactReference.child(currentUserId),userReference,User.class)
                .build();
        adapter = new ContactAdapter(options,getContext());
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
