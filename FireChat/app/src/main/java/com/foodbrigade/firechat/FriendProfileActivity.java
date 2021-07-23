package com.foodbrigade.firechat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.foodbrigade.firechat.dialog.CustomProgressDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfileActivity extends AppCompatActivity {
    Toolbar toolbar;
    TextView tvname,tvStatus;
    CircleImageView civ;
    Button btnSend;
    DatabaseReference rootreference,messageReference,contactReference;
    FirebaseAuth mAuth;
    String sender,receiver;
    User u;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_profile);
        init();
        Intent intent=getIntent();
        u=(User)intent.getSerializableExtra("key");
        toolbar.setTitle(u.getUsername());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        receiver=u.getKey();
        contactReference=FirebaseDatabase.getInstance().getReference().child("Contacts");
        sender=mAuth.getCurrentUser().getUid();
        contactReference.child(sender).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    btnSend.setText("send Message");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (sender.equals(receiver)){
            toolbar.setTitle("You");
            btnSend.setText("Edit Profile");
        }
        else{
            checkrequeststaus();
            btnSend.setText("SEND REQUEST");
        }
        tvname.setText(u.getUsername());
        tvStatus.setText(u.getStatus());
        Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(civ);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status=btnSend.getText().toString();
                if (sender.equals(receiver)){
                    Intent intent1=new Intent(FriendProfileActivity.this,Setting_Activity.class);
                    startActivity(intent1);
                    finish();
                }
               else if(status.equalsIgnoreCase("SEND REQUEST")){
                    rootreference.child(sender).child(receiver).child("Request Type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                rootreference.child(receiver).child(sender).child("Request Type").setValue("Received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            btnSend.setText("Cancel request");
                                            rootreference.child("Users").child(sender).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()){
                                                        User curuser=snapshot.getValue(User.class);
                                                        try {
                                                            String url="https://fcm.googleapis.com/fcm/send";
                                                            String token=u.getToken();
                                                            JSONObject data=new JSONObject();
                                                            data.put("title","Chat Request");
                                                            data.put("body","From : "+curuser.getUsername());
                                                            JSONObject notdata=new JSONObject();
                                                            notdata.put("data",data);
                                                            notdata.put("to",token);

                                                            JsonObjectRequest request=new JsonObjectRequest(url, notdata, new Response.Listener<JSONObject>() {
                                                                @Override
                                                                public void onResponse(JSONObject response) {

                                                                }
                                                            }, new Response.ErrorListener() {
                                                                @Override
                                                                public void onErrorResponse(VolleyError error) {

                                                                }
                                                            }){
                                                                @Override
                                                                public Map<String, String> getHeaders(){
                                                                    String value="Key=AAAAPUd7kng:APA91bGd98eS7vSIT-YdkbM_4aTk8iAZHIl6-w77nTygKgkEO8cMPfBgpk7cUlTriB5OwpUZfYhHcb7KBl3eYdelhoM3PP_oNRConOLuNNV4rJq9gtiwkMKKCmU6iKIdh7vu81U-y7VT";
                                                                    Map<String,String>headers=new HashMap<>();
                                                                    headers.put("Content-Type","application/json");
                                                                    headers.put("Authorization",value);
                                                                    return headers;
                                                                }
                                                            };
                                                            RequestQueue queue= Volley.newRequestQueue(FriendProfileActivity.this);
                                                            queue.add(request);
                                                        }catch(Exception e){

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        }
                    });
                }
               else if (status.equalsIgnoreCase("Cancel Request")){
                   rootreference.child(sender).child(receiver).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               rootreference.child(receiver).child(sender).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           btnSend.setText("send Request");
                                       }
                                   }
                               });
                           }
                       }
                   });
                }
               else if (btnSend.getText().equals("send Message")){
                   Intent intent=new Intent(FriendProfileActivity.this,ChatActivity.class);
                   intent.putExtra("obk",u);
                   startActivity(intent);
                }
            }
        });
    }

    private void checkrequeststaus() {
        rootreference.child(sender).child(receiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String request = dataSnapshot.child("Request Type").getValue().toString();
                    if (request.equalsIgnoreCase("sent")) {
                        btnSend.setText("Cancel request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        toolbar=findViewById(R.id.toolbar2);
        tvname=findViewById(R.id.prname);
        tvStatus=findViewById(R.id.prstatus);
        civ=findViewById(R.id.civ2);
        btnSend=findViewById(R.id.btnsendRequest);
        mAuth=FirebaseAuth.getInstance();
        rootreference= FirebaseDatabase.getInstance().getReference().child("Chat Request");
        messageReference= FirebaseDatabase.getInstance().getReference().child("Messages");
    }
}
