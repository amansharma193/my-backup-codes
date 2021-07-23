package com.transportervendor.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.transportervendor.R;
import com.transportervendor.adapter.AllBidsAdapter;
import com.transportervendor.adapter.CompletedLeadsAdapter;
import com.transportervendor.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {
    FragmentHistoryBinding fragment;
    RecyclerView.Adapter<CompletedLeadsAdapter.CompletedLeadsViewHolder> adapter;
    RecyclerView.Adapter<AllBidsAdapter.AllBidsViewHolder> adapter1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragment = FragmentHistoryBinding.inflate(LayoutInflater.from(getContext()));
        View v = fragment.getRoot();
        if (checkLanguage()){
            fragment.tablayout.addTab(fragment.tablayout.newTab().setText("पूर्ण भार"),0,true);
            fragment.tablayout.addTab(fragment.tablayout.newTab().setText("सभी बोलियां"),1,false);
        }else{
            fragment.tablayout.addTab(fragment.tablayout.newTab().setText("Completed Leads"),0,true);
            fragment.tablayout.addTab(fragment.tablayout.newTab().setText("All Bids"),1,false);
        }
        fragment.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }
    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(new CompletedLeadsFragment());
                break;
            case 1 :
                replaceFragment(new AllBidsFragment());
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
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
