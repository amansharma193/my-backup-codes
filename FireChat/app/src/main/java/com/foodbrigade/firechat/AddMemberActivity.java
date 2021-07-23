package com.foodbrigade.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.AddMemberAdapter;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AddMemberActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView rv;
    FirebaseRecyclerOptions<User>options;
    DatabaseReference rootReference;
    String userkey,groupkey;
    Button btnadd;
    AddMemberAdapter adapter;
    ArrayList<User>al;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member_activity);
        init();
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference= FirebaseDatabase.getInstance().getReference();
        Intent intent=getIntent();
        groupkey=intent.getStringExtra("group");
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomProgressDialogue pd=new CustomProgressDialogue(AddMemberActivity.this,"Adding...");
                pd.show();
                al=adapter.getSelectedUserList();
                if (al.size()!=0) {
                    final HashMap<String, Object> hm = new HashMap<>();
                    for (int i = 0; i < al.size(); i++) {
                        hm.put(al.get(i).getKey(), "");
                    }
                    rootReference.child("Group").child(groupkey).updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            rootReference.child("Group").child(groupkey).child("members").updateChildren(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    if (task.isSuccessful()) {
                                        finish();
                                    } else {
                                        Toast.makeText(AddMemberActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        options=new FirebaseRecyclerOptions.Builder<User>()
                .setIndexedQuery(rootReference.child("Contacts").child(userkey),rootReference.child("Users"),User.class)
                .build();
        adapter=new AddMemberAdapter(options,AddMemberActivity.this,groupkey);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(AddMemberActivity.this));
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init() {
        toolbar=findViewById(R.id.toolbar);
        rv=findViewById(R.id.rv);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnadd=findViewById(R.id.btnAdd);
    }

}
