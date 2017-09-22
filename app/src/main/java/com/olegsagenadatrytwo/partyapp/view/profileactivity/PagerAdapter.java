package com.olegsagenadatrytwo.partyapp.view.profileactivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitle = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment (Fragment fragment,String title){
        mFragmentList.add(fragment);
        mFragmentTitle.add(title);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return mFragmentList.get(position);
            case 1:
                return mFragmentList.get(position);
            case 2:
                return mFragmentList.get(position);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitle.get(position);
    }
}
