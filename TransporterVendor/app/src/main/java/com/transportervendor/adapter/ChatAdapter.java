package com.transportervendor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.beans.Message;
import com.transportervendor.databinding.ChatActivityBinding;
import com.transportervendor.databinding.MessageViewBinding;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    Context context;
    ArrayList<Message>al;

    public ChatAdapter(Context context, ArrayList<Message> al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageViewBinding binding=MessageViewBinding.inflate(LayoutInflater.from(context));
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message=al.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equalsIgnoreCase(message.getFrom())){
            holder.binding.ll1.setVisibility(View.GONE);
            holder.binding.senderTime.setVisibility(View.GONE);
            holder.binding.senderMsg.setVisibility(View.GONE);
            SimpleDateFormat sd=new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            holder.binding.myTime.setText(sd.format(message.getTimeStamp()));
            holder.binding.myMsg.setText(message.getMessage());
        }else{
            holder.binding.ll2.setVisibility(View.GONE);
            holder.binding.myMsg.setVisibility(View.GONE);
            SimpleDateFormat sd=new SimpleDateFormat("MMM dd, yyyy hh:mm a");
            holder.binding.myTime.setVisibility(View.GONE);
            holder.binding.senderTime.setText(sd.format(message.getTimeStamp()));
            holder.binding.senderMsg.setText(message.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        MessageViewBinding binding;
        public ChatViewHolder(@NonNull MessageViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
