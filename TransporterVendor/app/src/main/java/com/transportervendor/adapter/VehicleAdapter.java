package com.transportervendor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.EditVehicle;
import com.transportervendor.HomeActivity;
import com.transportervendor.ManageVehicle;
import com.transportervendor.NetworkUtil;
import com.transportervendor.apis.VehicleService;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.Vehicle;
import com.transportervendor.databinding.VehicleViewBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ManageVehicleViewHolder> {
    Context context;
    ArrayList<Vehicle>al;

    public VehicleAdapter(Context context, ArrayList<Vehicle> al) {
        this.context = context;
        this.al = al;
    }


    @NonNull
    @Override
    public ManageVehicleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleViewBinding binding=VehicleViewBinding.inflate(LayoutInflater.from(context));
        return new ManageVehicleViewHolder(binding);
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
    public void onBindViewHolder(@NonNull final ManageVehicleViewHolder holder, final int position) {
        final Vehicle vehicle=al.get(position);
        holder.binding.count.setText("   "+vehicle.getCount());
        holder.binding.name.setText(vehicle.getName());
        Picasso.get().load(vehicle.getImageUrl()).into(holder.binding.pic);
        holder.binding.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.binding.more);
                Menu menu=popup.getMenu();
                if (checkLanguage()){
                    menu.add("संपादित करें");
                    menu.add("डिलीट");
                }else {
                    menu.add("Edit");
                    menu.add("Delete");
                }
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title=item.getTitle().toString();
                        if(title.equalsIgnoreCase("Edit") || title.equalsIgnoreCase("संपादित करें")){
                            Intent in=new Intent(context, EditVehicle.class);
                            in.putExtra("vehicle",vehicle);
                            context.startActivity(in);
                        }else if (title.equalsIgnoreCase("Delete") || title.equalsIgnoreCase("डिलीट")){
                            if(NetworkUtil.getConnectivityStatus(context)){
                                VehicleService.VehicleApi vehicleApi=VehicleService.getVehicleApiInstance();
                                Call<Transporter>call=vehicleApi.deleteVehicle(vehicle.getVehicleId(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                                String s="Please wait...";
                                if (checkLanguage()){
                                    s="कृपया प्रतीक्षा करें...";
                                }
                                final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                                pd.show();
                                call.enqueue(new Callback<Transporter>() {
                                    @Override
                                    public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                        pd.dismiss();
                                        if(response.isSuccessful()){
                                            SharedPreferences mPrefs =  context.getSharedPreferences("Transporter",Context.MODE_PRIVATE);
                                            SharedPreferences.Editor prefsEditor = mPrefs.edit();
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            prefsEditor.putString("Transporter", json);
                                            prefsEditor.commit();
                                            Toast.makeText(context, "vehicle deleted.", Toast.LENGTH_SHORT).show();
                                            al.remove(position);
                                            notifyDataSetChanged();
                                        }else{
                                            Gson gson = new Gson();
                                            String json = gson.toJson(response.body());
                                            Toast.makeText(context, response.code()+"", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Transporter> call, Throwable t) {
                                        pd.dismiss();
                                        Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return al.size();
    }

    public class ManageVehicleViewHolder extends RecyclerView.ViewHolder{
        VehicleViewBinding binding;
        public ManageVehicleViewHolder(@NonNull VehicleViewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
