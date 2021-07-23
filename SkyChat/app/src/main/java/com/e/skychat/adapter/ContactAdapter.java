package com.e.skychat.adapter;

import android.content.Context;
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
import com.e.skychat.beans.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends FirebaseRecyclerAdapter<User, ContactAdapter.ContactViewHolder> {
    DatabaseReference userReference;
    Context context;
    public ContactAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
        super(options);
        this.context =context;
        userReference = FirebaseDatabase.getInstance().getReference("Users");
    }

    @Override
    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int i, @NonNull User user) {
        holder.tvName.setText(user.getName());
        holder.tvStatus.setText(user.getStatus());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
        if(user.getState()!=null){
            if(user.getState().equals("online")){
                holder.ivOnlineOfflineState.setVisibility(View.VISIBLE);
                holder.tvLastSeen.setVisibility(View.INVISIBLE);
            }
            else if(user.getState().equals("offline")){
                holder.ivOnlineOfflineState.setVisibility(View.INVISIBLE);
                holder.tvLastSeen.setVisibility(View.VISIBLE);
                holder.tvLastSeen.setText("Last seen : "+user.getDate()+" "+user.getTime());
                holder.tvLastSeen.setTextColor(context.getResources().getColor(R.color.actionBarGreen));
            }
        }
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.find_friend_list_item,parent,false);
        return new ContactViewHolder(v);
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder{
       CircleImageView civProfile;
       TextView tvStatus,tvName,tvLastSeen;
       ImageView ivOnlineOfflineState;

       public ContactViewHolder(View itemView){
           super(itemView);
           civProfile = itemView.findViewById(R.id.civProfile);
           tvStatus = itemView.findViewById(R.id.tvStatus);
           tvName = itemView.findViewById(R.id.tvName);
           ivOnlineOfflineState = itemView.findViewById(R.id.ivOnlineOffline);
           tvLastSeen = itemView.findViewById(R.id.tvLastSeen);
       }
   }
}
