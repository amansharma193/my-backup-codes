package com.e.skychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.e.skychat.R;
import com.e.skychat.beans.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendStoryListAdapter extends RecyclerView.Adapter<FriendStoryListAdapter.FriendStoryListViewHolder> {
    Context context;
    ArrayList<User>al;
    OnRecyclerViewClick listener;
    public FriendStoryListAdapter(Context context, ArrayList<User>al){
        this.context = context;
        this.al = al;
    }
    @NonNull
    @Override
    public FriendStoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend_story_list,parent,false);
        return new FriendStoryListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendStoryListViewHolder holder, int position) {
      User user = al.get(position);
        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
        holder.tvName.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class FriendStoryListViewHolder extends RecyclerView.ViewHolder{
      CircleImageView civProfile;
      TextView tvName;
      public FriendStoryListViewHolder(View itemView){
          super(itemView);
          civProfile = itemView.findViewById(R.id.civProfile);
          tvName = itemView.findViewById(R.id.tvName);
          itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  int position = getAdapterPosition();
                  User user = al.get(position);
                  if(position != RecyclerView.NO_POSITION && listener != null){
                      listener.onItemClick(user,position);
                  }
              }
          });
      }
  }
  public interface OnRecyclerViewClick{
        void onItemClick(User user, int postion);
  }
  public void setOnItemClickListener(OnRecyclerViewClick listener){
        this.listener = listener;
  }
}
