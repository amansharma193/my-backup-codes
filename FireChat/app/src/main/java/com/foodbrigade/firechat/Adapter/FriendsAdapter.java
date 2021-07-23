package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.foodbrigade.firechat.ShowStory;
import com.foodbrigade.firechat.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FriendsAdapter extends FirebaseRecyclerAdapter<User, FriendsAdapter.FriendsViewHolder> {
    DatabaseReference rootReference;
    Context context;
    public FriendsAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context=context;
        rootReference= FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int i, @NonNull final User user) {
        rootReference.child("Stories").child(user.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int count=(int)snapshot.getChildrenCount();
                    Picasso.get().load(user.getImage()).into(holder.civ);
                    holder.tvname.setText(user.getUsername());
                    holder.tvstatus.setText(count+" stories");
                    holder.ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent in=new Intent(context, ShowStory.class);
                            in.putExtra("user",user.getKey());
                            context.startActivity(in);
                        }
                    });
                }else{
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

    @NonNull
    @Override
    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.select_member,parent,false);
        return new FriendsViewHolder(v);
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll,ll1;
        CircleImageView civ;
        TextView tvname,tvstatus;
        ImageView ivcheck;
        View vew;
        public FriendsViewHolder(@NonNull View v) {
            super(v);
            ll=v.findViewById(R.id.adapter);
            ll1=v.findViewById(R.id.ll1);
            civ=v.findViewById(R.id.civ_pro);
            tvname=v.findViewById(R.id.nametv);
            tvstatus=v.findViewById(R.id.statustv);
            ivcheck=v.findViewById(R.id.iv_check);
            vew=v.findViewById(R.id.vew);
        }
    }
}
