package com.foodbrigade.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.Adapter.FindFriendAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class FindFriendActivity extends AppCompatActivity {
    DatabaseReference userReference;
    ArrayList<User>al;
    String userkey,name=" ";
    FirebaseAuth mAuth;
    RecyclerView rv;
    TextView tvfindfriend;
    EditText etfindfriend;
    ImageView btnback,btnsearch;
    Toolbar toolbar;
    FirebaseRecyclerAdapter<User, FindFriendAdapter.FindFriendViewHolder>adapter;
    RecyclerView.LayoutManager manager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_friend);
        mAuth=FirebaseAuth.getInstance();
        init();
        setSupportActionBar(toolbar);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FindFriendActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        userReference=FirebaseDatabase.getInstance().getReference();
        etfindfriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name=charSequence.toString();
                onStart();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<User> options=null;
        if (name.equals(" ")) {
             options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(userReference.child("Users"), User.class)
                    .build();
        }
        else{
            options = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(userReference.child("Users").orderByChild("username").startAt(name).endAt(name+"\uf8ff"), User.class)
                    .build();
        }
        adapter=new FindFriendAdapter(options,FindFriendActivity.this);
        rv.setAdapter(adapter);

        manager=new LinearLayoutManager(FindFriendActivity.this);
        rv.setLayoutManager(manager);
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init() {
        if(mAuth.getCurrentUser()!=null){
            userkey=mAuth.getCurrentUser().getUid();
        }
        rv=findViewById(R.id.statusrv);
        tvfindfriend=findViewById(R.id.tvfindfriend);
        etfindfriend=findViewById(R.id.etfindfriend);
        btnback=findViewById(R.id.btn_back);
        btnsearch=findViewById(R.id.btn_search);
        toolbar=findViewById(R.id.toolbar);
        btnsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvfindfriend.setVisibility(View.GONE);
                etfindfriend.setVisibility(View.VISIBLE);
            }
        });
    }
}
