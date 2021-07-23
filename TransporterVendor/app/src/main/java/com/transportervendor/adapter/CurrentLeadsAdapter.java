package com.transportervendor.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.gson.Gson;
import com.transportervendor.BidInfoActivity;
import com.transportervendor.BidNowActivity;
import com.transportervendor.ChatActivity;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.HomeActivity;
import com.transportervendor.NetworkUtil;
import com.transportervendor.R;
import com.transportervendor.apis.LeadsService;
import com.transportervendor.apis.UserService;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.User;
import com.transportervendor.databinding.CurrentLoadViewBinding;
import com.transportervendor.databinding.CurrentLoadViewHindiBinding;
import com.transportervendor.databinding.UpdateStatusViewBinding;
import com.transportervendor.databinding.UpdateStatusViewHindiBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CurrentLeadsAdapter extends RecyclerView.Adapter<CurrentLeadsAdapter.CurrentLeadsViewHolder> {
    ArrayList<BidWithLead>al;
    Context context;

    public CurrentLeadsAdapter(Context context,ArrayList<BidWithLead>al){
        this.context=context;
        this.al=al;
    }
    @NonNull
    @Override
    public CurrentLeadsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (checkLanguage()){
            CurrentLoadViewHindiBinding binding1=CurrentLoadViewHindiBinding.inflate(LayoutInflater.from(context));
            return new CurrentLeadsViewHolder(binding1);
        }
        CurrentLoadViewBinding binding=CurrentLoadViewBinding.inflate(LayoutInflater.from(context));
        return new CurrentLeadsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CurrentLeadsViewHolder holder, int position) {
        if (checkLanguage()){
            final BidWithLead bidWithLead=al.get(position);
            final Leads leads=bidWithLead.getLead();
            holder.binding1.tvmaterial.setText(leads.getTypeOfMaterial());
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding1.tvfrom.setText(name);
            holder.binding1.status.setText(leads.getStatus());
            holder.binding1.tvdate.setText("पूरा होने की तारीख:  "+leads.getDateOfCompletion());
            holder.binding1.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, holder.binding1.more);
                    Menu menu=popup.getMenu();
                    if (checkLanguage()){
                        menu.add("नवीनतम स्थिति");
                        menu.add("ग्राहक के साथ चैट करें");
                        menu.add("सौदा रद्द करें");
                    }else {
                        menu.add("Update Status");
                        menu.add("Chat with Client");
                        menu.add("cancel lead");
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            String title=item.getTitle().toString();
                            if(title.equalsIgnoreCase("Update Status") || title.equalsIgnoreCase("नवीनतम स्थिति")){
                                final AlertDialog ab=new AlertDialog.Builder(context).create();
                                String status="confirmed";
                                final UpdateStatusViewHindiBinding binding= UpdateStatusViewHindiBinding.inflate(LayoutInflater.from(context));
                                ab.setView(binding.getRoot());
                                ab.setTitle("Update Status");
                                if(leads.getStatus().equalsIgnoreCase("confirmed") ){

                                }else{
                                    if(leads.getStatus().equalsIgnoreCase("loaded")){
                                        binding.loaded.setChecked(true);
                                    }else if(leads.getStatus().equalsIgnoreCase("in transit")){
                                        binding.loaded.setChecked(true);
                                        binding.intransit.setChecked(true);
                                    }else if(leads.getStatus().equalsIgnoreCase("reached")){
                                        binding.loaded.setChecked(true);
                                        binding.intransit.setChecked(true);
                                        binding.reached.setChecked(true);
                                    }
                                }
                                ab.setButton(DialogInterface.BUTTON_POSITIVE, "लागू करे", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String status="confirmed";
                                        if(binding.loaded.isChecked())
                                            status="loaded";
                                        if(binding.intransit.isChecked())
                                            status="in transit";
                                        if(binding.reached.isChecked())
                                            status="reached";
                                        if(binding.delivered.isChecked())
                                            status="completed";
                                        leads.setStatus(status);
                                        LeadsService.LeadsApi leadsApi=LeadsService.getLeadsApiInstance();
                                        Call<Leads> call=leadsApi.updateLeads(leads);
                                        ab.dismiss();
                                        if(NetworkUtil.getConnectivityStatus(context)) {
                                            String s="Please wait...";
                                            if (checkLanguage()){
                                                s="कृपया प्रतीक्षा करें...";
                                            }
                                            final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                                            pd.show();
                                            call.enqueue(new Callback<Leads>() {
                                                @Override
                                                public void onResponse(Call<Leads> call, Response<Leads> response) {
                                                    pd.dismiss();
                                                    if(response.code()==200) {
                                                        Toast.makeText(context, "success.", Toast.LENGTH_SHORT).show();
                                                        holder.binding1.status.setText(leads.getStatus());
                                                        UserService.UserApi userApi=UserService.getUserApiInstance();
                                                        Call<User>call3=userApi.getUser(response.body().getUserId());
                                                        pd.show();
                                                        call3.enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                pd.dismiss();
                                                                if(response.isSuccessful()){
                                                                    try {
                                                                        RequestQueue queue = Volley.newRequestQueue(context);
                                                                        String url = "https://fcm.googleapis.com/fcm/send";
                                                                        JSONObject data = new JSONObject();
                                                                        data.put("title", "status update");
                                                                        SharedPreferences shared = context.getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                                                        String json=shared.getString("Transporter","");
                                                                        Gson gson = new Gson();
                                                                        Transporter transporter = gson.fromJson(json, Transporter.class);
                                                                        data.put("body", "From : " + transporter.getName());
                                                                        JSONObject notification_data = new JSONObject();
                                                                        notification_data.put("data", data);
                                                                        notification_data.put("to", response.body().getToken());
                                                                        JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                            }
                                                                        }, new com.android.volley.Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }) {
                                                                            @Override
                                                                            public Map<String, String> getHeaders() {
                                                                                String api_key_header_value = "Key=AAAAWv788Wk:APA91bFW0Z_ISKSzu2ZD97ouIZde3jHsaKSvxLG2_adRdmaUCeQ5Jv88XpcNa2o06RruMbRIWF0gYgh6VPYknq-ELrXgIEmp3SVeu3YTH_2cVmEDUT3Jbg1u6N5OxsacPVIFKqkkBhyp";
                                                                                Map<String, String> headers = new HashMap<>();
                                                                                headers.put("Content-Type", "application/json");
                                                                                headers.put("Authorization", api_key_header_value);
                                                                                return headers;
                                                                            }
                                                                        };
                                                                        queue.add(request);
                                                                    }catch (Exception e){
                                                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                pd.dismiss();
                                                                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Leads> call, Throwable t) {
                                                    pd.dismiss();
                                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else
                                            Toast.makeText(context, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                ab.setButton(DialogInterface.BUTTON_NEGATIVE, "रद्द करे", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ab.dismiss();
                                    }
                                });
                                ab.show();
                            }else if(title.equalsIgnoreCase("Chat with Client")  || title.equalsIgnoreCase("ग्राहक के साथ चैट करें")){
                                Intent in=new Intent(context, ChatActivity.class);
                                in.putExtra("id",leads.getUserId());
                                context.startActivity(in);

                            }else if(title.equalsIgnoreCase("cancel lead") || title.equalsIgnoreCase("सौदा रद्द करें")) {
                                if (leads.getStatus().equalsIgnoreCase("confirmed")) {
                                    leads.setStatus("");
                                    leads.setDealLockedWith("");
                                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                                    Call<Leads> call = leadsApi.updateLeads(leads);
                                    if (NetworkUtil.getConnectivityStatus(context)) {
                                        String s="Please wait...";
                                        if (checkLanguage()){
                                            s="कृपया प्रतीक्षा करें...";
                                        }
                                        final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                                        pd.show();
                                        call.enqueue(new Callback<Leads>() {
                                            @Override
                                            public void onResponse(Call<Leads> call, Response<Leads> response) {
                                                pd.dismiss();
                                                if (response.code() == 200)
                                                    Toast.makeText(context, "लोड रद्द किया गया", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<Leads> call, Throwable t) {
                                                pd.dismiss();
                                                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(context,  "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                    Toast.makeText(context, "this lead can't be cancelled.", Toast.LENGTH_SHORT).show();

                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }else{
            final BidWithLead bidWithLead=al.get(position);
            final Leads leads=bidWithLead.getLead();
            holder.binding.tvmaterial.setText(leads.getTypeOfMaterial());
            String str[]=leads.getPickUpAddress().split(",");
            String name=str[str.length-2];
            str=leads.getDeliveryAddress().split(",");
            name +=" to "+str[str.length-2];
            holder.binding.tvfrom.setText(name);
            holder.binding.status.setText(leads.getStatus());
            holder.binding.tvdate.setText("Date of completion: "+leads.getDateOfCompletion());
            holder.binding.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(context, holder.binding.more);
                    Menu menu=popup.getMenu();
                    if (checkLanguage()){
                        menu.add("नवीनतम स्थिति");
                        menu.add("ग्राहक के साथ चैट करें");
                        menu.add("सौदा रद्द करें");
                    }else {
                        menu.add("Update Status");
                        menu.add("Chat with Client");
                        menu.add("cancel lead");
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            String title=item.getTitle().toString();
                            if(title.equalsIgnoreCase("Update Status") || title.equalsIgnoreCase("नवीनतम स्थिति")){
                                final AlertDialog ab=new AlertDialog.Builder(context).create();
                                String status="confirmed";
                                final UpdateStatusViewBinding binding=UpdateStatusViewBinding.inflate(LayoutInflater.from(context));
                                ab.setView(binding.getRoot());
                                ab.setTitle("Update Status");
                                if(leads.getStatus().equalsIgnoreCase("confirmed") ){

                                }else{
                                    if(leads.getStatus().equalsIgnoreCase("loaded")){
                                        binding.loaded.setChecked(true);
                                    }else if(leads.getStatus().equalsIgnoreCase("in transit")){
                                        binding.loaded.setChecked(true);
                                        binding.intransit.setChecked(true);
                                    }else if(leads.getStatus().equalsIgnoreCase("reached")){
                                        binding.loaded.setChecked(true);
                                        binding.intransit.setChecked(true);
                                        binding.reached.setChecked(true);
                                    }
                                }
                                ab.setButton(DialogInterface.BUTTON_POSITIVE, "Apply", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String status="confirmed";
                                        if(binding.loaded.isChecked())
                                            status="loaded";
                                        if(binding.intransit.isChecked())
                                            status="in transit";
                                        if(binding.reached.isChecked())
                                            status="reached";
                                        if(binding.delivered.isChecked())
                                            status="completed";
                                        leads.setStatus(status);
                                        LeadsService.LeadsApi leadsApi=LeadsService.getLeadsApiInstance();
                                        Call<Leads> call=leadsApi.updateLeads(leads);
                                        ab.dismiss();
                                        if(NetworkUtil.getConnectivityStatus(context)) {
                                            String s="Please wait...";
                                            if (checkLanguage()){
                                                s="कृपया प्रतीक्षा करें...";
                                            }
                                            final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                                            pd.show();
                                            call.enqueue(new Callback<Leads>() {
                                                @Override
                                                public void onResponse(Call<Leads> call, Response<Leads> response) {
                                                    pd.dismiss();
                                                    if(response.code()==200) {
                                                        Toast.makeText(context, "success.", Toast.LENGTH_SHORT).show();
                                                        holder.binding.status.setText(leads.getStatus());
                                                        UserService.UserApi userApi=UserService.getUserApiInstance();
                                                        Call<User>call3=userApi.getUser(response.body().getUserId());
                                                        pd.show();
                                                        call3.enqueue(new Callback<User>() {
                                                            @Override
                                                            public void onResponse(Call<User> call, Response<User> response) {
                                                                pd.dismiss();
                                                                if(response.isSuccessful()){
                                                                    try {
                                                                        RequestQueue queue = Volley.newRequestQueue(context);
                                                                        String url = "https://fcm.googleapis.com/fcm/send";
                                                                        JSONObject data = new JSONObject();
                                                                        data.put("title", "sta,j-jtus update");
                                                                        SharedPreferences shared = context.getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                                                        String json=shared.getString("Transporter","");
                                                                        Gson gson = new Gson();
                                                                        Transporter transporter = gson.fromJson(json, Transporter.class);
                                                                        data.put("body", "From : " + transporter.getName());
                                                                        JSONObject notification_data = new JSONObject();
                                                                        notification_data.put("data", data);
                                                                        notification_data.put("to", response.body().getToken());
                                                                        JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                                                                            @Override
                                                                            public void onResponse(JSONObject response) {
                                                                            }
                                                                        }, new com.android.volley.Response.ErrorListener() {
                                                                            @Override
                                                                            public void onErrorResponse(VolleyError error) {
                                                                                Toast.makeText(context, "" + error, Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }) {
                                                                            @Override
                                                                            public Map<String, String> getHeaders() {
                                                                                String api_key_header_value = "Key=AAAAWv788Wk:APA91bFW0Z_ISKSzu2ZD97ouIZde3jHsaKSvxLG2_adRdmaUCeQ5Jv88XpcNa2o06RruMbRIWF0gYgh6VPYknq-ELrXgIEmp3SVeu3YTH_2cVmEDUT3Jbg1u6N5OxsacPVIFKqkkBhyp";
                                                                                Map<String, String> headers = new HashMap<>();
                                                                                headers.put("Content-Type", "application/json");
                                                                                headers.put("Authorization", api_key_header_value);
                                                                                return headers;
                                                                            }
                                                                        };
                                                                        queue.add(request);
                                                                    }catch (Exception e){
                                                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<User> call, Throwable t) {
                                                                pd.dismiss();
                                                                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Leads> call, Throwable t) {
                                                    pd.dismiss();
                                                    Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }else
                                            Toast.makeText(context, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                ab.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ab.dismiss();
                                    }
                                });
                                ab.show();
                            }else if(title.equalsIgnoreCase("Chat with Client")  || title.equalsIgnoreCase("ग्राहक के साथ चैट करें")){
                                Intent in=new Intent(context, ChatActivity.class);
                                in.putExtra("id",leads.getUserId());
                                context.startActivity(in);

                            }else if(title.equalsIgnoreCase("cancel lead") || title.equalsIgnoreCase("सौदा रद्द करें")) {
                                if (leads.getStatus().equalsIgnoreCase("confirmed")) {
                                    leads.setStatus("");
                                    leads.setDealLockedWith("");
                                    LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
                                    Call<Leads> call = leadsApi.updateLeads(leads);
                                    if (NetworkUtil.getConnectivityStatus(context)) {
                                        String s="Please wait...";
                                        if (checkLanguage()){
                                            s="कृपया प्रतीक्षा करें...";
                                        }
                                        final CustomProgressDialog pd=new CustomProgressDialog(context,s);
                                        pd.show();
                                        call.enqueue(new Callback<Leads>() {
                                            @Override
                                            public void onResponse(Call<Leads> call, Response<Leads> response) {
                                                pd.dismiss();
                                                if (response.code() == 200)
                                                    Toast.makeText(context, "lead cancelled", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onFailure(Call<Leads> call, Throwable t) {
                                                pd.dismiss();
                                                Toast.makeText(context, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    } else {
                                        Toast.makeText(context,  "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                    Toast.makeText(context, "this lead can't be cancelled.", Toast.LENGTH_SHORT).show();

                            }
                            return false;
                        }
                    });
                    popup.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
       return al.size();
    }

    public class CurrentLeadsViewHolder extends RecyclerView.ViewHolder{
        CurrentLoadViewBinding binding;
        CurrentLoadViewHindiBinding binding1;
        public CurrentLeadsViewHolder(CurrentLoadViewBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
        public CurrentLeadsViewHolder(CurrentLoadViewHindiBinding binding1)
        {
            super(binding1.getRoot());
            this.binding1 = binding1;
        }
    }
    private boolean checkLanguage() {
        SharedPreferences mprefs =context.getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}
