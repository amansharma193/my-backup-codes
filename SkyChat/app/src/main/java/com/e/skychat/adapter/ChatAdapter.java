package com.e.skychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.e.skychat.R;
import com.e.skychat.beans.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirebaseRecyclerAdapter<User, ChatAdapter.ChatViewHolder> {

    private final View.OnClickListener mOnClickListener = new TestClickListener();

    DatabaseReference userReference,messageCounterReference;
    Context context;
    OnRecyclerViewClick listener;
    String currentUserId;

    public ChatAdapter(@NonNull FirebaseRecyclerOptions<User> options,Context context) {
        super(options);
        this.context =context;
        userReference = FirebaseDatabase.getInstance().getReference("Users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        messageCounterReference = FirebaseDatabase.getInstance().getReference("MessageCounter");
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int i, @NonNull User user) {
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.rotate_demo);
        holder.civProfile.setAnimation(animation);
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
        messageCounterReference.child(currentUserId).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long total = dataSnapshot.getChildrenCount();
                    if(total > 0){
                        holder.btnCounter.setVisibility(View.VISIBLE);
                        holder.btnCounter.setText(""+total);
                    }
                    else
                        holder.btnCounter.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.find_friend_list_item,parent,false);
        v.setOnClickListener(mOnClickListener);
        return new ChatViewHolder(v);
    }

    public class TestClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
           Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

        }
    }
    public class ChatViewHolder extends RecyclerView.ViewHolder{
       CircleImageView civProfile;
       TextView tvStatus,tvName,tvLastSeen;
       ImageView ivOnlineOfflineState;
       Button btnCounter;
       public ChatViewHolder(View itemView){
           super(itemView);

           civProfile = itemView.findViewById(R.id.civProfile);
           tvStatus = itemView.findViewById(R.id.tvStatus);
           tvName = itemView.findViewById(R.id.tvName);
           ivOnlineOfflineState = itemView.findViewById(R.id.ivOnlineOffline);
           tvLastSeen = itemView.findViewById(R.id.tvLastSeen);
           btnCounter = itemView.findViewById(R.id.btnCounter);
           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   int position = getAdapterPosition();
                   if(position != RecyclerView.NO_POSITION && listener!= null){
                       listener.onItemClick(getItem(position),position);
                   }
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
