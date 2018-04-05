package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.InterviewFragment;
import com.shirantech.sathitv.fragment.VODFragment;

/**
 * Created by jyoshna on 1/27/16.
 */
public class EntertainmentViewPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGES = 3;
    private Context context;

    public EntertainmentViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = VODFragment.newInstance(0);
                break;
            case 1:
                fragment = VODFragment.newInstance(1);
                break;
            case 2:
                fragment = InterviewFragment.newInstance();
                break;
    }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
            return context.getResources().getString(R.string.title_vod);
            case 1:
                return context.getResources().getString(R.string.title_mod);
            case 2:
                return context.getResources().getString(R.string.title_nrn_bishes);
        }
        return null;
    }
}
