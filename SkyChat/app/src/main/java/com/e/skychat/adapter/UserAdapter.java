package com.e.skychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.R;
import com.e.skychat.SendRequestActivity;
import com.e.skychat.beans.User;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends FirebaseRecyclerAdapter<User, UserAdapter.UserViewHolder> {

    Context context;
    public UserAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int i, @NonNull final User user) {

       holder.tvStatus.setText(user.getStatus());
       holder.tvUsername.setText(user.getName());
       Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent in =new Intent(context, SendRequestActivity.class);
               in.putExtra("user",user);
               context.startActivity(in);
           }
       });
        if(user.getState()!=null){
            if(user.getState().equals("online")){
                holder.ivOnlineOfflineState.setVisibility(View.VISIBLE);
                holder.tvLastSeen.setVisibility(View.INVISIBLE);
            }

        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_list_item,parent,false);
        return new UserViewHolder(v);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
      CircleImageView civProfile;
      TextView tvUsername,tvStatus,tvLastSeen;
      ImageView ivOnlineOfflineState;
      public UserViewHolder(@NonNull View itemView) {
          super(itemView);
          civProfile = itemView.findViewById(R.id.civProfile);
          tvStatus = itemView.findViewById(R.id.tvStatus);
          tvUsername = itemView.findViewById(R.id.tvName);
          ivOnlineOfflineState = itemView.findViewById(R.id.ivOnlineOffline);
          tvLastSeen = itemView.findViewById(R.id.tvLastSeen);
      }

    }
}
