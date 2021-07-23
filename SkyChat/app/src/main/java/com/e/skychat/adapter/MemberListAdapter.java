package com.e.skychat.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.R;
import com.e.skychat.beans.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberListAdapter extends FirebaseRecyclerAdapter<User, MemberListAdapter.MemberListViewHolder> {
    String currentUserId;
    OnRecyclerViewClick listener;
    public MemberListAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull MemberListViewHolder holder, int i, @NonNull User user) {
        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
        holder.tvMemberName.setText(user.getName());
        holder.tvMemberStatus.setText(user.getStatus());
        if(currentUserId.equals(user.getUid())){
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(0,0);
            holder.itemView.setLayoutParams(params);
            holder.itemView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public MemberListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_list_item,parent,false);
        return new MemberListViewHolder(v);
    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder{
       CircleImageView civProfile;
       TextView tvMemberName,tvMemberStatus;
       LinearLayout childLinearLayout;
       public MemberListViewHolder(View itemView){
           super(itemView);
           civProfile = itemView.findViewById(R.id.civProfile);
           tvMemberName = itemView.findViewById(R.id.tvName);
           tvMemberStatus = itemView.findViewById(R.id.tvStatus);
           //childLinearLayout = itemView.findViewById(R.id.childLinearLayout);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                  int poistion = getAdapterPosition();
                  User user = getItem(poistion);
                  if(poistion != RecyclerView.NO_POSITION && listener !=null)
                      listener.onItemClick(user,poistion);
               }
           });
       }
   }
   public interface OnRecyclerViewClick{
        void onItemClick(User user, int position);
   }
   public void setOnItemClick(OnRecyclerViewClick listener){
        this.listener = listener;
   }
}
