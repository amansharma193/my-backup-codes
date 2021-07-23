package com.e.skychat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.adapter.UserAdapter;
import com.e.skychat.beans.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FindFriendActivity extends AppCompatActivity {
    Toolbar toolBar;
    RecyclerView rv;
    TextView tvText;
    EditText etFindFriend;
    ImageView ivGoToBack,ivSearch;
    DatabaseReference usersReference;
    FirebaseRecyclerAdapter<User, UserAdapter.UserViewHolder>adapter;
    String name = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        initComponent();

        usersReference = FirebaseDatabase.getInstance().getReference().child("Users");

        etFindFriend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                name = charSequence.toString();
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
        FirebaseRecyclerOptions<User> option;
        if(name.equals("")) {
            option = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(usersReference, User.class)
                    .build();
        }
        else{
            option = new FirebaseRecyclerOptions.Builder<User>()
                    .setQuery(usersReference.orderByChild("name").startAt(name).endAt(name+"\uf8ff"), User.class)
                    .build();

        }

        adapter = new UserAdapter(option,FindFriendActivity.this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(FindFriendActivity.this));
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initComponent() {
      toolBar = findViewById(R.id.findFriendToolBar);
      rv = findViewById(R.id.rv);

      tvText = findViewById(R.id.tvText);
      etFindFriend = findViewById(R.id.etFindFriend);
      ivSearch = findViewById(R.id.ivSearch);
      ivGoToBack = findViewById(R.id.ivGoToBack);

      toolBar.setTitle("Find friend");
      toolBar.setTitleTextColor(getResources().getColor(R.color.white));

      setSupportActionBar(toolBar);

      ivSearch.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              tvText.setVisibility(View.GONE);
              etFindFriend.setVisibility(View.VISIBLE);
          }
      });
      ivGoToBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              finish();
          }
      });

    }
}
