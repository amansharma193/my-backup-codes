package com.transporteruser.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.transporteruser.api.UserService;
import com.transporteruser.bean.Lead;
import com.transporteruser.databinding.CompletedLoadBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedLoadShowAdapter extends RecyclerView.Adapter<CompletedLoadShowAdapter.CompletedViewHolder> {
    ArrayList<Lead> leadList;
    public CompletedLoadShowAdapter(ArrayList<Lead> leadList){
        this.leadList = leadList;
    }

    @NonNull
    @Override
    public CompletedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompletedLoadBinding binding = CompletedLoadBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new CompletedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CompletedViewHolder holder, int position) {
        final Lead lead = leadList.get(position);
        holder.binding.tvDate.setText(lead.getDateOfCompletion());
        holder.binding.tvTypeOfmaterial.setText(lead.getTypeOfMaterial());
        holder.binding.tvQuntity.setText(lead.getWeight());
        holder.binding.morevertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(holder.itemView.getContext(),holder.binding.morevertical);
                final Menu menu= popupMenu.getMenu();
                menu.add("Delete");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String title = menuItem.getTitle().toString();
                        if (title.equalsIgnoreCase("Delete")){
                            final UserService.UserApi userApi = UserService.getUserApiInstance();
                            AlertDialog.Builder ab = new AlertDialog.Builder(holder.itemView.getContext());
                            ab.setTitle("DELETE");
                            ab.setMessage("Are You Sure ?");
                            ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    userApi.deleteLeadById(lead.getLeadId()).enqueue(new Callback<ArrayList<Lead>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Lead>> call, Response<ArrayList<Lead>> response) {
                                            if(response.code() == 200){
                                                Toast.makeText(holder.itemView.getContext(), "Lead Deleted", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                                Toast.makeText(holder.itemView.getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Lead>> call, Throwable t) {
                                            Toast.makeText(holder.itemView.getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                            ab.setNegativeButton("No",null);
                            ab.show();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        String[] pickupAddress = lead.getPickUpAddress().split(",");
        String pickup= (pickupAddress[1]);
        String[] deliveryAddress = lead.getDeliveryAddress().split(",");
        String delivery= (deliveryAddress[1]);
        holder.binding.tvAddress.setText(pickup+" To "+delivery);
    }

    @Override
    public int getItemCount() {
        return leadList.size();
    }


    public class CompletedViewHolder extends RecyclerView.ViewHolder {
        CompletedLoadBinding binding;
        public CompletedViewHolder(CompletedLoadBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
