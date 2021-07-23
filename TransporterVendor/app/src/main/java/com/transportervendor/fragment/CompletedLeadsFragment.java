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
import com.transportervendor.adapter.CompletedLeadsAdapter;
import com.transportervendor.apis.LeadsService;
import com.transportervendor.beans.BidWithLead;
import com.transportervendor.databinding.CompletedFragmentBinding;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class CompletedLeadsFragment extends Fragment {
    CompletedFragmentBinding fragment;
    CompletedLeadsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment=CompletedFragmentBinding.inflate(LayoutInflater.from(getContext()));
        LeadsService.LeadsApi leadApi = LeadsService.getLeadsApiInstance();
        Call<ArrayList<BidWithLead>> call = leadApi.getCompletedLeads(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (NetworkUtil.getConnectivityStatus(getContext())) {
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
                        if(al==null)
                            al=new ArrayList<>();
                        adapter = new CompletedLeadsAdapter(getContext(), al);
                        fragment.rv.setAdapter(adapter);
                        fragment.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                        SharedPreferences mPrefs= getActivity().getSharedPreferences("Transporter",MODE_PRIVATE);
                        SharedPreferences.Editor prefsEditor = mPrefs.edit();
                        prefsEditor.putString("completed",al.size()+"");
                        prefsEditor.commit();
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<BidWithLead>> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            Toast.makeText(getContext(), "Please enable internet connection.", Toast.LENGTH_SHORT).show();
        return fragment.getRoot();
    }
    public  boolean checkLanguage() {
        SharedPreferences mprefs =getActivity().getSharedPreferences("Transporter",MODE_PRIVATE);
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }
}
