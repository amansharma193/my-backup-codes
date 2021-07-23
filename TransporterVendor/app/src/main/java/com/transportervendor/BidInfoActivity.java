package com.transportervendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.apis.BidService;
import com.transportervendor.apis.UserService;
import com.transportervendor.beans.Bid;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.SpecialRequirement;
import com.transportervendor.beans.User;
import com.transportervendor.databinding.BidNowViewBinding;
import com.transportervendor.databinding.BidNowViewHindiBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidInfoActivity extends AppCompatActivity {
    BidNowViewBinding binding;
    BidNowViewHindiBinding binding1;
    BidWithLead bid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkLanguage()) {
            binding1 = BidNowViewHindiBinding.inflate(LayoutInflater.from(this));
            setContentView(binding1.getRoot());
            setSupportActionBar(binding1.toolbar);
            getSupportActionBar().setTitle("अद्यतन बोली");
        } else {
            binding = BidNowViewBinding.inflate(LayoutInflater.from(this));
            setContentView(binding.getRoot());
            setSupportActionBar(binding.toolbar);
            getSupportActionBar().setTitle("Update Bid");
        }
        Intent in = getIntent();
        bid = (BidWithLead) in.getSerializableExtra("leads");

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkLanguage()) {
            UserService.UserApi userApi = UserService.getUserApiInstance();
            Call<User> call = userApi.getUser(bid.getLead().getUserId());
            if (NetworkUtil.getConnectivityStatus(this)) {
                String s = "Please wait...";
                if (checkLanguage()) {
                    s = "कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd = new CustomProgressDialog(BidInfoActivity.this, s);
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            binding1.username.setText(response.body().getName());
                            binding1.resusername.setText(response.body().getName() + " को प्रतिसाद भेजें");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(BidInfoActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            binding1.etRemark.setText(bid.getBid().getRemark());
            String str[] = bid.getLead().getPickUpAddress().split(",");
            String name = str[str.length - 2];
            str = bid.getLead().getDeliveryAddress().split(",");
            name += " to " + str[str.length - 2];
            binding1.tvfrom.setText(name);
            binding1.tvmaterial.setText(bid.getLead().getTypeOfMaterial());
            binding1.tvweight.setText(bid.getLead().getWeight() + " ton");
            binding1.etRate.setText(bid.getBid().getAmount());
            binding1.etRemark.setText(bid.getBid().getRemark());
            binding1.lastdate.setText(bid.getLead().getDateOfCompletion());
            binding1.pickcontact.setText(bid.getLead().getContactForPickup());
            binding1.delcontact.setText(bid.getLead().getContactForDelivery());
            binding1.pickadd.setText(bid.getLead().getPickUpAddress());
            binding1.deladd.setText(bid.getLead().getDeliveryAddress());
            binding1.btnbidnow.setText("अद्यतन बोली");
            if (bid.getLead().getSpecialRequirement() != null) {
                final SpecialRequirement sp = bid.getLead().getSpecialRequirement();
                binding1.material2.setText(sp.getAdditionalMaterialType());
                binding1.pick.setText(sp.getPickupStreet());
                binding1.delivery.setText(sp.getDeliverystreet());
                binding1.remark.setText(sp.getRemark());
                binding1.read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding1.read.getText().toString().equalsIgnoreCase("अधिक पढ़ें...")) {
                            binding1.read.setText("कम पढ़ें...");
                            binding1.line.setVisibility(View.VISIBLE);
                            if (sp.getHandelWithCare()) {
                                binding1.hwc.setVisibility(View.VISIBLE);
                            }
                            binding1.mt.setVisibility(View.VISIBLE);
                            binding1.pc.setVisibility(View.VISIBLE);
                            binding1.rm.setVisibility(View.VISIBLE);
                            binding1.dv.setVisibility(View.VISIBLE);
                        } else {
                            binding1.read.setText("अधिक पढ़ें...");
                            binding1.line.setVisibility(View.GONE);
                            binding1.mt.setVisibility(View.GONE);
                            binding1.pc.setVisibility(View.GONE);
                            binding1.rm.setVisibility(View.GONE);
                            binding1.dv.setVisibility(View.GONE);
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
                    String etrate = binding1.etRate.getText().toString();
                    String etremark = binding1.etRemark.getText().toString();
                    if (etrate.isEmpty()) {
                        binding1.etRate.setError("यह फ़ील्ड खाली नहीं हो सकती।");
                        return;
                    }
                    if (etremark.isEmpty()) {
                        binding1.etRemark.setError("यह फ़ील्ड खाली नहीं हो सकती।");
                        return;
                    }
                    if (NetworkUtil.getConnectivityStatus(BidInfoActivity.this)) {
                        finish();
                        String s = "Please wait...";
                        if (checkLanguage()) {
                            s = "कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd = new CustomProgressDialog(BidInfoActivity.this, s);
                        pd.show();
                        final Bid bd = new Bid(bid.getBid().getBidId(), bid.getLead().getLeadId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "", etrate, etremark, bid.getLead().getDateOfCompletion());
                        BidService.BidApi bidApi = BidService.getBidApiInstance();
                        Call<Bid> call = bidApi.updateBid(bd);
                        call.enqueue(new Callback<Bid>() {
                            @Override
                            public void onResponse(Call<Bid> call, Response<Bid> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    Toast.makeText(BidInfoActivity.this, "अद्यतन सफलता", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Bid> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(BidInfoActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(BidInfoActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            UserService.UserApi userApi = UserService.getUserApiInstance();
            Call<User> call = userApi.getUser(bid.getLead().getUserId());
            if (NetworkUtil.getConnectivityStatus(this)) {
                String s = "Please wait...";
                if (checkLanguage()) {
                    s = "कृपया प्रतीक्षा करें...";
                }
                final CustomProgressDialog pd = new CustomProgressDialog(BidInfoActivity.this, s);
                pd.show();
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        pd.dismiss();
                        if (response.code() == 200) {
                            binding.username.setText(response.body().getName());
                            binding.resusername.setText("Send Response to " + response.body().getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        pd.dismiss();
                        Toast.makeText(BidInfoActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            binding.etRemark.setText(bid.getBid().getRemark());
            String str[] = bid.getLead().getPickUpAddress().split(",");
            String name = str[str.length - 2];
            str = bid.getLead().getDeliveryAddress().split(",");
            name += " to " + str[str.length - 2];
            binding.tvfrom.setText(name);
            binding.tvmaterial.setText(bid.getLead().getTypeOfMaterial());
            binding.tvweight.setText(bid.getLead().getWeight() + " ton");
            binding.etRate.setText(bid.getBid().getAmount());
            binding.etRemark.setText(bid.getBid().getRemark());
            binding.lastdate.setText(bid.getLead().getDateOfCompletion());
            binding.pickcontact.setText(bid.getLead().getContactForPickup());
            binding.delcontact.setText(bid.getLead().getContactForDelivery());
            binding.pickadd.setText(bid.getLead().getPickUpAddress());
            binding.deladd.setText(bid.getLead().getDeliveryAddress());
            binding.btnbidnow.setText("update Bid");
            if (bid.getLead().getSpecialRequirement() != null) {
                final SpecialRequirement sp = bid.getLead().getSpecialRequirement();
                binding.material2.setText(sp.getAdditionalMaterialType());
                binding.pick.setText(sp.getPickupStreet());
                binding.delivery.setText(sp.getDeliverystreet());
                binding.remark.setText(sp.getRemark());
                binding.read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (binding.read.getText().toString().equalsIgnoreCase("Read more...")) {
                            binding.read.setText("Read less...");
                            binding.line.setVisibility(View.VISIBLE);
                            if (sp.getHandelWithCare()) {
                                binding.hwc.setVisibility(View.VISIBLE);
                            }
                            binding.mt.setVisibility(View.VISIBLE);
                            binding.pc.setVisibility(View.VISIBLE);
                            binding.rm.setVisibility(View.VISIBLE);
                            binding.dv.setVisibility(View.VISIBLE);
                        } else {
                            binding.read.setText("Read more...");
                            binding.line.setVisibility(View.GONE);
                            binding.mt.setVisibility(View.GONE);
                            binding.pc.setVisibility(View.GONE);
                            binding.rm.setVisibility(View.GONE);
                            binding.dv.setVisibility(View.GONE);
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
                    String etrate = binding.etRate.getText().toString();
                    String etremark = binding.etRemark.getText().toString();
                    if (etrate.isEmpty()) {
                        binding.etRate.setError("this field can't be empty.");
                        return;
                    }
                    if (etremark.isEmpty()) {
                        binding.etRemark.setError("this field can't be empty.");
                        return;
                    }
                    if (NetworkUtil.getConnectivityStatus(BidInfoActivity.this)) {
                        finish();
                        String s = "Please wait...";
                        if (checkLanguage()) {
                            s = "कृपया प्रतीक्षा करें...";
                        }
                        final CustomProgressDialog pd = new CustomProgressDialog(BidInfoActivity.this, s);
                        pd.show();
                        final Bid bd = new Bid(bid.getBid().getBidId(), bid.getLead().getLeadId(), FirebaseAuth.getInstance().getCurrentUser().getUid(), "", etrate, etremark, bid.getLead().getDateOfCompletion());
                        BidService.BidApi bidApi = BidService.getBidApiInstance();
                        Call<Bid> call = bidApi.updateBid(bd);
                        call.enqueue(new Callback<Bid>() {
                            @Override
                            public void onResponse(Call<Bid> call, Response<Bid> response) {
                                pd.dismiss();
                                if (response.code() == 200) {
                                    Toast.makeText(BidInfoActivity.this, "update success.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Bid> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(BidInfoActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                        Toast.makeText(BidInfoActivity.this, "please enable internet connection.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
