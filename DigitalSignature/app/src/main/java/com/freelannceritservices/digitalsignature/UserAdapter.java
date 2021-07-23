package com.freelannceritservices.digitalsignature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freelannceritservices.digitalsignature.databinding.UserViewBinding;
import com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    Context context;
    ArrayList<User> al;

    public UserAdapter(Context context, ArrayList al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UseradapterviewBinding binding=UseradapterviewBinding.inflate(LayoutInflater.from(context));
        return new UserViewHolder(binding) ;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user=al.get(position);
        holder.binding.name.setText(user.getName());
        holder.binding.colony.setText(user.getId());
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        UseradapterviewBinding binding;
        public UserViewHolder(@NonNull UseradapterviewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
