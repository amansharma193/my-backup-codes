package com.foodbrigade.firechat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.Adapter.MakeAdminAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MakeAdminActivity extends AppCompatActivity {
    Toolbar toolbar;
    String groupkey;
    ArrayList<String>al;
    RecyclerView rv;
    DatabaseReference rootReference;
    MakeAdminAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_admin_activity);
        init();
        Intent in=getIntent();
        al= in.getStringArrayListExtra("group");
        groupkey=al.get(0);
        adapter=new MakeAdminAdapter(MakeAdminActivity.this,al);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(MakeAdminActivity.this));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rootReference= FirebaseDatabase.getInstance().getReference();
        rv=findViewById(R.id.rv);
    }
}
