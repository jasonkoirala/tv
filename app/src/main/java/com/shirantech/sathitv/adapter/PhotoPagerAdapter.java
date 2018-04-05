package com.shirantech.sathitv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shirantech.sathitv.fragment.FullScreenPhotoFragment;
import com.shirantech.sathitv.model.Photo;

import java.util.List;

/**
 * Adapter for showing photo gallery
 */
public class PhotoPagerAdapter extends FragmentPagerAdapter {

    private final List<Photo> mPhotoList;

    public PhotoPagerAdapter(final FragmentManager fm, final List<Photo> photoList) {
        super(fm);
        this.mPhotoList = photoList;
    }

    @Override
    public Fragment getItem(int position) {
        return FullScreenPhotoFragment.newInstance(mPhotoList.get(position));
    }

    @Override
    public int getCount() {
        return mPhotoList.size();
    }
}
