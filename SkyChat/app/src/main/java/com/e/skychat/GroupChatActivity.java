package com.e.skychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.skychat.adapter.GroupMessageAdapter;
import com.e.skychat.beans.Group;
import com.e.skychat.beans.Message;
import com.e.skychat.beans.User;
import com.e.skychat.fargment.GroupChatMessageBottomSheet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    CircleImageView civGroupIcon;
    TextView tvGroupName,tvGroupDescription;
    ImageView ivButtonSend,ivButtonImage,ivButtonAttachFile;
    ListView lv;
    EditText etMessage;
    String currentUserId;
    DatabaseReference groupReference,userReference;
    String groupId,currentUserImageurl;
    GroupMessageAdapter adapter;
    ArrayList<Message> messageList;
    StorageReference storageReference;
    Toolbar toolBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initComponent();

        // Getting currentUserId and groupRefernece
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        groupReference = FirebaseDatabase.getInstance().getReference("Group");
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        storageReference = FirebaseStorage.getInstance().getReference("ImageMessages");

        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     User u = dataSnapshot.getValue(User.class);
                     if(u.getImage()!=null)
                         currentUserImageurl = u.getImage();
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Gettting intent and setting group icaon and name and description
        Intent in = getIntent();
        final Group group = (Group)in.getSerializableExtra("group");
        groupId = group.getGroupId();


        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(GroupChatActivity.this,GroupInfoActivity.class);
                in.putExtra("group",group);
                startActivity(in);
            }
        });

        Picasso.get().load(group.getIcon()).placeholder(R.drawable.firechaticon).into(civGroupIcon);
        tvGroupName.setText(group.getGroupName());

        // Sending Text message in a group on ivButtomSend Click
        ivButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data= etMessage.getText().toString();
                if(TextUtils.isEmpty(data))
                    return;

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyy");
                String date = sd.format(calendar.getTime());

                sd = new SimpleDateFormat("hh:mm a");
                String time = sd.format(calendar.getTime());

                long timestamp = calendar.getTimeInMillis();
                String messageId = groupReference.push().getKey();

                Message msg = new Message();
                msg.setMessage(data);
                msg.setMessageId(messageId);
                msg.setType("text");
                msg.setFrom(currentUserId);
                msg.setTime(time);
                msg.setDate(date);
                msg.setTimestamp(timestamp);
                msg.setSenderIcon(currentUserImageurl);
                groupReference.child(groupId).child("messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            etMessage.setText("");
                        else
                            Toast.makeText(GroupChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        groupReference.child(groupId).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   if(dataSnapshot.exists()){
                       Message msg = dataSnapshot.getValue(Message.class);
                       messageList.add(msg);
                       adapter.notifyDataSetChanged();
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
        ivButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(Intent.createChooser(in,"Select image"),111);
            }
        });

        ivButtonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupChatMessageBottomSheet dialog = new GroupChatMessageBottomSheet(currentUserId,groupId,currentUserImageurl);
                dialog.show(getSupportFragmentManager(),"");
            }
        });
    } // eof onCreate



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            final ProgressDialog pd = new ProgressDialog(GroupChatActivity.this);
            pd.setTitle("Sending..");
            pd.setMessage("please wait...");
            pd.show();
            Uri imageUri = data.getData();
            final String messageId = groupReference.push().getKey();
            StorageReference filePath = storageReference.child(messageId+".jpg");
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            Calendar cdate = Calendar.getInstance();
                            SimpleDateFormat sd = new SimpleDateFormat("MMM dd, yyyy");
                            String date = sd.format(cdate.getTime());

                            Calendar ctime = Calendar.getInstance();
                            sd = new SimpleDateFormat("hh:mm a");
                            String time = sd.format(ctime.getTime());

                            long timestamp = Calendar.getInstance().getTimeInMillis();

                            final Message msg = new Message();
                            msg.setDate(date);
                            msg.setTime(time);
                            msg.setMessageId(messageId);
                            msg.setType("image");
                            msg.setFrom(currentUserId);
                            msg.setTimestamp(timestamp);
                            msg.setMessage(imageUrl);
                            msg.setSenderIcon(currentUserImageurl);
                            groupReference.child(groupId).child("messages").child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   pd.dismiss();
                                  if(!task.isSuccessful())
                                      Toast.makeText(GroupChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        groupReference.child(groupId).child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(!dataSnapshot.exists())
                   finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initComponent(){
        civGroupIcon = findViewById(R.id.civFriendProfile);
        tvGroupName = findViewById(R.id.tvFriendName);
        tvGroupDescription = findViewById(R.id.tvUserState);
        tvGroupDescription.setText("Tap for more info.");
        etMessage = findViewById(R.id.etMessage);
        lv = findViewById(R.id.lv);
        ivButtonSend = findViewById(R.id.ivButtonSend);
        ivButtonAttachFile = findViewById(R.id.ivButtonAttachFile);
        ivButtonImage = findViewById(R.id.ivButtonImage);
        messageList = new ArrayList<>();
        adapter = new GroupMessageAdapter(GroupChatActivity.this,messageList);
        lv.setAdapter(adapter);

        toolBar = findViewById(R.id.chatMessageToolBar);

    }
}
