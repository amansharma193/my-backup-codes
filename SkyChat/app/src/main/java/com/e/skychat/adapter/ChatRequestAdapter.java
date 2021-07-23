package com.e.skychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.R;
import com.e.skychat.beans.ChatRequest;
import com.e.skychat.beans.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRequestAdapter extends FirebaseRecyclerAdapter<ChatRequest, ChatRequestAdapter.ChatRequestViewHolder> {
    Context context;
    DatabaseReference userReference,contactReference,chatRequestReference;
    String currentUserId;
    public ChatRequestAdapter(@NonNull FirebaseRecyclerOptions<ChatRequest> options, Context context) {
        super(options);
        this.context = context;
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        contactReference = FirebaseDatabase.getInstance().getReference("Contacts");
        chatRequestReference = FirebaseDatabase.getInstance().getReference("ChatRequest");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChatRequestViewHolder holder, int i, @NonNull ChatRequest chatRequest) {
      String senderUserId = getRef(i).getKey();
      userReference.child(senderUserId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             if(dataSnapshot.exists()){
                  User user = dataSnapshot.getValue(User.class);
                  final String senderUserId = user.getUid();
                  holder.tvStatus.setText(user.getStatus());
                  holder.tvName.setText(user.getName());
                  Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
                  holder.btnAcceptRequet.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                        contactReference.child(currentUserId).child(senderUserId).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   contactReference.child(senderUserId).child(currentUserId).child("contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                         if(task.isSuccessful()){
                                           chatRequestReference.child(currentUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                  if(task.isSuccessful()){
                                                      chatRequestReference.child(senderUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                          @Override
                                                          public void onComplete(@NonNull Task<Void> task) {
                                                             if(task.isSuccessful()){
                                                                 Toast.makeText(context, "Contact saved", Toast.LENGTH_SHORT).show();
                                                             }
                                                          }
                                                      });
                                                  }
                                               }
                                           });
                                         }
                                       }
                                   });
                               }
                            }
                        });
                      }
                  });
                  holder.btnRejectRequest.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          chatRequestReference.child(currentUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if(task.isSuccessful()){
                                      chatRequestReference.child(senderUserId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if(task.isSuccessful()){
                                                  Toast.makeText(context, "Request cancelled", Toast.LENGTH_SHORT).show();
                                              }
                                          }
                                      });
                                  }
                              }
                          });
                      }
                  });
             }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
    }

    @NonNull
    @Override
    public ChatRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.request_item_list,parent,false);
        return new ChatRequestViewHolder(v);
    }

    public class ChatRequestViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civProfile;
        TextView tvName,tvStatus;
        Button btnAcceptRequet,btnRejectRequest;
        public ChatRequestViewHolder(View itemView){
            super(itemView);
            civProfile = itemView.findViewById(R.id.civProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAcceptRequet = itemView.findViewById(R.id.btnAcceptRequest);
            btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);
        }
    }
}
