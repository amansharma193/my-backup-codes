package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestAdapter extends FirebaseRecyclerAdapter<User,FriendRequestAdapter.FriendRequestViewHolder> {
    Context context;
    DatabaseReference rootreference;
    FirebaseAuth mAuth;
    String userkey;
    public FriendRequestAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
        super(options);
        this.context=context;
        mAuth=FirebaseAuth.getInstance();
        userkey=mAuth.getCurrentUser().getUid();
        rootreference= FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.request_rec_view,parent,false);
        return new FriendRequestViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull final FriendRequestViewHolder holder, int i, @NonNull final User user) {
       String s= getRef(i).getKey();
       rootreference.child("Users").child(s).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()) {
                  final User u = dataSnapshot.getValue(User.class);
                  holder.name.setText(u.getUsername());
                  holder.status.setText(u.getStatus());
                  Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(holder.civ);
                  holder.accept.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          rootreference.child("Contacts").child(userkey).child(u.getKey()).child("Contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()) {
                                      rootreference.child("Contacts").child(userkey).child(u.getKey()).child("key").setValue(u.getKey()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()) {
                                                  rootreference.child("Contacts").child(u.getKey()).child(userkey).child("Contact").setValue("saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                      @Override
                                                      public void onComplete(@NonNull Task<Void> task) {
                                                          if (task.isSuccessful()) {
                                                              rootreference.child("Contacts").child(u.getKey()).child(userkey).child("key").setValue(userkey).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                  @Override
                                                                  public void onComplete(@NonNull Task<Void> task) {
                                                                      rootreference.child("Chat Request").child(userkey).child(u.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                          @Override
                                                                          public void onComplete(@NonNull Task<Void> task) {
                                                                              if (task.isSuccessful()) {
                                                                                  rootreference.child("Chat Request").child(u.getKey()).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                      @Override
                                                                                      public void onComplete(@NonNull Task<Void> task) {
                                                                                          if (task.isSuccessful()) {
                                                                                              Toast.makeText(context, "contact saved.", Toast.LENGTH_SHORT).show();
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
                                                  });
                                              }
                                          }
                                      });
                                  }
                              }
                          });
                      }
                  });
                  holder.reject.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                          rootreference.child("Chat Request").child(userkey).child(u.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                  if (task.isSuccessful()) {
                                      rootreference.child("Chat Request").child(u.getKey()).child(userkey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task) {
                                              if (!task.isSuccessful()) {
                                                  Toast.makeText(context, task.getException().toString(), Toast.LENGTH_SHORT).show();
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
    public class FriendRequestViewHolder extends RecyclerView.ViewHolder{
        TextView name,status;
        CircleImageView civ;
        Button accept,reject;
        LinearLayout ll;
        public FriendRequestViewHolder(@NonNull View v) {
            super(v);
            name=v.findViewById(R.id.reqname);
            status=v.findViewById(R.id.reqStatus);
            civ=v.findViewById(R.id.civ_req);
            accept=v.findViewById(R.id.btnacpt);
            reject=v.findViewById(R.id.btnrjct);
            ll=v.findViewById(R.id.llreq);
        }
    }
}
