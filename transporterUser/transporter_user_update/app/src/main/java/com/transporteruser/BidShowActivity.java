package com.transporteruser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.transporteruser.adapters.BidShowAdapter;
import com.transporteruser.api.UserService;
import com.transporteruser.bean.Bid;
import com.transporteruser.bean.Lead;
import com.transporteruser.bean.Transporter;
import com.transporteruser.databinding.ActivityBidShowBinding;
import com.transporteruser.databinding.ReceiveBiddingAlrtdilogBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidShowActivity extends AppCompatActivity {
    ActivityBidShowBinding binding;
    UserService.UserApi userApi;
    BidShowAdapter adapter;
    Lead lead;
    Transporter transporter;
    String name;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("user",MODE_PRIVATE);
        name = sp.getString("name","");
        userApi = UserService.getUserApiInstance();
        binding = ActivityBidShowBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        Intent in = getIntent();
        lead = (Lead) in.getSerializableExtra("lead");

        getBidsByLead(lead.getLeadId());

        String[] pickupAddress = lead.getPickUpAddress().split(",");
        String pickup= (pickupAddress[2]);
        String[] deliveyAdress = lead.getDeliveryAddress().split(",");
        String delivery= (deliveyAdress[2]);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(pickup+" To "+delivery);
    }

    private void getBidsByLead(String leadId) {
        Call<ArrayList<Bid>> call = userApi.getAllBidsByLeadId(leadId);
        call.enqueue(new Callback<ArrayList<Bid>>() {
            @Override
            public void onResponse(Call<ArrayList<Bid>> call, Response<ArrayList<Bid>> response) {
                if(response.code() == 200) {
                    adapter = new BidShowAdapter(response.body(),lead);
                    binding.rv.setAdapter(adapter);
                    binding.rv.setLayoutManager(new LinearLayoutManager(BidShowActivity.this));
                    adapter.onBidShowClickListener(new BidShowAdapter.OnRecycleViewClickListener() {
                        @Override
                        public void onClickListener(Bid bid, int position) {
                            getAlertDialog(bid);
                            userApi.getCurrentTransporter(bid.getTransporterId()).enqueue(new Callback<Transporter>() {
                                @Override
                                public void onResponse(Call<Transporter> call, Response<Transporter> response) {
                                    if(response.code() == 200)
                                        transporter = response.body();
                                }

                                @Override
                                public void onFailure(Call<Transporter> call, Throwable t) {

                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Bid>> call, Throwable t) {

            }
        });
    }

    private void getAlertDialog(final Bid bid){
        final AlertDialog ab = new AlertDialog.Builder(this).create();
        ReceiveBiddingAlrtdilogBinding binding = ReceiveBiddingAlrtdilogBinding.inflate(LayoutInflater.from(this));
        ab.setView(binding.getRoot());
        ab.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.tvdate.setText(bid.getEstimatedDate());
        binding.tvRate.setText(""+bid.getAmount());
        binding.tvRemark.setText(bid.getRemark());
        binding.tvTransporterName.setText(bid.getTransporterName());
        binding.tvMaterial.setText(lead.getTypeOfMaterial());
        binding.ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ab.dismiss();
            }
        });
        ab.setCancelable(false);
        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lead.setDealLockedWith(bid.getTransporterId());
                lead.setTransporterName(bid.getTransporterName());
                lead.setStatus("confirmed");
                //lead.setAmount(bid.getAmount());
                Call<Lead> call=  userApi.updateLead(lead);

                call.enqueue(new Callback<Lead>() {
                    @Override
                    public void onResponse(Call<Lead> call, Response<Lead> response) {
                        if (response.code()==200){
                            Toast.makeText(BidShowActivity.this, "Bid Accepted", Toast.LENGTH_SHORT).show();
                            notification();
                            Intent i = new Intent(BidShowActivity.this,MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<Lead> call, Throwable t) {

                    }
                });

            }
        });
        ab.show();
    }

    private void notification(){
        String token = transporter.getToken();
        try{
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject data = new JSONObject();
            data.put("title","Accept Bid ");
            data.put("body", "From "+name);

            JSONObject notification_data = new JSONObject();
            notification_data.put("data", data);
            notification_data.put("to",token);

            JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String api_key_header_value = "AAAAWv788Wk:APA91bFW0Z_ISKSzu2ZD97ouIZde3jHsaKSvxLG2_adRdmaUCeQ5Jv88XpcNa2o06RruMbRIWF0gYgh6VPYknq-ELrXgIEmp3SVeu3YTH_2cVmEDUT3Jbg1u6N5OxsacPVIFKqkkBhyp";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", api_key_header_value);
                    return headers;
                }
            };
            queue.add(request);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}