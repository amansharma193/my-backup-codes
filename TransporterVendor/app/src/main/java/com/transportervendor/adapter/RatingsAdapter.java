package com.transportervendor.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.transportervendor.R;
import com.transportervendor.beans.Rating;
import com.transportervendor.databinding.ReviewsViewBinding;

import java.util.ArrayList;

public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.RatingViewHolder> {
    Context context;
    ArrayList<Rating>al;

    public RatingsAdapter(Context context, ArrayList<Rating> al) {
        this.context = context;
        this.al = al;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewsViewBinding binding=ReviewsViewBinding.inflate(LayoutInflater.from(context),parent,false);
        return new RatingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating rating=al.get(position);
        holder.binding.name.setText(rating.getUserName());
        holder.binding.review.setText(rating.getFeedback());
        float f=Float.parseFloat(rating.getRating());
        Picasso.get().load(rating.getImageUrl()).placeholder(R.drawable.user).into(holder.binding.civ);
        holder.binding.ratingBar.setRating(f);
        if (rating.getFeedback().length()>=65){
            holder.binding.review.setSingleLine(false);
        }else{
            holder.binding.review.setSingleLine(true);
        }
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    class RatingViewHolder extends RecyclerView.ViewHolder{
        ReviewsViewBinding binding;
        public RatingViewHolder(@NonNull ReviewsViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
