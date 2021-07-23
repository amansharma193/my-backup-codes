package com.transportervendor.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.BidInfoActivity;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.Filter;
import com.transportervendor.FilterAdapter;
import com.transportervendor.NetworkUtil;
import com.transportervendor.R;
import com.transportervendor.SortByName;
import com.transportervendor.adapter.MarketLeadAdapter;
import com.transportervendor.apis.LeadsService;
import com.transportervendor.apis.StateService;
import com.transportervendor.beans.Leads;
import com.transportervendor.beans.State;
import com.transportervendor.databinding.DialogViewBinding;
import com.transportervendor.databinding.FragmentMarketBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MarketFragment extends Fragment {
    FragmentMarketBinding fragment;
    MarketLeadAdapter adapter;
    ArrayList<String> lid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = FragmentMarketBinding.inflate(LayoutInflater.from(getContext()));
        if (checkLanguage()){
            fragment.txt.setText("लोड");
        }
        View v = fragment.getRoot();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter = new MarketLeadAdapter(getContext(), new ArrayList<Leads>());
        final LeadsService.LeadsApi leadsApi = LeadsService.getLeadsApiInstance();
        Call<ArrayList<String>> call1 = leadsApi.getcurrentLeadsId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final Call<ArrayList<Leads>> call2 = leadsApi.getAllLeads();
        if (NetworkUtil.getConnectivityStatus(getContext())) {
            String s="Please wait...";
            if (checkLanguage()){
                s="कृपया प्रतीक्षा करें...";
            }
            final CustomProgressDialog pd=new CustomProgressDialog(getContext(),s);
            pd.show();
            call2.enqueue(new Callback<ArrayList<Leads>>() {
                @Override
                public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        ArrayList<Leads> al = response.body();
                        Collections.sort(al);
                        fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                        adapter = new MarketLeadAdapter(getContext(), al);
                        fragment.rv.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            pd.show();
            call1.enqueue(new Callback<ArrayList<String>>() {
                @Override
                public void onResponse(Call<ArrayList<String>> call, Response<ArrayList<String>> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        lid = response.body();
                        if (lid == null)
                            lid = new ArrayList<>();
                        adapter.setLid(lid);
                        adapter.notifyDataSetChanged();
                        fragment.rv.setAdapter(adapter);
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<String>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            fragment.filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogViewBinding alb = DialogViewBinding.inflate(LayoutInflater.from(getContext()));
                    StateService.StateApi stateApi = StateService.getStateApiInstance();
                    Call<ArrayList<State>> call = stateApi.getState();
                    alb.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    if (NetworkUtil.getConnectivityStatus(getContext())) {
                        pd.show();
                        call.enqueue(new Callback<ArrayList<State>>() {
                            @Override
                            public void onResponse(Call<ArrayList<State>> call, Response<ArrayList<State>> response) {
                                pd.dismiss();
                                if (response.isSuccessful()) {
                                    ArrayList<State>al=response.body();
                                    Collections.sort(al,new SortByName());
                                    FilterAdapter adapter = new FilterAdapter(getContext(),al);
                                    alb.rv.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onFailure(Call<ArrayList<State>> call, Throwable t) {
                                pd.dismiss();
                                Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                    }
                    final AlertDialog ab = new AlertDialog.Builder(getContext()).create();
                    ab.setView(alb.getRoot());
                    ab.setTitle("Filter");
                    if(checkLanguage()){
                        alb.al.setText("सभी");
                        alb.btnupdate.setText("लागू करे");
                        alb.btncancel.setText("रद्द करे");
                        ab.setTitle("फ़िल्टर");
                    }
                    alb.btncancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ab.dismiss();
                        }
                    });
                    alb.btnupdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!(alb.all.isChecked())) {
                                LeadsService.LeadsApi leadsApi1 = LeadsService.getLeadsApiInstance();
                                Call<ArrayList<Leads>> call = leadsApi1.getfilteredLeads(Filter.getInstance());
                                if (NetworkUtil.getConnectivityStatus(getContext())) {
                                    String s="Please wait...";
                                    if (checkLanguage()){
                                        s="कृपया प्रतीक्षा करें...";
                                    }
                                    final CustomProgressDialog pd=new CustomProgressDialog(getContext(),s);
                                    pd.show();
                                    call.enqueue(new Callback<ArrayList<Leads>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                                            pd.dismiss();
                                            if (response.isSuccessful()) {
                                                adapter = new MarketLeadAdapter(getContext(), response.body());
                                                adapter.setLid(lid);
                                                fragment.rv.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                ab.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else
                                    Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
                            }else{
                                LeadsService.LeadsApi leadsApi1=LeadsService.getLeadsApiInstance();
                                Call<ArrayList<Leads>>call5=leadsApi1.getAllLeads();
                                if(NetworkUtil.getConnectivityStatus(getContext())) {
                                    String s="Please wait...";
                                    if (checkLanguage()){
                                        s="कृपया प्रतीक्षा करें...";
                                    }
                                    final CustomProgressDialog pd=new CustomProgressDialog(getContext(),s);
                                    pd.show();
                                    call5.enqueue(new Callback<ArrayList<Leads>>() {
                                        @Override
                                        public void onResponse(Call<ArrayList<Leads>> call, Response<ArrayList<Leads>> response) {
                                            pd.dismiss();
                                            if (response.code() == 200) {
                                                ArrayList<Leads> al = response.body();
                                                fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                                                adapter = new MarketLeadAdapter(getContext(), al);
                                                fragment.rv.setAdapter(adapter);
                                                adapter.notifyDataSetChanged();
                                                ab.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ArrayList<Leads>> call, Throwable t) {
                                            pd.dismiss();
                                            Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    Toast.makeText(getContext(), "please enable internet connection.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    ab.show();
                }
            });
        } else
            Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
    }
    private boolean checkLanguage() {
        SharedPreferences mprefs = getActivity().getSharedPreferences("Transporter", MODE_PRIVATE);
        String s = mprefs.getString("language", "");
        if (s.equalsIgnoreCase("hindi")) {
            return true;
        }
        return false;
    }
}
