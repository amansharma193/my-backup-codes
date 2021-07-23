package com.e.skychat.adapter;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupAdapter extends FirebaseRecyclerAdapter<User, CreateGroupAdapter.CreateGroupViewHolder> {

    ArrayList<User>selectedUserList;
    public CreateGroupAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
        selectedUserList = new ArrayList<User>();
    }

    @Override
    protected void onBindViewHolder(@NonNull final CreateGroupViewHolder holder, int i, @NonNull final User user) {
        Picasso.get().load(user.getImage()).placeholder(R.drawable.firechaticon).into(holder.civProfile);
        holder.tvName.setText(user.getName());
        holder.tvStatus.setText(user.getStatus());
        if(user.isChecked()){
            holder.ivSelected.setVisibility(View.VISIBLE);
        }
        else
            holder.ivSelected.setVisibility(View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setChecked(!user.isChecked());
                if(user.isChecked()) {
                    holder.ivSelected.setVisibility(View.VISIBLE);
                    selectedUserList.add(user);
                }
                else {
                    holder.ivSelected.setVisibility(View.GONE);
                    selectedUserList.remove(user);
                }
            }
        });

    }

    @NonNull
    @Override
    public CreateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_group_item_list,parent,false);
        return new CreateGroupViewHolder(v);
    }


    public class CreateGroupViewHolder extends RecyclerView.ViewHolder{
      CircleImageView civProfile;
      TextView tvName,tvStatus;
      ImageView ivSelected;
      public CreateGroupViewHolder(View itemView){
          super(itemView);
          civProfile = itemView.findViewById(R.id.civProfile);
          tvName = itemView.findViewById(R.id.tvName);
          tvStatus = itemView.findViewById(R.id.tvStatus);
          ivSelected = itemView.findViewById(R.id.ivSelected);
      }
  }
  public ArrayList<User> getSelectedUserList(){
        return selectedUserList;
  }

}
