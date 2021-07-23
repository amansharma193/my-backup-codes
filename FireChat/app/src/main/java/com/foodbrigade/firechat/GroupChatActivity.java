package com.foodbrigade.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.Adapter.GroupMessageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class GroupChatActivity extends AppCompatActivity {
    TextView tvfrnd,tvdesc;
    CircleImageView civfrndimg;
    ImageView btnback,btnemoji,btnfile;
    Toolbar toolbar;
    DatabaseReference rootReference;
    String userkey,friend,random,userImage;
    CircleButton btnSend;
    View rootView;
    RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder>adapter;
    ArrayList<Message>al;
    EmojiconEditText etmsg;
    EmojIconActions actions;
    User u;
    TextView lastSeen;
    StorageReference storageReference,filepath;
    RecyclerView rv;
    Message m;
    Group group;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        init();
        Intent in = getIntent();
        group = (Group) in.getSerializableExtra("group");
        tvfrnd.setText(group.getName());
        Picasso.get().load(group.getIcon()).placeholder(R.drawable.radio).into(civfrndimg);
        lastSeen.setText("Tap for group info");
        actions = new EmojIconActions(this, rootView, etmsg, btnemoji);
        actions.ShowEmojIcon();
        actions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

            }

            @Override
            public void onKeyboardClose() {

            }
        });
        lastSeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GroupChatActivity.this, MemberInfoActivity.class);
                in.putExtra("groupkey", group);
                startActivity(in);
                finish();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GroupChatActivity.this, MainActivity.class);
                startActivity(in);
                finish();
            }
        });
        btnfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Bundle bn=new Bundle();
                    bn.putString("groupkey",group.getGroupid());
                    BottomSheetDialogFragment dialogFragment = new GroupMessageBottomSheet(userkey, group.getGroupid());
                    dialogFragment.setArguments(bn);
                    dialogFragment.show(GroupChatActivity.this.getSupportFragmentManager(), "dialog bottom sheet");
                } catch (Exception e){

                }
            }
        });
        rootReference.child("Group").child(group.getGroupid()).child("Messages").orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    final Message msg = dataSnapshot.getValue(Message.class);
                    rootReference.child("Group").child(group.getGroupid()).child("Messages").child(msg.getKey()).child("delete").child(userkey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                            } else {
                                al.add(msg);
                                adapter.notifyDataSetChanged();
                                rv.smoothScrollToPosition(adapter.getItemCount());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        rootReference.child("Users").child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User u = snapshot.getValue(User.class);
                    if (u.getImage() != null) {
                        userImage = u.getImage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etmsg.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    return;
                }
                etmsg.setText("");
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                String date = sd.format(c.getTime());
                sd = new SimpleDateFormat("hh:mm a");
                String time = sd.format(c.getTime());
                long timestamp = Calendar.getInstance().getTimeInMillis();
                Message message = new Message();
                message.setDate(date);
                message.setTime(time);
                message.setMessage(msg);
                message.setFrom(userkey);
                message.setType("text");
                String key = rootReference.child("Group").child(group.getGroupid()).child("Messages").push().getKey();
                message.setKey(key);
                message.setSendericon(userImage);
                message.setTimestamp(timestamp);
                rootReference.child("Group").child(group.getGroupid()).child("Messages").child(key).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(GroupChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        adapter = new GroupMessageAdapter(GroupChatActivity.this, al);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        new ItemTouchHelper(new DeleteOnSwipe(0, ItemTouchHelper.RIGHT)).attachToRecyclerView(rv);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rootReference.child("Group").child(group.getGroupid()).child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        tvfrnd=findViewById(R.id.tvfrnd_name);
        civfrndimg=findViewById(R.id.civ_chat);
        btnback=findViewById(R.id.btn_back);
        rootReference= FirebaseDatabase.getInstance().getReference();
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        etmsg=findViewById(R.id.etmessage);
        btnSend=findViewById(R.id.btnSend_msg);
        btnemoji=findViewById(R.id.ivemoji);
        btnfile=findViewById(R.id.ivmedia);
        rootView=findViewById(R.id.rootview);
        rv=findViewById(R.id.chattingrv);
        lastSeen=findViewById(R.id.tvSeen);
        al=new ArrayList<>();
    }
    public class DeleteOnSwipe extends ItemTouchHelper.SimpleCallback{
        public DeleteOnSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position=viewHolder.getAdapterPosition();
            final View view=findViewById(R.id.rootview);
            final Message message=al.get(position);
            al.remove(position);
            adapter.notifyDataSetChanged();
            final String msgkey=message.getKey();
            String sender =message.getFrom();
            rootReference.child("Group").child(group.getGroupid()).child("Messages").child(message.getKey()).child("delete").child(userkey).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Toast.makeText(GroupChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
