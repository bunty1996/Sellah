package com.app.admin.sellah.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ChatVPAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragmentArray;

    public ChatVPAdapter(FragmentManager fm,ArrayList<Fragment> fragmentArray) {
        super(fm);
        this.fragmentArray=fragmentArray;
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentArray.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArray.size();
    }
}
