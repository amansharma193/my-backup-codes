package com.foodbrigade.firechat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foodbrigade.firechat.Message;
import com.foodbrigade.firechat.R;
import com.squareup.picasso.Picasso;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class MemberMessagaAdapter extends ArrayAdapter<ArrayList<Message>> {
    Context context;
    ArrayList<Message>al;
    public MemberMessagaAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Message>al) {
        super(context,resource);
        this.al=al;
        this.context=context;
    }

    @Override
    public int getCount() {
        return al.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Message message=al.get(position);
        View v= LayoutInflater.from(context).inflate(R.layout.member_message_list,parent,false);
        TextView tvtime=v.findViewById(R.id.tvtime);
        tvtime.setText(message.getTime()+" "+ message.getDate());
        TextView tvmessage=v.findViewById(R.id.tvmsg);
        ImageView image=v.findViewById(R.id.ivmsg);
        if (message.getType().equals("image")){
            image.setVisibility(View.VISIBLE);
            Picasso.get().load(message.getMessage()).into(image);
        }
        else if (message.getType().equals("text")){
            tvmessage.setVisibility(View.VISIBLE);
            tvmessage.setText(message.getMessage());
        }
        else if (message.getType().equals("pdf")){
            image.setVisibility(View.VISIBLE);
            image.requestLayout();
            image.getLayoutParams().height=200;
            image.getLayoutParams().width=200;
            image.setImageResource(R.drawable.ic_pdf_flat);
        }
        else if (message.getType().equals("docx")){
            image.setVisibility(View.VISIBLE);
            image.requestLayout();
            image.getLayoutParams().height=200;
            image.getLayoutParams().width=200;
            image.setImageResource(R.drawable.ic_pdf_flat);
        }
        return v;
    }
}
