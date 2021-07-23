package com.e.skychat.fargment;

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

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.GroupChatActivity;
import com.e.skychat.R;
import com.e.skychat.adapter.ShowGroupAdapter;
import com.e.skychat.beans.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GroupFragment extends Fragment {
    RecyclerView rv;
    String currentUserId;
    DatabaseReference groupReference;
    ShowGroupAdapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group, null);
        rv = v.findViewById(R.id.rv);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Group>option = new FirebaseRecyclerOptions.Builder<Group>()
                .setQuery(groupReference.orderByChild(currentUserId).equalTo(""),Group.class)
                .build();
       adapter = new ShowGroupAdapter(option);

       adapter.setOnItemClick(new ShowGroupAdapter.OnRecyclerViewClick() {
           @Override
           public void onItemClick(Group group, int position) {
              Intent in = new Intent(getContext(), GroupChatActivity.class);
              in.putExtra("group",group);
              startActivity(in);
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
