package com.freelannceritservices.digitalsignature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding;

import java.util.ArrayList;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.PublicViewHolder> {
    Context context;
    ArrayList<Family> al;

    public PublicAdapter(Context context, ArrayList al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public PublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UseradapterviewBinding binding=UseradapterviewBinding.inflate(LayoutInflater.from(context));
        return new PublicViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicViewHolder holder, int position) {
        Family family=al.get(position);
        holder.binding.name.setText(family.getName());
        holder.binding.colony.setText(family.getAddress());
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class PublicViewHolder extends RecyclerView.ViewHolder {
        UseradapterviewBinding binding;
        public PublicViewHolder(@NonNull UseradapterviewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
