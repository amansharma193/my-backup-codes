package com.freelannceritservices.digitalsignature;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessorAdapter extends FragmentPagerAdapter {
    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0: fragment=new UsersFragment();break;
            case 1: fragment=new PublicFragment();break;
            //case 2: fragment=new RequestFragment();break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title="";
        if (position==0){
            title="Users";
        }
        else if (position==1){
            title="Public";
        }
        else if (position==2){
            title="Request";
        }
        return title;
    }
}
