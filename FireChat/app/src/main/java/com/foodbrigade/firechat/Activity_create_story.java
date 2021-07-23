package com.foodbrigade.firechat;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class Activity_create_story extends AppCompatActivity {
    String userkey;
    EmojiconEditText ettext;
    FloatingActionButton btncreate;
    int code;
    Uri imguri,vidUri;
    ImageView ivStory;
    EmojiconTextView txtstatus;
    VideoView vd;
    DatabaseReference storyReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_story);
        Intent in=getIntent();
        vd=findViewById(R.id.vdstory);
        code=in.getIntExtra("key",100);
        txtstatus=findViewById(R.id.txtstatus);
        ivStory=findViewById(R.id.ivstory);
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        storyReference= FirebaseDatabase.getInstance().getReference("Stories").child(userkey);
        btncreate=findViewById(R.id.btncreate);
        ettext=findViewById(R.id.caption);
        if (code==101|| code==222){
            imguri=Uri.parse(in.getStringExtra("uri"));
            ivStory.setVisibility(View.VISIBLE);
            ivStory.setImageURI(imguri);
            vd.setVisibility(View.GONE);
        }
        else if (code==444) {
            vidUri = Uri.parse(in.getStringExtra("uri"));
            vd.setVisibility(View.VISIBLE);
            ivStory.setVisibility(View.GONE);
            vd.setVideoURI(vidUri);
            vd.setMediaController(new MediaController(this));
            vd.requestFocus();
            vd.start();
        }
        if (code==333){
            txtstatus.setVisibility(View.VISIBLE);
            txtstatus.setText("Enter below...");
            ettext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    txtstatus.setText(""+charSequence);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        storageReference=FirebaseStorage.getInstance().getReference("Story");
        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CustomProgressDialogue pd = new CustomProgressDialogue(Activity_create_story.this, "Uploading...");
                pd.show();
                if (code == 101||code ==222) {
                    final String storyid = storyReference.push().getKey();
                    StorageReference filepath = storageReference.child(storyid + ".jpg");
                    filepath.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imgUrl = uri.toString();
                                    String text = ettext.getText().toString();
                                    if (TextUtils.isEmpty(text)) {
                                        text = "";
                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyyy");
                                    String date = sd.format(calendar.getTime());
                                    sd = new SimpleDateFormat("hh:mm a");
                                    String time = sd.format(calendar.getTime());
                                    long timestamp = calendar.getTimeInMillis();
                                    Story story = new Story();
                                    story.setDate(date);
                                    story.setTime(time);
                                    story.setTimestamp(timestamp);
                                    story.setUrl(imgUrl);
                                    story.setUid(userkey);
                                    story.setType("image");
                                    story.setStoryid(storyid);
                                    story.setText(text);
                                    storyReference.child(storyid).setValue(story).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            if (task.isSuccessful()) {
                                                finish();
                                                Toast.makeText(Activity_create_story.this, "story created", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(Activity_create_story.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                if (code == 444) {
                    final String storyid = storyReference.push().getKey();
                    StorageReference filepath = storageReference.child(storyid + ".mp4");
                    filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imgUrl = uri.toString();
                                    String text = ettext.getText().toString();
                                    if (TextUtils.isEmpty(text)) {
                                        text = "";
                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyyy");
                                    String date = sd.format(calendar.getTime());
                                    sd = new SimpleDateFormat("hh:mm a");
                                    String time = sd.format(calendar.getTime());
                                    long timestamp = calendar.getTimeInMillis();
                                    Story story = new Story();
                                    story.setDate(date);
                                    story.setTime(time);
                                    story.setTimestamp(timestamp);
                                    story.setUrl(imgUrl);
                                    story.setUid(userkey);
                                    story.setType("video");
                                    story.setStoryid(storyid);
                                    story.setText(text);
                                    storyReference.child(storyid).setValue(story).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            if (task.isSuccessful()) {
                                                finish();
                                                Toast.makeText(Activity_create_story.this, "story created", Toast.LENGTH_SHORT).show();
                                            } else
                                                Toast.makeText(Activity_create_story.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
                else if (code==333){
                    String storyid = storyReference.push().getKey();
                    String text = ettext.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                       return;
                    }
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sd = new SimpleDateFormat("MMM dd,yyyy");
                    String date = sd.format(calendar.getTime());
                    sd = new SimpleDateFormat("hh:mm a");
                    String time = sd.format(calendar.getTime());
                    long timestamp = calendar.getTimeInMillis();
                    Story story = new Story();
                    story.setDate(date);
                    story.setTime(time);
                    story.setTimestamp(timestamp);
                    story.setUid(userkey);
                    story.setType("text");
                    story.setStoryid(storyid);
                    story.setText(text);
                    storyReference.child(storyid).setValue(story).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pd.dismiss();
                            if (task.isSuccessful()) {
                                finish();
                                Toast.makeText(Activity_create_story.this, "story created", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(Activity_create_story.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
