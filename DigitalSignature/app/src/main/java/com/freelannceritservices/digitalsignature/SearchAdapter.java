package com.freelannceritservices.digitalsignature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.freelannceritservices.digitalsignature.databinding.UseradapterviewBinding;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    ArrayList<Family>al;
    Context context;

    public SearchAdapter(ArrayList<Family> al, Context context) {
        this.al = al;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UseradapterviewBinding binding=UseradapterviewBinding.inflate(LayoutInflater.from(context));
        return new SearchViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Family family=al.get(position);
        holder.binding.name.setText(family.getName());
        holder.binding.colony.setText(family.getAddress());
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        UseradapterviewBinding binding;
        public SearchViewHolder(@NonNull UseradapterviewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
