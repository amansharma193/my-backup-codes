package com.foodbrigade.firechat.fragment;

import android.content.Intent;
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
import com.foodbrigade.firechat.Adapter.ShowGroupAdapter;
import com.foodbrigade.firechat.Group;
import com.foodbrigade.firechat.GroupChatActivity;
import com.foodbrigade.firechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupFragment extends Fragment {
    RecyclerView rv;
    String userkey;
    DatabaseReference rootReference;
    ShowGroupAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.group,container,false);
        rv=v.findViewById(R.id.rvgroup);
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference=FirebaseDatabase.getInstance().getReference();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Group>option=new FirebaseRecyclerOptions.Builder<Group>()
                .setQuery(rootReference.child("Group").orderByChild(userkey).equalTo(""),Group.class)
                .build();
        adapter=new ShowGroupAdapter(option);
        adapter.setOnClickListener(new ShowGroupAdapter.OnRecyclerViewClick() {
            @Override
            public void onItemClick(Group group, int position) {
                Intent in=new Intent(getContext(), GroupChatActivity.class);
                in.putExtra("group",group);
                getContext().startActivity(in);
            }
        });
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
