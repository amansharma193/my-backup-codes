package com.foodbrigade.firechat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.foodbrigade.firechat.fragment.ChatFragment;
import com.foodbrigade.firechat.fragment.ContactsFragment;
import com.foodbrigade.firechat.fragment.GroupFragment;
import com.foodbrigade.firechat.fragment.RequestFragment;
import com.foodbrigade.firechat.fragment.StoryFragment;

public class TabAccessorAdapter extends FragmentPagerAdapter {
    public TabAccessorAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment=null;
        switch (position){
            case 0: fragment=new ChatFragment();break;
            case 1: fragment=new GroupFragment();break;
            case 2: fragment=new StoryFragment();break;
            case 3: fragment=new RequestFragment();break;
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
        String title="";
        if (position==0){
            title="Chat";
        }
        else if (position==1){
            title="Group";
        }
        else if (position==2){
            title="Story";
        }
        else if (position==3){
            title="Request";
        }
        return title;
    }
}
