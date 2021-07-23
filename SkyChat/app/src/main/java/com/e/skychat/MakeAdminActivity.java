package com.e.skychat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.adapter.MakeAdminAdapter;
import com.e.skychat.beans.Group;
import com.e.skychat.beans.User;
import com.e.skychat.fargment.CreateAdminBottomSheet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MakeAdminActivity extends AppCompatActivity {
    RecyclerView rv;
    Button btnExit;
    Toolbar toolBar;
    Group group;
    DatabaseReference groupReference,memberReference,userReference;
    String currentUserId;
    MakeAdminAdapter adpter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        initComponent();

        group = (Group) getIntent().getSerializableExtra("group");

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        memberReference = FirebaseDatabase.getInstance().getReference("Group").child(group.getGroupId()).child("member");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User>option = new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(memberReference,userReference,User.class)
                .build();
        adpter = new MakeAdminAdapter(option);
        adpter.setOnItemClickListener(new MakeAdminAdapter.OnRecyclerViewClick() {
            @Override
            public void onItemClick(User user, int position) {
                CreateAdminBottomSheet bottomSheet = new CreateAdminBottomSheet(user,groupReference,memberReference,group,currentUserId);
                bottomSheet.show(getSupportFragmentManager(),"");
            }
        });
        rv.setAdapter(adpter);
        rv.setLayoutManager(new LinearLayoutManager(MakeAdminActivity.this));
        adpter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adpter.stopListening();
    }

    private void initComponent(){
        rv = findViewById(R.id.rv);
        toolBar = findViewById(R.id.createGroupToolBar);
        btnExit = findViewById(R.id.btnNext);
        btnExit.setVisibility(View.GONE);
        toolBar.setTitle("Create new admin");
        toolBar.setSubtitle("Make admim before exit group");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
