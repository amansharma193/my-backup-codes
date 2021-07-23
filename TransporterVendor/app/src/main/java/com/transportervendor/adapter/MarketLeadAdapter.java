package com.transportervendor.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.transportervendor.BidNowActivity;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.NetworkUtil;
import com.transportervendor.R;
import com.transportervendor.apis.BidService;
import com.transportervendor.apis.LeadsService;
import com.transportervendor.apis.UserService;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.User;
import com.transportervendor.databinding.MarketLeadViewBinding;
import com.transportervendor.databinding.MarketLeadViewHindiBinding;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MarketLeadAdapter extends RecyclerView.Adapter<MarketLeadAdapter.MarketLeadViewHolder> {
    Context context;
    ArrayList<Leads> al;
    ArrayList<String> lid;

    public MarketLeadAdapter(Context context, ArrayList<Leads> al) {
        this.al = al;
        this.context = context;
        lid = new ArrayList<>();
    }

    public void setLid(ArrayList<String> lid) {
        this.lid = lid;
    }

    @NonNull
    @Override
    public MarketLeadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (checkLanguage()){
            MarketLeadViewHindiBinding binding1=MarketLeadViewHindiBinding.inflate(LayoutInflater.from(context));
            return new MarketLeadViewHolder(binding1);
        }
        MarketLeadViewBinding binding = MarketLeadViewBinding.inflate(LayoutInflater.from(context));
        return new MarketLeadViewHolder(binding);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MarketLeadViewHolder holder, final int position) {
        if (checkLanguage()){
            final Leads leads = al.get(position);
            String name = "";
            String str[] = leads.getPickUpAddress().split(",");
            name += str[str.length - 2];
            str = leads.getDeliveryAddress().split(",");
            name += " to " + str[str.length - 2];
            UserService.UserApi userApi = UserService.getUserApiInstance();
            Call<User> call = userApi.getUser(leads.getUserId());
            Long t = Long.parseLong(leads.getTimestamp());
            Timestamp timestamp = new Timestamp(t);
            Date date = new Date(timestamp.getTime());
            holder.binding1.ptime.setText(date + "");
            if (NetworkUtil.getConnectivityStatus(context)) {
                String s="Please wait...";
                if (checkLanguage()){
                    s="कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            holder.binding1.username.setText(response.body().getName());
                            Picasso.get().load(response.body().getImageUrl()).placeholder(R.drawable.user).into(holder.binding1.uiv);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(context, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            holder.binding1.tvfrom.setText(name);
            holder.binding1.lastdate.setText("पूरा होने की तारीख : " + leads.getDateOfCompletion());
            holder.binding1.tvmaterial.setText(leads.getTypeOfMaterial());
            holder.binding1.tvweight.setText(leads.getWeight());
            String[] st = leads.getDateOfCompletion().split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(st[2]), Integer.parseInt(st[1]), Integer.parseInt(st[0]), 0, 0, 0);
            long dat = calendar.getTimeInMillis();
            if (leads.isActive()) {
                if ((Calendar.getInstance().getTimeInMillis() - dat) > 0) {
                    leads.setActive(false);
                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                    Call<Leads> cal = leadsApi.updateLeads(leads);
                    cal.enqueue(new Callback<Leads>() {
                        @Override
                        public void onResponse(Call<Leads> call, Response<Leads> response) {
                            if (response.isSuccessful()) {
                                holder.binding1.bidbtn.setBackgroundResource(R.drawable.view_inactive);
                                holder.binding1.bidbtn.setText("Inactive");
                            }
                        }

                        @Override
                        public void onFailure(Call<Leads> call, Throwable t) {
                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                if ((Calendar.getInstance().getTimeInMillis() - dat) >= (86400000 * 5)) {
                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                    Call<Leads> cal = leadsApi.deleteLead(leads.getLeadId());
                    cal.enqueue(new Callback<Leads>() {
                        @Override
                        public void onResponse(Call<Leads> call, Response<Leads> response) {
                            if (response.isSuccessful()) {
                                al.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<Leads> call, Throwable t) {
                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            if (leads.isActive()) {
                if (lid.contains(leads.getLeadId())) {
                    holder.binding1.bidbtn.setText("बोली लगाई गई");
                    holder.binding1.bidbtn.setBackgroundResource(R.drawable.bg_btn_bid);
                } else {
                    holder.binding1.bidbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            Intent in = new Intent(context, BidNowActivity.class);
                            in.putExtra("leads", leads);
                            //view.getContext().startActivity(in);
                            context.startActivity(in);
                        }
                    });
                }
            }else{
                holder.binding1.bidbtn.setBackgroundResource(R.drawable.view_inactive);
                holder.binding1.bidbtn.setText("Inactive");
            }
        }else{
            final Leads leads = al.get(position);
            String name = "";
            String str[] = leads.getPickUpAddress().split(",");
            name += str[str.length - 2];
            str = leads.getDeliveryAddress().split(",");
            name += " to " + str[str.length - 2];
            UserService.UserApi userApi = UserService.getUserApiInstance();
            Call<User> call = userApi.getUser(leads.getUserId());
            Long t = Long.parseLong(leads.getTimestamp());
            Timestamp timestamp = new Timestamp(t);
            Date date = new Date(timestamp.getTime());
            holder.binding.ptime.setText(date + "");
            if (NetworkUtil.getConnectivityStatus(context)) {
                String s="Please wait...";
                if (checkLanguage()){
                    s="कृपया प्रतीक्षा करें...";
                }
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.code() == 200) {
                            holder.binding.username.setText(response.body().getName());
                            Picasso.get().load(response.body().getImageUrl()).placeholder(R.drawable.user).into(holder.binding.uiv);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else
                Toast.makeText(context, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            holder.binding.tvfrom.setText(name);
            holder.binding.lastdate.setText("Last Date: " + leads.getDateOfCompletion());
            holder.binding.tvmaterial.setText(leads.getTypeOfMaterial());
            holder.binding.tvweight.setText(leads.getWeight());
            String[] st = leads.getDateOfCompletion().split("/");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(st[2]), Integer.parseInt(st[1]), Integer.parseInt(st[0]), 0, 0, 0);
            long dat = calendar.getTimeInMillis();
            if (leads.isActive()) {
                if ((Calendar.getInstance().getTimeInMillis() - dat) > 0) {
                    leads.setActive(false);
                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                    Call<Leads> cal = leadsApi.updateLeads(leads);
                    cal.enqueue(new Callback<Leads>() {
                        @Override
                        public void onResponse(Call<Leads> call, Response<Leads> response) {
                            if (response.isSuccessful()) {
                                holder.binding.bidbtn.setBackgroundResource(R.drawable.view_inactive);
                                holder.binding.bidbtn.setText("Inactive");
                            }
                        }

                        @Override
                        public void onFailure(Call<Leads> call, Throwable t) {
                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                if ((Calendar.getInstance().getTimeInMillis() - dat) >= (86400000 * 5)) {
                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                    Call<Leads> cal = leadsApi.deleteLead(leads.getLeadId());
                    cal.enqueue(new Callback<Leads>() {
                        @Override
                        public void onResponse(Call<Leads> call, Response<Leads> response) {
                            if (response.isSuccessful()) {
                                al.remove(position);
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<Leads> call, Throwable t) {
                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
            if (leads.isActive()) {
                if (lid.contains(leads.getLeadId())) {
                    holder.binding.bidbtn.setText("Bidded");
                    holder.binding.bidbtn.setBackgroundResource(R.drawable.bg_btn_bid);
                } else {
                    holder.binding.bidbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            Intent in = new Intent(context, BidNowActivity.class);
                            in.putExtra("leads", leads);
                            //view.getContext().startActivity(in);
                            context.startActivity(in);
                        }
                    });
                }
            }else{
                holder.binding.bidbtn.setBackgroundResource(R.drawable.view_inactive);
                holder.binding.bidbtn.setText("Inactive");
            }
        }

    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class MarketLeadViewHolder extends RecyclerView.ViewHolder {
        MarketLeadViewBinding binding;
        MarketLeadViewHindiBinding binding1;
        public MarketLeadViewHolder(@NonNull MarketLeadViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public MarketLeadViewHolder(@NonNull MarketLeadViewHindiBinding binding1) {
            super(binding1.getRoot());
            this.binding1 = binding1;
        }
    }
    private boolean checkLanguage() {
        SharedPreferences mprefs = context.getSharedPreferences("Transporter", MODE_PRIVATE);
        String s = mprefs.getString("language", "");
        if (s.equalsIgnoreCase("hindi")) {
            return true;
        }
        return false;
    }
}
