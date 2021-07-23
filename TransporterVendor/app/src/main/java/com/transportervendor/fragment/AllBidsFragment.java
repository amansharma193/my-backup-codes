package com.transportervendor.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.transportervendor.CustomProgressDialog;
import com.transportervendor.HomeActivity;
import com.transportervendor.NetworkUtil;
import com.transportervendor.adapter.AllBidsAdapter;
import com.transportervendor.apis.BidService;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.databinding.AllBidsFragmentBinding;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBidsFragment extends Fragment {
    AllBidsFragmentBinding fragment;
    AllBidsAdapter adapter1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment=AllBidsFragmentBinding.inflate(LayoutInflater.from(getContext()));
        BidService.BidApi bidApi = BidService.getBidApiInstance();
        Call<ArrayList<BidWithLead>> call = bidApi.getAllBids(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(NetworkUtil.getConnectivityStatus(getContext())) {
            String s="Please wait...";
            if (checkLanguage()){
                s="कृपया प्रतीक्षा करें...";
            }
            final CustomProgressDialog pd=new CustomProgressDialog(getContext(),s);
            pd.show();
            call.enqueue(new Callback<ArrayList<BidWithLead>>() {
                @Override
                public void onResponse(Call<ArrayList<BidWithLead>> call, Response<ArrayList<BidWithLead>> response) {
                    pd.dismiss();
                    if (response.code() == 200) {
                        ArrayList<BidWithLead> al = response.body();
                        Collections.sort(al);
                        adapter1 = new AllBidsAdapter(getContext(), al);
                        fragment.rv.setAdapter(adapter1);
                        fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BidWithLead>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(getContext(), "please enable internet connection.", Toast.LENGTH_SHORT).show();
        }
        return fragment.getRoot();
    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =getActivity().getSharedPreferences("Transporter",getActivity().MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}
