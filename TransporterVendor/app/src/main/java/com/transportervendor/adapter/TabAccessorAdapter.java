package com.transportervendor.adapter;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.transportervendor.HomeActivity;
import com.transportervendor.fragment.HistoryFragment;
import com.transportervendor.fragment.HomeFragment;
import com.transportervendor.fragment.MarketFragment;

import static android.content.Context.MODE_PRIVATE;

public class TabAccessorAdapter extends FragmentPagerAdapter {
    SharedPreferences mprefs;
    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior, SharedPreferences mprefs) {
        super(fm, behavior);
        this.mprefs=mprefs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;

        switch (position){
            case 0: fragment=new HomeFragment();break;
            case 1: fragment=new MarketFragment();break;
            case 2: fragment=new HistoryFragment();break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        if (checkLanguage()) {
            if (position == 0) {
                title = "होम";
            } else if (position == 1) {
                title = "मार्किट";
            } else if (position == 2) {
                title = "हिस्ट्री";
            }
        }
        else{
            if (position == 0) {
                title = "Home";
            } else if (position == 1) {
                title = "Market";
            } else if (position == 2) {
                title = "History";
            }
        }
        return title;
    }
    public  boolean checkLanguage() {
        String s=mprefs.getString("language","");
        if (s.equalsIgnoreCase("hindi")){
            return true;
        }
        return false;
    }

}
