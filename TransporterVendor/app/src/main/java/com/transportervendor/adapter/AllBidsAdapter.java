package com.transportervendor.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.BidInfoActivity;
import com.transportervendor.BidNowActivity;
import com.transportervendor.R;
import com.transportervendor.apis.*;
import com.transportervendor.beans.Bid;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;
import com.transportervendor.databinding.AllBidViewBinding;
import com.transportervendor.databinding.AllBidViewHindiBinding;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class AllBidsAdapter extends RecyclerView.Adapter<AllBidsAdapter.AllBidsViewHolder> {
    Context context;
    ArrayList<BidWithLead>al;
    public AllBidsAdapter(Context context, ArrayList<BidWithLead>al) {
        this.context=context;
        this.al=al;
    }

    @NonNull
    @Override
    public AllBidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (checkLanguage()){
            AllBidViewHindiBinding binding1=AllBidViewHindiBinding.inflate(LayoutInflater.from(context));
            return new AllBidsViewHolder(binding1);
        }
        AllBidViewBinding binding=AllBidViewBinding.inflate(LayoutInflater.from(context));
        return new AllBidsViewHolder(binding);
    }
    private boolean checkLanguage() {
        SharedPreferences mprefs =context.getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
    @Override
    public void onBindViewHolder(@NonNull final AllBidsViewHolder holder, final int position) {
        if (checkLanguage()){
            final BidWithLead bid=al.get(position);
            Leads leads=bid.getLead();
            holder.binding1.material.setText(leads.getTypeOfMaterial());
            if(checkLanguage()){
                holder.binding1.weight.setText(leads.getWeight()+" टन ");
            }else
                holder.binding1.weight.setText(leads.getWeight()+" ton");
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding1.location.setText(name);
            final PopupMenu popup = new PopupMenu(context, holder.binding1.more);
            Menu menu=popup.getMenu();
            menu.add("Edit");
            menu.add("Delete");
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String title=item.getTitle().toString();
                    if (title.equalsIgnoreCase("Edit")){
                        if (bid.getLead().getStatus().equals("") || (bid.getLead().getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && bid.getLead().getStatus().equalsIgnoreCase("confirmed"))){
                            Intent in = new Intent(context, BidInfoActivity.class);
                            in.putExtra("leads", bid);
                            context.startActivity(in);
                        }else if (bid.getLead().getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && (!bid.getLead().getStatus().equalsIgnoreCase("confirmed"))){
                            Toast.makeText(context, "you can't edit this bid.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "this bid is rejected.", Toast.LENGTH_SHORT).show();
                        }
                    }else if(title.equalsIgnoreCase("Delete")){
                        if (bid.getLead().getStatus().equalsIgnoreCase("") || !(bid.getLead().getDealLockedWith().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                            BidService.BidApi bidApi=BidService.getBidApiInstance();
                            Call<Bid>call=bidApi.deleteBid(bid.getBid().getBidId());
                            call.enqueue(new Callback<Bid>() {
                                @Override
                                public void onResponse(Call<Bid> call, Response<Bid> response) {
                                    if (response.isSuccessful()){
                                        Toast.makeText(context, "success.", Toast.LENGTH_SHORT).show();
                                        al.remove(position);
                                        notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(context, ""+response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Bid> call, Throwable t) {
                                    Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    return true;
                }
            });
            holder.binding1.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });
            if(leads.getStatus()!=null) {
                if (leads.getStatus().equals("")) {
                    holder.binding1.status.setText("Pending");
                    holder.binding1.status.setTextColor(Color.parseColor("#FFD700"));
                } else if (leads.getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.binding1.status.setText("Confirmed");
                    holder.binding1.status.setTextColor(Color.parseColor("#008000"));
                } else {
                    holder.binding1.status.setText("Rejected");
                    holder.binding1.status.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            else
                holder.binding1.status.setText("Pending");
            holder.binding1.amount.setText(bid.getBid().getAmount()+"/-");
            holder.binding1.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }else{
            final BidWithLead bid=al.get(position);
            Leads leads=bid.getLead();
            holder.binding.material.setText(leads.getTypeOfMaterial());
            if(checkLanguage()){
                holder.binding.weight.setText(leads.getWeight()+" टन ");
            }else
                holder.binding.weight.setText(leads.getWeight()+" ton");
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding.location.setText(name);
            final PopupMenu popup = new PopupMenu(context, holder.binding.more);
            Menu menu=popup.getMenu();
            menu.add("Edit");
            menu.add("Delete");
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String title=item.getTitle().toString();
                    if (title.equalsIgnoreCase("Edit")){
                        if (bid.getLead().getStatus().equals("") || (bid.getLead().getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && bid.getLead().getStatus().equalsIgnoreCase("confirmed"))){
                            Intent in = new Intent(context, BidInfoActivity.class);
                            in.putExtra("leads", bid);
                            context.startActivity(in);
                        }else if (bid.getLead().getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && (!bid.getLead().getStatus().equalsIgnoreCase("confirmed"))){
                            Toast.makeText(context, "you can't edit this bid.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "this bid is rejected.", Toast.LENGTH_SHORT).show();
                        }
                    }else if(title.equalsIgnoreCase("Delete")){
                        if (bid.getLead().getStatus().equalsIgnoreCase("") || !(bid.getLead().getDealLockedWith().equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                            BidService.BidApi bidApi=BidService.getBidApiInstance();
                            Call<Bid>call=bidApi.deleteBid(bid.getBid().getBidId());
                            call.enqueue(new Callback<Bid>() {
                                @Override
                                public void onResponse(Call<Bid> call, Response<Bid> response) {
                                    if (response.isSuccessful()){
                                        Toast.makeText(context, "success.", Toast.LENGTH_SHORT).show();
                                        al.remove(position);
                                        notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(context, ""+response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Bid> call, Throwable t) {
                                    Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    return true;
                }
            });
            holder.binding.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup.show();
                }
            });
            if(leads.getStatus()!=null) {
                if (leads.getStatus().equals("")) {
                    holder.binding.status.setText("Pending");
                    holder.binding.status.setTextColor(Color.parseColor("#FFD700"));
                } else if (leads.getDealLockedWith().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    holder.binding.status.setText("Confirmed");
                    holder.binding.status.setTextColor(Color.parseColor("#008000"));
                } else {
                    holder.binding.status.setText("Rejected");
                    holder.binding.status.setTextColor(Color.parseColor("#FF0000"));
                }
            }
            else
                holder.binding.status.setText("Pending");
            holder.binding.amount.setText(bid.getBid().getAmount()+"/-");
            holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class AllBidsViewHolder extends RecyclerView.ViewHolder{
        AllBidViewBinding binding;
        AllBidViewHindiBinding binding1;
        public AllBidsViewHolder(@NonNull AllBidViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        public AllBidsViewHolder(@NonNull AllBidViewHindiBinding binding1) {
            super(binding1.getRoot());
            this.binding1=binding1;
        }
    }
}
