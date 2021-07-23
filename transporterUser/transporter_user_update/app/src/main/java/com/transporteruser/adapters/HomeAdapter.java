package com.transporteruser.adapters;

import android.content.Intent;
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

import com.transporteruser.BidShowActivity;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.CurrentAndConfirmedListBinding;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    ArrayList<Lead> list;
    OnHomeRecyclerListner listner;
    boolean currentLoad = true;
    public HomeAdapter(ArrayList<Lead> list){
        this.list = list;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CurrentAndConfirmedListBinding binding = CurrentAndConfirmedListBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new HomeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeViewHolder holder, final int position) {
        final Lead lead = list.get(position);
        if(position == 0 && lead.getStatus().equals("")) {
            holder.binding.tvCl.setVisibility(View.VISIBLE);
            holder.binding.tvCl.setText("Current Loads");
        }else if(currentLoad && (lead.getStatus().equalsIgnoreCase("confirmed") || lead.getStatus().equalsIgnoreCase("in transist") || lead.getStatus().equalsIgnoreCase("reached") || lead.getStatus().equalsIgnoreCase("loaded"))){
            holder.binding.tvCl.setVisibility(View.VISIBLE);
            holder.binding.tvCl.setText("Confirmed Loads");
        }else{
            holder.binding.tvCl.setVisibility(View.GONE);
        }


        if(lead !=null && lead.getStatus().equals("")) {
            holder.binding.llCurrentLoad.setVisibility(View.VISIBLE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent in = new Intent(holder.itemView.getContext(), BidShowActivity.class);
                    in.putExtra("lead",lead);
                    holder.itemView.getContext().startActivity(in);
                }
            });
            holder.binding.llConfirmLoad.setVisibility(View.GONE);
            String[] deliveryAddress = lead.getDeliveryAddress().split(",");
            String delivery=(deliveryAddress[1]);

            String[] pickupAddress = lead.getPickUpAddress().split(",");
            String pickup= (pickupAddress[1]);
            holder.binding.tvCLLocation.setText(pickup+" To "+delivery);
            holder.binding.tvCLMaterialType.setText(lead.getTypeOfMaterial());
            if (!lead.getBidCount().equalsIgnoreCase("0")) {
                holder.binding.llCounter.setVisibility(View.VISIBLE);
                holder.binding.tvCounter.setText("" + lead.getBidCount());
            }
            else{
                holder.binding.llCounter.setVisibility(View.GONE);
            }


            holder.binding.tvLastDate.setText(lead.getDateOfCompletion());
            holder.binding.tvDistance.setText(lead.getKm());
        }else{
            currentLoad = false;
            holder.binding.llCurrentLoad.setVisibility(View.GONE);
            holder.binding.llConfirmLoad.setVisibility(View.VISIBLE);
            holder.binding.tvTransporterName.setText(""+lead.getTransporterName());



            String[] deliveryAddress = lead.getDeliveryAddress().split(",");
            String de=(deliveryAddress[1]);

            String[] pickupAddress = lead.getPickUpAddress().split(",");
            String pickup= (pickupAddress[1]);
            holder.binding.tvLocation.setText(pickup+" To "+de );
            holder.binding.tvMaterialType.setText(lead.getTypeOfMaterial());
            holder.binding.tvPickUpContact.setText(lead.getContactForPickup());
            holder.binding.tvDeliveryContact.setText(lead.getContactForDelivery());
            holder.binding.tvProgressStatus.setText(lead.getStatus());

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder{
        CurrentAndConfirmedListBinding binding;
        public HomeViewHolder(final CurrentAndConfirmedListBinding binding) {
            super(binding.getRoot());
            this.binding=binding;


            binding.morevertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PopupMenu popupMenu = new PopupMenu(itemView.getContext(),binding.morevertical);
                    final Menu menu= popupMenu.getMenu();
                    menu.add("Edit");
                    menu.add("Delete");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            String title = menuItem.getTitle().toString();
                            int position = getAdapterPosition();
                            if(title.equals("Edit")){
                                if(position != RecyclerView.NO_POSITION && listner != null)
                                    listner.onClick(list.get(position),position,"Edit");
                            }else if (title.equals("Delete")){
                                if(position != RecyclerView.NO_POSITION && listner != null)
                                    listner.onClick(list.get(position),position,"Delete");
                            } 
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });


            binding.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(),binding.morevertical);
                    final Menu menu= popupMenu.getMenu();
                    menu.add("Chat with Client");
                    menu.add("Cancel");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            String title = menuItem.getTitle().toString();
                            int position = getAdapterPosition();
                            if(title.equals("Chat with Client")){
                                if(position != RecyclerView.NO_POSITION && listner != null)
                                    listner.onClick(list.get(position),position,"Chat with client");
                            }else if (title.equals("Cancel")){
                                Toast.makeText(itemView.getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });


        }
    }

    public interface OnHomeRecyclerListner{
        public void onClick(Lead lead , int position , String status);
    }

    public void OnHomeClick(OnHomeRecyclerListner listner){
        this.listner = listner;
    }
}
