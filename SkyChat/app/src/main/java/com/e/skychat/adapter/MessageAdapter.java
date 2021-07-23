package com.e.skychat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.skychat.R;
import com.e.skychat.beans.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageAdapter  extends ArrayAdapter {
  Context context;
  ArrayList<Message>al;
  String currentUserId;
   public MessageAdapter(Context context, ArrayList<Message>al){
       super(context,R.layout.chat_message_item_list,al);
       this.context = context;
       this.al = al;
       currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
   }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       View v = LayoutInflater.from(context).inflate(R.layout.chat_message_item_list,null);
        RelativeLayout leftRl = v.findViewById(R.id.leftRL);
        RelativeLayout rightRl = v.findViewById(R.id.rightRL);
        TextView tvFriendMessageTime = v.findViewById(R.id.tvFriendMessageTime);
        TextView tvMyMessageTime = v.findViewById(R.id.tvMyMessageTime);
        TextView tvFriendMessage = v.findViewById(R.id.tvFriendMessage);
        TextView tvMyMessage = v.findViewById(R.id.tvMyMessage);
        ImageView ivLeft = v.findViewById(R.id.ivLeft);
        ImageView ivRight = v.findViewById(R.id.ivRight);

        final Message message = al.get(position);
        if(message.getType().equals("text")){
            if(currentUserId.equals(message.getFrom())){
               leftRl.setVisibility(View.GONE);
               rightRl.setVisibility(View.VISIBLE);
               tvMyMessageTime.setText(message.getDate()+" "+message.getTime());
               tvMyMessage.setText(message.getMessage());
            }
            else{
               rightRl.setVisibility(View.GONE);
               leftRl.setVisibility(View.VISIBLE);
               tvFriendMessageTime.setText(message.getDate()+" "+message.getTime());
               tvFriendMessage.setText(message.getMessage());
            }
        }
        else if(message.getType().equals("image")){
            if(currentUserId.equals(message.getFrom())){
               leftRl.setVisibility(View.GONE);
               rightRl.setVisibility(View.VISIBLE);
               ivRight.getLayoutParams().height = 600;
               ivRight.getLayoutParams().width =600;
               Picasso.get().load(message.getMessage()).into(ivRight);
               tvMyMessage.setVisibility(View.GONE);
               tvMyMessageTime.setText(message.getDate()+" "+message.getTime());
               tvMyMessageTime.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
               tvMyMessageTime.setTextColor(context.getResources().getColor(R.color.actionBarGreen));
            }
            else{
               rightRl.setVisibility(View.GONE);
               leftRl.setVisibility(View.VISIBLE);
               ivLeft.getLayoutParams().height = 600;
               ivLeft.getLayoutParams().width = 600;
               tvFriendMessage.setVisibility(View.GONE);
               tvFriendMessageTime.setText(message.getDate()+" "+message.getTime());
               Picasso.get().load(message.getMessage()).into(ivLeft);
               tvFriendMessageTime.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
               tvFriendMessageTime.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        else if(message.getType().equals("pdf") || message.getType().equals("docx")){
            if(currentUserId.equals(message.getFrom())){
                leftRl.setVisibility(View.GONE);
                rightRl.setVisibility(View.VISIBLE);
                ivRight.getLayoutParams().height = 300;
                ivRight.getLayoutParams().width =300;

                if(message.getType().equals("pdf"))
                    ivRight.setBackgroundResource(R.drawable.pdf);
                else if(message.getType().equals("docx"))
                    ivRight.setBackgroundResource(R.drawable.word_file);

                tvMyMessage.setVisibility(View.GONE);
                tvMyMessageTime.setText(message.getDate()+" "+message.getTime());
                tvMyMessageTime.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
                tvMyMessageTime.setTextColor(context.getResources().getColor(R.color.actionBarGreen));

                ivRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }
            else{
                rightRl.setVisibility(View.GONE);
                leftRl.setVisibility(View.VISIBLE);
                ivLeft.getLayoutParams().height = 300;
                ivLeft.getLayoutParams().width = 300;

                if(message.getType().equals("pdf"))
                    ivLeft.setBackgroundResource(R.drawable.pdf);
                else if(message.getType().equals("docx"))
                    ivLeft.setBackgroundResource(R.drawable.word_file);


                tvFriendMessage.setVisibility(View.GONE);

                tvFriendMessageTime.setText(message.getDate()+" "+message.getTime());
                Picasso.get().load(message.getMessage()).into(ivLeft);
                tvFriendMessageTime.setBackground(new ColorDrawable(context.getResources().getColor(R.color.white)));
                tvFriendMessageTime.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));


                ivLeft.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getMessage()));
                        context.startActivity(in);
                    }
                });
            }
        }
        return v;
    }
}
