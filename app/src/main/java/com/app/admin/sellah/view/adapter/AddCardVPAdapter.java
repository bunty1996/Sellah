package com.app.admin.sellah.view.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class AddCardVPAdapter extends FragmentPagerAdapter {

    ArrayList<Fragment> fragments;
    public AddCardVPAdapter(FragmentManager fm,ArrayList<Fragment> fragments) {
        super(fm);
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int pos) {

        switch (pos) {
            case 0:
                return fragments.get(0);
            case 1:
                return fragments.get(1);
            case 2:
                return fragments.get(2);
            case 3:
                return fragments.get(3);
            default:
                return fragments.get(0);
        }
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
