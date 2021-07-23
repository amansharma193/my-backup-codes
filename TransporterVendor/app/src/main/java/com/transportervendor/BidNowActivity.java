package com.transportervendor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.transportervendor.apis.BidService;
import com.transportervendor.apis.TransporterService;
import com.transportervendor.apis.UserService;
import com.transportervendor.beans.Bid;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.SpecialRequirement;
import com.transportervendor.beans.Transporter;
import com.transportervendor.beans.User;
import com.transportervendor.databinding.BidNowViewBinding;
import com.transportervendor.databinding.BidNowViewHindiBinding;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidNowActivity extends AppCompatActivity {
    Toolbar toolbar;
    BidNowViewBinding binding;
    BidNowViewHindiBinding binding1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent in = getIntent();
        final Leads leads = (Leads) in.getSerializableExtra("leads");
        UserService.UserApi userApi = UserService.getUserApiInstance();
        Call<User> call = userApi.getUser(leads.getUserId());
        if (checkLanguage()) {
            binding1 = BidNowViewHindiBinding.inflate(LayoutInflater.from(BidNowActivity.this));
            setContentView(binding1.getRoot());
            setSupportActionBar(binding1.toolbar);
            getSupportActionBar().setTitle("अभी बोली लगाएँ");
            if (NetworkUtil.getConnectivityStatus(this)) {
                String s = "Please wait...";
                if (checkLanguage()) {
                    s = "कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd = new CustomProgressDialog(BidNowActivity.this, s);
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            final User user = response.body();
                            binding1.username.setText(user.getName());
                            binding1.resusername.setText(user.getName()+" को प्रतिसाद भेजें");
                            String str[] = leads.getPickUpAddress().split(",");
                            String name = str[str.length - 2];
                            str = leads.getDeliveryAddress().split(",");
                            name += " to " + str[str.length - 2];
                            binding1.tvfrom.setText(name);
                            binding1.tvmaterial.setText(leads.getTypeOfMaterial());
                            binding1.tvweight.setText(leads.getWeight() + " ton");
                            binding1.pickadd.setText(leads.getPickUpAddress());
                            binding1.deladd.setText(leads.getDeliveryAddress());
                            binding1.pickcontact.setText(leads.getContactForPickup());
                            binding1.delcontact.setText(leads.getContactForDelivery());
                            binding1.lastdate.setText(leads.getDateOfCompletion());
                            Toast.makeText(BidNowActivity.this, "" + leads.getSpecialRequirement(), Toast.LENGTH_SHORT).show();
                            if (leads.getSpecialRequirement() != null) {
                                final SpecialRequirement sp = leads.getSpecialRequirement();
                                if (!TextUtils.isEmpty(sp.getAdditionalMaterialType())) {
                                    binding1.material2.setText(sp.getAdditionalMaterialType());
                                    binding1.pick.setText(sp.getPickupStreet());
                                    binding1.delivery.setText(sp.getDeliverystreet());
                                    binding1.remark.setText(sp.getRemark());
                                }
                                binding1.read.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (binding1.read.getText().toString().equalsIgnoreCase("Read more...")) {
                                            binding1.read.setText("Read less...");
                                            if (sp.getHandelWithCare()) {
                                                binding1.hwc.setVisibility(View.VISIBLE);
                                                binding1.line.setVisibility(View.VISIBLE);
                                            }
                                            if (!TextUtils.isEmpty(sp.getAdditionalMaterialType())) {
                                                binding1.mt.setVisibility(View.VISIBLE);
                                                binding1.pc.setVisibility(View.VISIBLE);
                                                binding1.rm.setVisibility(View.VISIBLE);
                                                binding1.dv.setVisibility(View.VISIBLE);
                                                binding1.line.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            binding1.hwc.setVisibility(View.GONE);
                                            binding1.read.setText("Read more...");
                                            binding1.mt.setVisibility(View.GONE);
                                            binding1.pc.setVisibility(View.GONE);
                                            binding1.rm.setVisibility(View.GONE);
                                            binding1.dv.setVisibility(View.GONE);
                                            binding1.line.setVisibility(View.GONE);
                                            binding1.hwc.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                binding1.read.setVisibility(View.GONE);
                            }
                            binding1.btnbidnow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String rate = binding1.etRate.getText().toString();
                                    String remark = binding1.etRemark.getText().toString();
                                    if (rate.isEmpty()) {
                                        binding1.etRate.setError("this field can't be empty.");
                                        return;
                                    }
                                    if (remark.isEmpty()) {
                                        binding1.etRemark.setError("this field can't be empty.");
                                        return;
                                    }
                                    SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                    String json = shared.getString("Transporter", "");
                                    Gson gson = new Gson();
                                    Transporter transporter = gson.fromJson(json, Transporter.class);
                                    final Bid bid = new Bid("", leads.getLeadId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), transporter.getName(), rate, remark, leads.getDateOfCompletion());
                                    BidService.BidApi bidApi = BidService.getBidApiInstance();
                                    Call<Bid> call = bidApi.createBid(bid);
                                    if (NetworkUtil.getConnectivityStatus(BidNowActivity.this)) {
                                        pd.show();
                                        call.enqueue(new Callback<Bid>() {
                                            @Override
                                            public void onResponse(Call<Bid> call, Response<Bid> response) {
                                                pd.dismiss();
                                                if (response.code() == 200) {
                                                    try {
                                                        RequestQueue queue = Volley.newRequestQueue(BidNowActivity.this);
                                                        String url = "https://fcm.googleapis.com/fcm/send";

                                                        String token = user.getToken();
                                                        Log.e("Token ======>>>>>>", "" + user.getToken() + "    =============" + token);
                                                        SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                                        String json = shared.getString("Transporter", "");
                                                        Gson gson = new Gson();
                                                        Transporter transporter = gson.fromJson(json, Transporter.class);
                                                        JSONObject data = new JSONObject();
                                                        data.put("title", "new bid");
                                                        data.put("body", "From : " + transporter.getName());
                                                        JSONObject notification_data = new JSONObject();
                                                        notification_data.put("data", data);
                                                        notification_data.put("to", user.getToken());
                                                        JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                            }
                                                        }, new com.android.volley.Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.e("spanshoe", "error..." + error.getMessage());
                                                                Toast.makeText(BidNowActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                                                    } catch (Exception e) {
                                                        Toast.makeText(BidNowActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Bid> call, Throwable t) {
                                                pd.dismiss();
                                                Toast.makeText(BidNowActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else
                                        Toast.makeText(BidNowActivity.this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(BidNowActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            } else {
                Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (NetworkUtil.getConnectivityStatus(this)) {
                binding = BidNowViewBinding.inflate(LayoutInflater.from(BidNowActivity.this));
                setContentView(binding.getRoot());
                setSupportActionBar(binding.toolbar);
                getSupportActionBar().setTitle("Bid Now");
                String s = "Please wait...";
                if (checkLanguage()) {
                    s = "कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd = new CustomProgressDialog(BidNowActivity.this, s);
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            final User user = response.body();
                            binding.username.setText(user.getName());
                            binding.resusername.setText("Send Response to " + user.getName());
                            String str[] = leads.getPickUpAddress().split(",");
                            String name = str[str.length - 2];
                            str = leads.getDeliveryAddress().split(",");
                            name += " to " + str[str.length - 2];
                            binding.tvfrom.setText(name);
                            binding.tvmaterial.setText(leads.getTypeOfMaterial());
                            binding.tvweight.setText(leads.getWeight() + " ton");
                            binding.pickadd.setText(leads.getPickUpAddress());
                            binding.deladd.setText(leads.getDeliveryAddress());
                            binding.pickcontact.setText(leads.getContactForPickup());
                            binding.delcontact.setText(leads.getContactForDelivery());
                            binding.lastdate.setText(leads.getDateOfCompletion());
                            Toast.makeText(BidNowActivity.this, "" + leads.getSpecialRequirement(), Toast.LENGTH_SHORT).show();
                            if (leads.getSpecialRequirement() != null) {
                                final SpecialRequirement sp = leads.getSpecialRequirement();
                                if (!TextUtils.isEmpty(sp.getAdditionalMaterialType())) {
                                    binding.material2.setText(sp.getAdditionalMaterialType());
                                    binding.pick.setText(sp.getPickupStreet());
                                    binding.delivery.setText(sp.getDeliverystreet());
                                    binding.remark.setText(sp.getRemark());
                                }
                                binding.read.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (binding.read.getText().toString().equalsIgnoreCase("Read more...")) {
                                            binding.read.setText("Read less...");
                                            if (sp.getHandelWithCare()) {
                                                binding.hwc.setVisibility(View.VISIBLE);
                                                binding.line.setVisibility(View.VISIBLE);
                                            }
                                            if (!TextUtils.isEmpty(sp.getAdditionalMaterialType())) {
                                                binding.mt.setVisibility(View.VISIBLE);
                                                binding.pc.setVisibility(View.VISIBLE);
                                                binding.rm.setVisibility(View.VISIBLE);
                                                binding.dv.setVisibility(View.VISIBLE);
                                                binding.line.setVisibility(View.VISIBLE);
                                            }

                                        } else {
                                            binding.hwc.setVisibility(View.GONE);
                                            binding.read.setText("Read more...");
                                            binding.mt.setVisibility(View.GONE);
                                            binding.pc.setVisibility(View.GONE);
                                            binding.rm.setVisibility(View.GONE);
                                            binding.dv.setVisibility(View.GONE);
                                            binding.line.setVisibility(View.GONE);
                                            binding.hwc.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            } else {
                                binding.read.setVisibility(View.GONE);
                            }
                            binding.btnbidnow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String rate = binding.etRate.getText().toString();
                                    String remark = binding.etRemark.getText().toString();
                                    if (rate.isEmpty()) {
                                        binding.etRate.setError("this field can't be empty.");
                                        return;
                                    }
                                    if (remark.isEmpty()) {
                                        binding.etRemark.setError("this field can't be empty.");
                                        return;
                                    }
                                    SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                    String json = shared.getString("Transporter", "");
                                    Gson gson = new Gson();
                                    Transporter transporter = gson.fromJson(json, Transporter.class);
                                    final Bid bid = new Bid("", leads.getLeadId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), transporter.getName(), rate, remark, leads.getDateOfCompletion());
                                    BidService.BidApi bidApi = BidService.getBidApiInstance();
                                    Call<Bid> call = bidApi.createBid(bid);
                                    if (NetworkUtil.getConnectivityStatus(BidNowActivity.this)) {
                                        pd.show();
                                        call.enqueue(new Callback<Bid>() {
                                            @Override
                                            public void onResponse(Call<Bid> call, Response<Bid> response) {
                                                pd.dismiss();
                                                if (response.code() == 200) {
                                                    try {
                                                        RequestQueue queue = Volley.newRequestQueue(BidNowActivity.this);
                                                        String url = "https://fcm.googleapis.com/fcm/send";

                                                        String token = user.getToken();
                                                        Log.e("Token ======>>>>>>", "" + user.getToken() + "    =============" + token);
                                                        SharedPreferences shared = getSharedPreferences("Transporter", Context.MODE_PRIVATE);
                                                        String json = shared.getString("Transporter", "");
                                                        Gson gson = new Gson();
                                                        Transporter transporter = gson.fromJson(json, Transporter.class);
                                                        JSONObject data = new JSONObject();
                                                        data.put("title", "new bid");
                                                        data.put("body", "From : " + transporter.getName());
                                                        JSONObject notification_data = new JSONObject();
                                                        notification_data.put("data", data);
                                                        notification_data.put("to", user.getToken());
                                                        JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new com.android.volley.Response.Listener<JSONObject>() {
                                                            @Override
                                                            public void onResponse(JSONObject response) {
                                                            }
                                                        }, new com.android.volley.Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                                Log.e("spanshoe", "error..." + error.getMessage());
                                                                Toast.makeText(BidNowActivity.this, "" + error, Toast.LENGTH_SHORT).show();
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
                                                    } catch (Exception e) {
                                                        Toast.makeText(BidNowActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Bid> call, Throwable t) {
                                                pd.dismiss();
                                                Toast.makeText(BidNowActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else
                                        Toast.makeText(BidNowActivity.this, "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(BidNowActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });

            } else {
                Toast.makeText(this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public boolean checkLanguage() {
        SharedPreferences mprefs = getSharedPreferences("Transporter", MODE_PRIVATE);
        String s = mprefs.getString("language", "");
        if (s.equalsIgnoreCase("hindi")) {
            return true;
        }
        return false;
    }
}
