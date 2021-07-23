package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.ChatActivity;
import com.foodbrigade.firechat.FindFriendActivity;
import com.foodbrigade.firechat.FriendProfileActivity;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.Setting_Activity;
import com.foodbrigade.firechat.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.security.AccessController.getContext;

public class FindFriendAdapter extends FirebaseRecyclerAdapter<User, FindFriendAdapter.FindFriendViewHolder> {
    Context context;
    public FindFriendAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
        super(options);
        this.context=context;
    }
    @NonNull
    @Override
    public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.friend_view,parent,false);
        return new FindFriendViewHolder(v);
    }
    @Override
    protected void onBindViewHolder(@NonNull FindFriendViewHolder findFriendViewHolder, int i, @NonNull final User user) {
        findFriendViewHolder.tvname.setText(user.getUsername());
        findFriendViewHolder.tvstatus.setText(user.getStatus());
        Picasso.get().load(user.getImage()).placeholder(R.drawable.radio).into(findFriendViewHolder.civ);
        if (user.getState()!=null){
            if (user.getState().equalsIgnoreCase("online")) {
                findFriendViewHolder.iv.setVisibility(View.VISIBLE);
                findFriendViewHolder.tvLastSeen.setVisibility(View.GONE);
            } else if (user.getState().equalsIgnoreCase("offline")){
                findFriendViewHolder.iv.setVisibility(View.GONE);
               findFriendViewHolder.tvLastSeen.setVisibility(View.VISIBLE);
                findFriendViewHolder.tvLastSeen.setText("Last Seen : "+user.getTime()+" "+user.getDate());
            }
        }
        findFriendViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, FriendProfileActivity.class);
                intent.putExtra("key",user);
                context.startActivity(intent);
            }
        });
        findFriendViewHolder.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                androidx.appcompat.app.AlertDialog.Builder ab = new AlertDialog.Builder(context);
                View v=LayoutInflater.from(context).inflate(R.layout.profile_picture,null);
                ab.setView(v);
                ImageView iv=v.findViewById(R.id.iv);
                iv.requestLayout();
                iv.getLayoutParams().height=500;
                iv.getLayoutParams().width=500;
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.get().load(user.getImage()).placeholder(R.drawable.radio).into(iv);
                ab.show();
            }
        });
    }
    public class FindFriendViewHolder extends RecyclerView.ViewHolder{
       CircleImageView civ;
        TextView tvname,tvstatus,tvLastSeen;
        LinearLayout ll;
        ImageView iv;
        public FindFriendViewHolder(@NonNull View v) {
            super(v);
            civ=v.findViewById(R.id.civ_pro);
            tvname=v.findViewById(R.id.tvName);
            tvstatus=v.findViewById(R.id.tvStatus);
            ll=v.findViewById(R.id.lladapter);
            iv=itemView.findViewById(R.id.logoOnline);
            tvLastSeen=itemView.findViewById(R.id.tvLastSeen);
        }
    }
}
