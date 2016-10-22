package com.sin.cainiao.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MainFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment>fragmentList = new ArrayList<>();

    public MainFragmentAdapter(List<Fragment> fragments,FragmentManager fm) {
        super(fm);
        this.fragmentList = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragmentList.get(position);
    }

    public void swapData(List<Fragment> fragments){
        this.fragmentList = fragments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "SECTION 1";
            case 1:
                return "SECTION 2";
            case 2:
                return "SECTION 3";
        }
        return null;
    }

}
