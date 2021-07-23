package com.foodbrigade.firechat.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.ChatAdapter;
import com.foodbrigade.firechat.Adapter.ContactAdapter;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatFragment extends Fragment {
    RecyclerView rv;
    String userkey;
    DatabaseReference rootReference;
    ChatAdapter adapter;
    FirebaseRecyclerOptions<User> options;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.chat,container,false);
        init(v);
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        options=new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(rootReference.child("Contacts").child(userkey),rootReference.child("Users"),User.class)
                .build();
        adapter= new ChatAdapter(options,getContext());
        rv.setAdapter(adapter);
//        adapter.setOnItemClick(new ChatAdapter.OnRecyclerViewClick() {
//            @Override
//            public void onItemClick(User user, int position) {
//
//            }
//        });
        adapter.startListening();
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init(View v) {
        rv=v.findViewById(R.id.rvchat);
        rootReference= FirebaseDatabase.getInstance().getReference();
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
