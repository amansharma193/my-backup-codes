package com.foodbrigade.firechat.Adapter;

import android.content.Context;
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
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMemberAdapter extends FirebaseRecyclerAdapter<User, AddMemberAdapter.AddMemberViewHolder> {
    Context context;
    ArrayList<User>userlist;
    DatabaseReference rootReference;
    String groupkey;
    public AddMemberAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context,String groupkey) {
        super(options);
        this.context=context;
        userlist=new ArrayList<>();
        this.groupkey=groupkey;
        rootReference= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull final AddMemberViewHolder holder, int i, @NonNull final User user) {
        rootReference.child("Group").child(groupkey).child(user.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    holder.tvname.setText(user.getUsername());
                    holder.tvstatus.setText(user.getStatus());
                    Picasso.get().load(user.getImage()).into(holder.civ);
                    if (user.isCheck()){
                        holder.ivcheck.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.ivcheck.setVisibility(View.INVISIBLE);
                    }
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
                else{
                    holder.ll.setVisibility(View.GONE);
                    holder.civ.setVisibility(View.GONE);
                    holder.tvname.setVisibility(View.GONE);
                    holder.ivcheck.setVisibility(View.GONE);
                    holder.ll1.setVisibility(View.GONE);
                    holder.vew.setVisibility(View.GONE);
                    holder.tvstatus.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public ArrayList<User> getSelectedUserList(){
        return userlist;
    }
    @NonNull
    @Override
    public AddMemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.select_member,parent,false);
        return new AddMemberViewHolder(v);
    }

    public class AddMemberViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ;
        TextView tvname,tvstatus;
        LinearLayout ll,ll1;
        ImageView ivcheck;
        View vew;
        public AddMemberViewHolder(@NonNull View v) {
            super(v);
            civ=v.findViewById(R.id.civ_pro);
            ll=v.findViewById(R.id.adapter);
            tvname=v.findViewById(R.id.nametv);
            tvstatus=v.findViewById(R.id.statustv);
            ivcheck=v.findViewById(R.id.iv_check);
            vew=v.findViewById(R.id.vew);
            ll1=v.findViewById(R.id.ll1);
        }
    }
}
