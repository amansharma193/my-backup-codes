package com.foodbrigade.firechat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.Adapter.ChatAdapter;
import com.foodbrigade.firechat.Adapter.MessageAdapter;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.foodbrigade.firechat.fragment.ContactsFragment;
import com.foodbrigade.firechat.fragment.MessageBottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
public class ChatActivity extends AppCompatActivity {
    TextView tvfrnd;
    CircleImageView civfrndimg;
    ImageView btnback,btnemoji,btnfile;
    Toolbar toolbar;
    DatabaseReference rootReference;
    String userkey,friend,random;
    CircleButton btnSend;
    View rootView;
    EmojiconEditText etmsg;
    EmojIconActions actions;
    User u;
    int positiondel,flag=0;
    TextView lastSeen;
    StorageReference storageReference,filepath;
    RecyclerView rv;
    Message m;
    MessageAdapter adapter;
    ArrayList<Message>al;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        toolbar=findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        init();
             Intent intent = getIntent();
             u = (User) intent.getSerializableExtra("obk");
             tvfrnd.setText(u.getUsername());
                 if(u.getState().equalsIgnoreCase("online")){
                     lastSeen.setText("Online");
                 }
                 else if (u.getState().equalsIgnoreCase("offline")){
                     lastSeen.setText("Last Seen : "+u.getTime()+" "+u.getDate());
                 }
             Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(civfrndimg);
             friend = u.getKey();
             adapter=new MessageAdapter(ChatActivity.this,al);
             rv.setAdapter(adapter);
             storageReference= FirebaseStorage.getInstance().getReference("Chat Images");
             rv.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        actions=new EmojIconActions(this,rootView,etmsg,btnemoji);
        actions.ShowEmojIcon();
        actions.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
            }
            @Override
            public void onKeyboardClose() {
            }
        });
        rootReference.child("Messages").child(userkey).child(friend).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (flag==0) {
                        al.add(msg);
                        adapter.notifyDataSetChanged();
                        rv.smoothScrollToPosition(adapter.getItemCount());
                    }
                    else if (flag==1){
                        al.add(positiondel,msg);
                        adapter.notifyDataSetChanged();
                        rv.smoothScrollToPosition(adapter.getItemCount());
                    }
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
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(ChatActivity.this,MainActivity.class);
                startActivity(in);
                finish();
            }
        });
        civfrndimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tvfrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                flag=0;
                Calendar c=Calendar.getInstance();
                SimpleDateFormat sd=new SimpleDateFormat("MMM dd, yyyy");
                String date=sd.format(c.getTime());
                sd=new SimpleDateFormat("hh:mm a");
                String message=etmsg.getText().toString();
                etmsg.setText("");
                if (TextUtils.isEmpty(message)){
                    return;
                }
                String time=sd.format(c.getTime());
                random=rootReference.child("Messages").child(userkey).child(u.getKey()).push().getKey();
                 m=new Message(message,"text",date,time,userkey,u.getKey(),random, Calendar.getInstance().getTimeInMillis());
                rootReference.child("Messages").child(userkey).child(u.getKey()).child(random).setValue(m)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            rootReference.child("Messages").child(u.getKey()).child(userkey).child(random).setValue(m)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    rootReference.child("Message Counter").child(u.getKey()).child(userkey).child(random).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()){
                                                Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
        btnfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bn=new Bundle();
                bn.putString("groupkey",u.getKey());
                BottomSheetDialogFragment dialogFragment=new MessageBottomSheet(userkey,u.getKey());
                dialogFragment.setArguments(bn);
                dialogFragment.show(ChatActivity.this.getSupportFragmentManager(),"dialog bottom sheet");
            }
        });
        new ItemTouchHelper(new ChatActivity.DeleteOnSwipe(0,ItemTouchHelper.RIGHT)).attachToRecyclerView(rv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Unfriend");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title=item.getTitle().toString();
        if (title.equalsIgnoreCase("Unfriend")){
            final CustomProgressDialogue object=new CustomProgressDialogue(ChatActivity.this,"Please wait...");
            object.show();
            rootReference.child("Contacts").child(userkey).child(friend).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        rootReference.child("Contacts").child(friend).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                object.dismiss();
                                if (!task.isSuccessful()){
                                    Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (u.getState()!=null){
            if (u.getState().equalsIgnoreCase("online")){
                lastSeen.setText("online");
            }
        }
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
        al=new ArrayList<>();

        lastSeen=findViewById(R.id.tvSeen);
    }
    class DeleteOnSwipe extends ItemTouchHelper.SimpleCallback{

        public DeleteOnSwipe(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position=viewHolder.getAdapterPosition();
            positiondel=position;
            View view=findViewById(R.id.rootview);
            final Message message= al.get(position);
            al.remove(position);
            adapter.notifyDataSetChanged();
            final String msgkey=message.getKey();
            String sender =message.getFrom();
            if (userkey.equals(message.getFrom())){
                rootReference.child("Messages").child(userkey).child(message.getTo()).child(msgkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else{
                rootReference.child("Messages").child(message.getTo()).child(message.getFrom()).child(msgkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            adapter.notifyDataSetChanged();
                            Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            Snackbar snackbar= Snackbar.make(view,"Are you sure ?",Snackbar.LENGTH_LONG);
            snackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flag=1;
                    if (userkey.equals(message.getFrom())){
                        rootReference.child("Messages").child(userkey).child(message.getTo()).child(msgkey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if (!task.isSuccessful()){
                                   adapter.notifyDataSetChanged();
                                   Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                               }
                            }
                        });
                    } else{
                        rootReference.child("Messages").child(message.getTo()).child(message.getFrom()).child(msgkey).setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()){
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
            snackbar.show();

        }
    }
}
