package com.e.skychat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.e.skychat.fargment.ChatFragment;
import com.e.skychat.fargment.GroupFragment;
import com.e.skychat.fargment.RequestFragment;
import com.e.skychat.fargment.StoryFragment;

public class TabAccessorAdapter extends FragmentPagerAdapter {

    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0: fragment = new ChatFragment();break;
            case 1: fragment = new GroupFragment();break;
            case 2: fragment = new StoryFragment();break;
            case 3: fragment = new RequestFragment();break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String tabTitle = "";
        if(position == 0)
            tabTitle = "CHAT";
        else if(position == 1)
            tabTitle = "GROUP";
        else if(position == 2)
            tabTitle = "STORY";
        else if(position == 3)
            tabTitle = "REQUEST";

        return tabTitle;
    }
}
