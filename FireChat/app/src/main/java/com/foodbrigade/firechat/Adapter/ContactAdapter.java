package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.foodbrigade.firechat.R;
import com.foodbrigade.firechat.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends FirebaseRecyclerAdapter<User, ContactAdapter.ContactViewHolder> {
    DatabaseReference rootReference;
    String userkey,friend;
    Context context;
    protected static ArrayList<String>al;
    public ContactAdapter(@NonNull FirebaseRecyclerOptions<User> options, Context context) {
        super(options);
        this.context=context;
        rootReference= FirebaseDatabase.getInstance().getReference();
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ContactViewHolder holder,final int i, @NonNull  User u) {
    if (i==0){
        al=new ArrayList<String>();
    }
        holder.tvname.setText(u.getUsername());
        holder.tvstatus.setText(u.getStatus());
        Picasso.get().load(u.getImage()).placeholder(R.drawable.radio).into(holder.civ);
        if (u.getState()!=null) {
            if (u.getState().equalsIgnoreCase("online")) {
                holder.iv.setVisibility(View.VISIBLE);
                holder.tvlastSeen.setVisibility(View.GONE);
            } else if (u.getState().equalsIgnoreCase("offline")) {
                holder.iv.setVisibility(View.INVISIBLE);
                holder.tvlastSeen.setVisibility(View.VISIBLE);
                holder.tvlastSeen.setText("Last Seen : " + u.getTime() + " " + u.getDate());
            }
        }
        if (i<=al.size()) {
            al.add(i, u.getKey());
        }
    }
    public String getkey(int pois){
        return al.get(pois);
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.friend_view,parent,false);
        return new ContactViewHolder(v);
    }
    public class ContactViewHolder extends RecyclerView.ViewHolder{
        CircleImageView civ;
        LinearLayout ll;
        ImageView iv;
        TextView tvname,tvstatus,tvlastSeen;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            civ=itemView.findViewById(R.id.civ_pro);
            tvname=itemView.findViewById(R.id.tvName);
            tvstatus=itemView.findViewById(R.id.tvStatus);
            iv=itemView.findViewById(R.id.logoOnline);
            tvlastSeen=itemView.findViewById(R.id.tvLastSeen);
        }
    }
}
