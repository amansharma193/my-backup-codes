package com.transportervendor.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transportervendor.beans.Bid;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;
import com.transportervendor.databinding.HistoryViewBinding;
import com.transportervendor.databinding.HistoryViewHindiBinding;

import java.util.ArrayList;

public class CompletedLeadsAdapter extends RecyclerView.Adapter<CompletedLeadsAdapter.CompletedLeadsViewHolder> {
    Context context;
    ArrayList<BidWithLead>al;
    public CompletedLeadsAdapter(Context context, ArrayList<BidWithLead>al){
        this.al=al;
        this.context=context;
    }
    @NonNull
    @Override
    public CompletedLeadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (checkLanguage()){
            HistoryViewHindiBinding binding1=HistoryViewHindiBinding.inflate(LayoutInflater.from(context));
            return new CompletedLeadsViewHolder(binding1);
        }
        HistoryViewBinding binding=HistoryViewBinding.inflate(LayoutInflater.from(context));
        return new CompletedLeadsViewHolder(binding);
    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =context.getSharedPreferences("Transporter",context.MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedLeadsAdapter.CompletedLeadsViewHolder holder, int position) {
        if (checkLanguage()){
            BidWithLead bidWithLead=al.get(position);
            Leads leads=bidWithLead.getLead();
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding1.location.setText(name);
            holder.binding1.material.setText(leads.getTypeOfMaterial());
            holder.binding1.weight.setText(leads.getWeight());
            holder.binding1.amount.setText(bidWithLead.getBid().getAmount());
            holder.binding1.date.setText(leads.getDateOfCompletion());
        }else{
            BidWithLead bidWithLead=al.get(position);
            Leads leads=bidWithLead.getLead();
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding.location.setText(name);
            holder.binding.material.setText(leads.getTypeOfMaterial());
            holder.binding.weight.setText(leads.getWeight());
            holder.binding.amount.setText(bidWithLead.getBid().getAmount());
            holder.binding.date.setText(leads.getDateOfCompletion());
        }

    }

    @Override
    public int getItemCount() {
        if(al!=null)
            return al.size();
        else
            Toast.makeText(context, "no data", Toast.LENGTH_SHORT).show();
            return 0;
    }

    public  class CompletedLeadsViewHolder extends RecyclerView.ViewHolder {
        HistoryViewBinding binding;
        HistoryViewHindiBinding binding1;
        public CompletedLeadsViewHolder(@NonNull HistoryViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
        public CompletedLeadsViewHolder(@NonNull HistoryViewHindiBinding binding1) {
            super(binding1.getRoot());
            this.binding1=binding1;
        }
    }
}
