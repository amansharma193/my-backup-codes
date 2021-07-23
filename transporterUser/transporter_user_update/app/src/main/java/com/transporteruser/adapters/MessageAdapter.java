package com.transporteruser.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.transporteruser.R;
import com.transporteruser.bean.Message;
import com.transporteruser.databinding.ChatListBinding;

import java.util.ArrayList;

public class MessageAdapter extends ArrayAdapter {
    Context context;
    ArrayList<Message> al;
    String currentUser;

    public MessageAdapter(Context context, ArrayList<Message> al) {
        super(context, R.layout.chat_list, al);
        this.context = context;
        this.al = al;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatListBinding binding = ChatListBinding.inflate(LayoutInflater.from(context));
        final Message message = al.get(position);


        if (currentUser.equals(message.getFrom())) {
            binding.received.setVisibility(View.GONE);
            binding.sent.setVisibility(View.VISIBLE);
            binding.tvChatSent.setText(message.getMessage());
        } else {
            binding.received.setVisibility(View.VISIBLE);
            binding.sent.setVisibility(View.GONE);
            binding.tvChatReceived.setText(message.getMessage());
        }

        return binding.getRoot();
    }
}
