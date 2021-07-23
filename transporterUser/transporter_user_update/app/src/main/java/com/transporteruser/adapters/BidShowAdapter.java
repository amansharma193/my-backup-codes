package com.transporteruser.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.BidShowBinding;

import java.util.ArrayList;

public class BidShowAdapter extends RecyclerView.Adapter<BidShowAdapter.BidsViewHolder> {
    Lead lead;
    ArrayList<Bid> bidList;
    OnRecycleViewClickListener listener;

    public BidShowAdapter(ArrayList<Bid> bidList,Lead lead) {
        this.bidList = bidList;
        this.lead = lead;
    }

    @NonNull
    @Override
    public BidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BidShowBinding binding = BidShowBinding.inflate(LayoutInflater.from(parent.getContext()));
        BidsViewHolder bidsViewHolder = new BidsViewHolder(binding);
        return bidsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BidsViewHolder holder, int position) {

        Bid bid = bidList.get(position);
        holder.binding.tvDate.setText(bid.getEstimatedDate());
        holder.binding.tvMaterialType.setText(lead.getTypeOfMaterial());
        holder.binding.tvQuntity.setText(lead.getWeight());
        holder.binding.tvPickUpContact.setText(lead.getContactForPickup());
        holder.binding.tvDeliveryContact.setText(lead.getContactForDelivery());
    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }


    public class BidsViewHolder extends RecyclerView.ViewHolder {
        BidShowBinding binding;

        public BidsViewHolder(BidShowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onClickListener(bidList.get(position), position);
                    }
                }
            });
        }
    }

    public interface OnRecycleViewClickListener {
        public void onClickListener(Bid bid, int position);

    }

    public void onBidShowClickListener(OnRecycleViewClickListener listener) {
        this.listener = listener;
    }


}
