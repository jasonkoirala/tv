package com.shirantech.sathitv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.shirantech.sathitv.fragment.BannerFragment;
import com.shirantech.sathitv.model.Banner;
import com.shirantech.sathitv.utils.AppLog;

import java.util.List;

/**
 * Created by root on 12/8/15.
 */
public class BannerPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = BannerPagerAdapter.class.getSimpleName();
    private List<Banner> bannerList;

    public BannerPagerAdapter(FragmentManager fm, List<Banner> bannerList) {
        super(fm);
        this.bannerList = bannerList;
        AppLog.showLog(TAG, "bannerListSize::" + this.bannerList.size());
    }

    @Override
    public Fragment getItem(int position) {
        AppLog.showLog(TAG, "getItem::pos:" + position);
        return BannerFragment.newInstance(bannerList.get(position));
    }

    @Override
    public int getCount() {
        return bannerList != null ? bannerList.size() : 1;
    }

}
