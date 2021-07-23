package com.e.skychat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.e.skychat.adapter.MessageAdapter;
import com.e.skychat.beans.Message;
import com.e.skychat.beans.User;
import com.e.skychat.fargment.ChatMessageBottomSheet;
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

public class ChatMessageActivity extends AppCompatActivity {

    CircleImageView civFriendProfile;
    TextView tvFriendName,tvUserState;
    EditText etMessage;
    ImageView ivButtonImage,ivButtonAttachFile,ivButtonSend,ivBackArrow;
    Toolbar toolBar;
    String currentUserId,receiverId;
    DatabaseReference messageReference,messageCounterReference,contactReference;
    ArrayList<Message> messageList;
    ArrayAdapter<Message> adapter;
    ListView lv;
    StorageReference storageReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initComponent();

        Intent in = getIntent();
        User user = (User)in.getSerializableExtra("friend");


        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(civFriendProfile);
        tvFriendName.setText(user.getName());

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        receiverId = user.getUid();
        messageReference = FirebaseDatabase.getInstance().getReference("Messages");
        messageCounterReference = FirebaseDatabase.getInstance().getReference("MessageCounter");
        storageReference = FirebaseStorage.getInstance().getReference("ImageMessages");

        ivButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = etMessage.getText().toString();

                if(TextUtils.isEmpty(message))
                    return;

                final String messageId = messageReference.push().getKey();

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
                msg.setTimestamp(timestamp);
                msg.setType("text");
                msg.setMessageId(messageId);
                msg.setMessage(message);
                msg.setFrom(currentUserId);
                msg.setTo(receiverId);

                messageReference.child(currentUserId).child(receiverId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          messageReference.child(receiverId).child(currentUserId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                 if(task.isSuccessful()){
                                   etMessage.setText("");
                                   messageCounterReference.child(receiverId).child(currentUserId).child(messageId).setValue("");
                                 }
                                 else{
                                     String errorMessage = task.getException().toString();
                                     Toast.makeText(ChatMessageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                     etMessage.setText("");
                                 }
                              }
                          });
                      }
                    }
                });
            }
        });

        messageReference.child(currentUserId).child(receiverId).orderByChild("timestamp").addChildEventListener(new ChildEventListener() {
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
                //in.setType("video/*")
                startActivityForResult(Intent.createChooser(in,"Select image"),111);
            }
        });

        ivButtonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessageBottomSheet dialog = new ChatMessageBottomSheet(currentUserId,receiverId);
                dialog.show(getSupportFragmentManager(),"send document");
            }
        });

    }// eof onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 111 && resultCode == RESULT_OK && data!=null && data.getData()!=null){

            final ProgressDialog pd = new ProgressDialog(ChatMessageActivity.this);
            pd.setTitle("Sending..");
            pd.setMessage("please wait...");
            pd.show();
            Uri imageUri = data.getData();
            final String messageId = messageReference.push().getKey();
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
                            msg.setTo(receiverId);
                            msg.setTimestamp(timestamp);
                            msg.setMessage(imageUrl);

                            messageReference.child(currentUserId).child(receiverId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   messageReference.child(receiverId).child(currentUserId).child(messageId).setValue(msg).addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                         pd.dismiss();
                                         if(task.isSuccessful()){

                                         }
                                         else{
                                             String errorMessage = task.getException().toString();
                                             Toast.makeText(ChatMessageActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                         }
                                       }
                                   });
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
        usersReference.child(receiverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     User user = dataSnapshot.getValue(User.class);
                     if(user.getState()!=null){
                         if(user.getState().equals("online")){
                           tvUserState.setText("online");
                         }
                         else if(user.getState().equals("offline")){
                             tvUserState.setText("last seen : "+user.getDate()+" "+user.getTime());
                         }
                     }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Remove Friend");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String title = item.getTitle().toString();
        if(title.equalsIgnoreCase("Remove Friend")){
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();
            contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle("Alert!");
            ab.setMessage("If you remove friend then you will not able to see the chat of this user.");
            ab.setPositiveButton("Yes, remove", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                   final ProgressDialog pd = new ProgressDialog(ChatMessageActivity.this);
                   pd.setTitle("please wait");
                   pd.setMessage("while removing friend");
                   pd.show();
                    contactReference.child(currentUserId).child(receiverId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             contactReference.child(receiverId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                   pd.dismiss();
                                   if(task.isSuccessful()){
                                       Toast.makeText(ChatMessageActivity.this, "Contact Removed", Toast.LENGTH_SHORT).show();
                                       finish();
                                   }
                                   else{
                                       Toast.makeText(ChatMessageActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                   }
                                 }
                             });
                         }
                      }
                  });
                }
            });
            ab.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            ab.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponent(){
        lv= findViewById(R.id.lv);
        tvUserState = findViewById(R.id.tvUserState);
        toolBar = findViewById(R.id.chatMessageToolBar);
        setSupportActionBar(toolBar);
        civFriendProfile = findViewById(R.id.civFriendProfile);
        tvFriendName = findViewById(R.id.tvFriendName);
        etMessage = findViewById(R.id.etMessage);
        ivButtonImage = findViewById(R.id.ivButtonImage);
        ivButtonAttachFile = findViewById(R.id.ivButtonAttachFile);
        ivButtonSend = findViewById(R.id.ivButtonSend);
        ivBackArrow = findViewById(R.id.ivBackArrow);
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(ChatMessageActivity.this,messageList);
        lv.setAdapter(adapter);
        ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
