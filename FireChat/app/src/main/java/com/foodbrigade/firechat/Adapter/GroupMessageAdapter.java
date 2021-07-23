package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foodbrigade.firechat.Message;
import com.foodbrigade.firechat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.GroupMessageViewHolder> {
    Context context;
    ArrayList<Message> al;
    String userkey;
    DatabaseReference rootReference;
    public GroupMessageAdapter(Context context,ArrayList<Message>al){
        this.context=context;
        this.al=al;
        userkey= FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootReference= FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public GroupMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.group_message_rec_view,parent,false);
        return new GroupMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMessageViewHolder holder, int position) {
        final Message message=al.get(position);
        if(message.getType().equalsIgnoreCase("text")) {
            if (userkey.equalsIgnoreCase(message.getFrom())) {
                holder.frndimage.setVisibility(View.GONE);
                holder.frndmsgtime.setVisibility(View.GONE);
                holder.frndmsg.setVisibility(View.GONE);
                holder.civsendericon.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.GONE);
                holder.mytime.setVisibility(View.VISIBLE);
                holder.mymsg.setVisibility(View.VISIBLE);
                holder.mytime.setText(message.getTime() + " " + message.getDate());
                holder.mymsg.setText(message.getMessage());
            } else {
                holder.civsendericon.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getSendericon()).into(holder.civsendericon);
                holder.frndimage.setVisibility(View.GONE);
                holder.mymsg.setVisibility(View.GONE);
                holder.mytime.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.GONE);
                holder.frndmsgtime.setVisibility(View.VISIBLE);
                holder.frndmsg.setVisibility(View.VISIBLE);
                holder.frndmsgtime.setText(message.getTime() + " " + message.getDate());
                holder.frndmsg.setText(message.getMessage());
            }
        }
        else if (message.getType().equalsIgnoreCase("image")){
            if (userkey.equalsIgnoreCase(message.getFrom())) {
                holder.mytime.setVisibility(View.VISIBLE);
                holder.mytime.setText(message.getTime()+" "+message.getDate());
                holder.mymsg.setVisibility(View.GONE);
                holder.frndmsg.setVisibility(View.GONE);
                holder.frndimage.setVisibility(View.GONE);
                holder.frndmsgtime.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.VISIBLE);
                holder.myimage.requestLayout();
                holder.myimage.getLayoutParams().height=200;
                Picasso.get().load(message.getMessage()).into(holder.myimage);
                holder.myimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });

            } else  {
                holder.civsendericon.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getSendericon()).into(holder.civsendericon);
                holder.frndmsgtime.setVisibility(View.VISIBLE);
                holder.frndmsgtime.setText(message.getTime()+" "+message.getDate());
                holder.mymsg.setVisibility(View.GONE);
                holder.mytime.setVisibility(View.GONE);
                holder.frndmsg.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.GONE);
                holder.frndimage.setVisibility(View.VISIBLE);
                holder.frndimage.requestLayout();
                holder.frndimage.getLayoutParams().height=200;
                Picasso.get().load(message.getMessage()).into(holder.frndimage);
                holder.frndimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }
        }
        else if(message.getType().equalsIgnoreCase("pdf")||message.getType().equalsIgnoreCase("doc")||message.getType().equalsIgnoreCase("docx")) {
            if (userkey.equalsIgnoreCase(message.getFrom())) {
                holder.mytime.setVisibility(View.VISIBLE);
                holder.mytime.setText(message.getTime()+" "+message.getDate());
                holder.mymsg.setVisibility(View.GONE);
                holder.frndmsg.setVisibility(View.GONE);
                holder.frndimage.setVisibility(View.GONE);
                holder.frndmsgtime.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.VISIBLE);
                holder.myimage.requestLayout();
                holder.myimage.getLayoutParams().width=200;
                holder.myimage.getLayoutParams().height=200;
                if (message.getType().equalsIgnoreCase("pdf"))
                    holder.myimage.setImageResource(R.drawable.ic_pdf_flat);
                else if (message.getType().equalsIgnoreCase("doc")||message.getType().equalsIgnoreCase("docx"))
                    holder.myimage.setImageResource(R.drawable.ic_doc_flat);
                holder.myimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            } else {
                holder.civsendericon.setVisibility(View.VISIBLE);
                Picasso.get().load(message.getSendericon()).into(holder.civsendericon);
                holder.frndmsgtime.setVisibility(View.VISIBLE);
                holder.frndmsgtime.setText(message.getTime()+" "+message.getDate());
                holder.mymsg.setVisibility(View.GONE);
                holder.mytime.setVisibility(View.GONE);
                holder.frndmsg.setVisibility(View.GONE);
                holder.myimage.setVisibility(View.GONE);
                holder.frndimage.setVisibility(View.VISIBLE);
                holder.frndimage.requestLayout();
                holder.frndimage.getLayoutParams().width=200;
                holder.frndimage.getLayoutParams().height=200;
                if (message.getType().equalsIgnoreCase("pdf"))
                    holder.frndimage.setImageResource(R.drawable.ic_pdf_flat);
                else if (message.getType().equalsIgnoreCase("doc")||message.getType().equalsIgnoreCase("docx"))
                    holder.frndimage.setImageResource(R.drawable.ic_doc_flat);
                holder.frndimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class GroupMessageViewHolder extends RecyclerView.ViewHolder{
        TextView frndmsg,mytime,mymsg,frndmsgtime;
        ImageView frndimage,myimage;
        CircleImageView civsendericon;
        public GroupMessageViewHolder(@NonNull View v) {
            super(v);
            frndmsg=v.findViewById(R.id.friendMsg);
            mytime=v.findViewById(R.id.MyMsgTime);
            mymsg=v.findViewById(R.id.MyMsg);
            frndmsgtime=v.findViewById(R.id.frndmsgtime);
            frndimage=v.findViewById(R.id.frndmsgimage);
            myimage=v.findViewById(R.id.mymsgimage);
            civsendericon=v.findViewById(R.id.sendericon);
        }
    }
}
