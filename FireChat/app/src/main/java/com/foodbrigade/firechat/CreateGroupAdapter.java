package com.foodbrigade.firechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroupAdapter extends FirebaseRecyclerAdapter<User, CreateGroupAdapter.CreateGroupViewHolder> {
    ArrayList<User>userlist;
    public CreateGroupAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
        userlist=new ArrayList<>();
    }

    @Override
    protected void onBindViewHolder(@NonNull final CreateGroupViewHolder holder, int i, @NonNull final User user) {
        Picasso.get().load(user.getImage()).placeholder(R.drawable.radio).into(holder.civ);
        holder.tvname.setText(user.getUsername());
        holder.tvstatus.setText(user.getStatus());
        if (user.isCheck()){
            holder.ivcheck.setVisibility(View.VISIBLE);
        }
        else
            holder.ivcheck.setVisibility(View.INVISIBLE);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setCheck(!user.isCheck());
                if (user.isCheck()){
                    holder.ivcheck.setVisibility(View.VISIBLE);
                    userlist.add(user);
                }
                else{
                    holder.ivcheck.setVisibility(View.INVISIBLE);
                    userlist.remove(user);
                }
            }
        });
    }
    public ArrayList<User> getSelectedUserList(){
        return userlist;
    }
    @NonNull
    @Override
    public CreateGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.select_member,parent,false);
        return new CreateGroupViewHolder(v);
    }

    public class CreateGroupViewHolder extends RecyclerView.ViewHolder{
        TextView tvname,tvstatus;
        CircleImageView civ;
        ImageView ivcheck;
        LinearLayout ll;
        public CreateGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname=itemView.findViewById(R.id.nametv);
            tvstatus=itemView.findViewById(R.id.statustv);
            civ=itemView.findViewById(R.id.civ_pro);
            ivcheck=itemView.findViewById(R.id.iv_check);
            ll=itemView.findViewById(R.id.adapter);
        }
    }
}
