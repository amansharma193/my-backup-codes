package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.ChatActivity;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.Setting_Activity;
import com.foodbrigade.firechat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirebaseRecyclerAdapter<User, ChatAdapter.ChatViewHolder>  {
    Context context;
    DatabaseReference rootReference;
    String userkey;
//    OnRecyclerViewClick listener;
    public ChatAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context=context;
        rootReference= FirebaseDatabase.getInstance().getReference();
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int i, @NonNull final User u) {
        holder.tvname.setText(u.getUsername());
        holder.tvstatus.setText(u.getStatus());
        rootReference.child("Message Counter").child(userkey).child(u.getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    long count=snapshot.getChildrenCount();
                    holder.messageCount.setVisibility(View.VISIBLE);
                    if (count>1000){
                        holder.messageCount.setText("999+");
                    }
                    else{
                        holder.messageCount.setText(""+count);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (u.getState()!=null){
            if (u.getState().equalsIgnoreCase("online")) {
                holder.iv.setVisibility(View.VISIBLE);
                holder.tvLastSeen.setVisibility(View.GONE);
            } else if (u.getState().equalsIgnoreCase("offline")){
                holder.iv.setVisibility(View.GONE);
                holder.tvLastSeen.setVisibility(View.VISIBLE);
                holder.tvLastSeen.setText("Last Seen : "+u.getTime()+" "+u.getDate());
            }
        }
        Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(holder.civ);
        holder.civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab = new AlertDialog.Builder(context);
                View v=LayoutInflater.from(context).inflate(R.layout.profile_picture,null);
                ab.setView(v);
                ImageView iv=v.findViewById(R.id.iv);
                iv.requestLayout();
                iv.getLayoutParams().height=500;
                iv.getLayoutParams().width=500;
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(iv);
                ab.show();
            }
        });
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("obk",u);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.friend_view,parent,false);
        return new ChatViewHolder(v);
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ;
        TextView tvname,tvstatus,tvLastSeen;
        LinearLayout ll;
        ImageView iv;
        Button messageCount;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            civ=itemView.findViewById(R.id.civ_pro);
            ll=itemView.findViewById(R.id.lladapter);
            tvname=itemView.findViewById(R.id.tvName);
            tvstatus=itemView.findViewById(R.id.tvStatus);
            iv=itemView.findViewById(R.id.logoOnline);
            tvLastSeen=itemView.findViewById(R.id.tvLastSeen);
            messageCount=itemView.findViewById(R.id.messagecounter);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    int position=getAdapterPosition();
//                    if (position!=RecyclerView.NO_POSITION && listener!=null){
//
//                    }
//                }
//            });
        }
    }
//    public interface OnRecyclerViewClick{
//        void onItemClick(User user,int position);
//    }
//    public void setOnItemClick(OnRecyclerViewClick listener){
//        this.listener=listener;
//    }
}
