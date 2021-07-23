package com.e.skychat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e.skychat.R;
import com.e.skychat.beans.Message;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupMemberMessageAdapter extends ArrayAdapter {
  Context context;
  ArrayList<Message>al;
  public GroupMemberMessageAdapter(Context context, ArrayList<Message> al){
      super(context, R.layout.group_member_message_item_list,al);
      this.context = context;
      this.al = al;
  }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
      Message message = al.get(position);
      View v = LayoutInflater.from(context).inflate(R.layout.group_member_message_item_list,null);
      TextView tvDateTime = v.findViewById(R.id.tvDateTime);
      TextView tvMessage = v.findViewById(R.id.tvMessage);
      ImageView ivMessage = v.findViewById(R.id.ivMessage);
      tvDateTime.setText(message.getDate()+" "+message.getTime());

      if(message.getType().equals("text")){
          tvMessage.setVisibility(View.VISIBLE);
          tvMessage.setText(message.getMessage());
      }
      else if(message.getType().equals("image")){
        ivMessage.setVisibility(View.VISIBLE);
          Picasso.get().load(message.getMessage()).placeholder(R.drawable.firechaticon).into(ivMessage);
      }
      else if(message.getType().equals("pdf") || message.getType().equals("docx")){
         ivMessage.setVisibility(View.VISIBLE);
          if(message.getType().equals("pdf"))
           ivMessage.setImageResource(R.drawable.pdf);
          else if(message.getType().equals("docx"))
              ivMessage.setImageResource(R.drawable.word_file);
      }
      return v;
    }
}
