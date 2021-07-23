package com.e.skychat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendRequestActivity extends AppCompatActivity {
    CircleImageView civProfile;
    TextView tvUsername,tvStatus;
    Button btnRequest;
    Toolbar toolBar;
    User user;
    String receiverUserId;
    String currentUserId;
    DatabaseReference chatRequestReference,contactReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);

        Intent in = getIntent();

        user = (User) in.getSerializableExtra("user");
        receiverUserId = user.getUid();

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatRequestReference = FirebaseDatabase.getInstance().getReference();

        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        initComponent();

        if(currentUserId.equals(receiverUserId)){
            btnRequest.setVisibility(View.GONE);
            toolBar.setTitle("You");
        }

        contactReference.child(currentUserId).child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    btnRequest.setVisibility(View.GONE);
                    toolBar.setTitle("Already friend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkRequestStatus();

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = btnRequest.getText().toString();
                if(status.equalsIgnoreCase("Send request"))
                    sendRequest();
                else if(status.equalsIgnoreCase("Cancel request"))
                    cancelRequest();
            }
        });
    }

    private void cancelRequest() {
      chatRequestReference.child("ChatRequest").child(currentUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                chatRequestReference.child("ChatRequest").child(receiverUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(SendRequestActivity.this, "Request cancelled", Toast.LENGTH_SHORT).show();
                           btnRequest.setText("Send request");
                           toolBar.setTitle("Send request");
                       }
                    }
                });
            }
          }
      });
    }

    private void checkRequestStatus() {
      chatRequestReference.child("ChatRequest").child(currentUserId).child(receiverUserId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.exists()){

                String requestType =  dataSnapshot.child("request_type").getValue().toString();
                 Log.e("RequestType"," => "+requestType);
                if(requestType.equals("sent")) {
                    btnRequest.setText("Cancel request");
                    toolBar.setTitle("Cancel request");
                }
             }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }

    private void sendRequest(){
        chatRequestReference.child("ChatRequest").child(currentUserId).child(receiverUserId)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   chatRequestReference.child("ChatRequest").child(receiverUserId).child(currentUserId)
                   .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful()){
                             FirebaseDatabase.getInstance().getReference("Users").child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot snapshot) {
                                     if(snapshot.exists()){
                                         User receiver = snapshot.getValue(User.class);
                                         try{
                                             RequestQueue queue = Volley.newRequestQueue(SendRequestActivity.this);
                                             String url = "https://fcm.googleapis.com/fcm/send";
                                             JSONObject data = new JSONObject();
                                             data.put("title", "Chat request");
                                             data.put("body", "From : "+receiver.getName());
                                             JSONObject notification_data = new JSONObject();
                                             notification_data.put("data", data);
                                             notification_data.put("to",receiver.getToken());
                                             JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                                                 @Override
                                                 public void onResponse(JSONObject response) {
                                                 }
                                             }, new Response.ErrorListener() {
                                                 @Override
                                                 public void onErrorResponse(VolleyError error) {
                                                     Toast.makeText(SendRequestActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                                                 }
                                             }) {
                                                 @Override
                                                 public Map<String, String> getHeaders() {
                                                     String api_key_header_value = "Key=AAAA_8lmWnQ:APA91bEYQuN6DDzns0nY2CzXq-FUhVCvv0pGXq0nr3iH_sg27WDB8PcN1RFTz7-If5SNVHOfA3SMuxQyWyPZKb-cns4Sd06iMbIb7vruOHtiBrebRDZAqrMx5Hl5zmHanUFXDCi6ekSr";
                                                     Map<String, String> headers = new HashMap<>();
                                                     headers.put("Content-Type", "application/json");
                                                     headers.put("Authorization", api_key_header_value);
                                                     return headers;
                                                 }
                                             };
                                             queue.add(request);
                                         }catch (Exception e){
                                             e.printStackTrace();
                                         }
                                     }
                                 }
                                 @Override
                                 public void onCancelled(@NonNull DatabaseError error) {
                                     Toast.makeText(SendRequestActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                                 }
                             });
                             Toast.makeText(SendRequestActivity.this, "Request send", Toast.LENGTH_SHORT).show();
                             btnRequest.setText("Cancel request");
                             toolBar.setTitle("Cancel request");
                         }
                         else{
                             String message = task.getException().toString();
                             Toast.makeText(SendRequestActivity.this, message, Toast.LENGTH_SHORT).show();
                         }
                       }
                   });
                }
            }
        });
    }
    private void initComponent() {
       civProfile = findViewById(R.id.civProfile);
       tvUsername = findViewById(R.id.tvUsername);
       tvStatus = findViewById(R.id.tvStatus);
       toolBar = findViewById(R.id.sendRequestToolBar);
       btnRequest = findViewById(R.id.btnSendRequest);
       toolBar.setTitle("Send request");
       toolBar.setTitleTextColor(getResources().getColor(R.color.white));
       setSupportActionBar(toolBar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(civProfile);
        tvUsername.setText(user.getName());
        tvStatus.setText(user.getStatus());
    }
}
