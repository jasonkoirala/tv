package com.shirantech.sathitv.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shirantech.sathitv.fragment.ShowAllPhotosFragment;
import com.shirantech.sathitv.model.ModelProfile;
import com.shirantech.sathitv.utils.AppLog;

import java.util.List;

/**
 * Created by jyoshna on 1/8/16.
 */
public class ShowAllPhotosPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = ShowAllPhotosPagerAdapter.class.getName();

    private List<ModelProfile> modelPhotosList;


    public ShowAllPhotosPagerAdapter(FragmentManager fm, List<ModelProfile> photoList) {
        super(fm);
        modelPhotosList = photoList;
    }

    @Override
        public Fragment getItem(int position) {
        ModelProfile modelProfile = modelPhotosList.get(position);
        AppLog.showLog(TAG, "position "+position+ "image : "+modelProfile.getImage());
        return ShowAllPhotosFragment.newInstance(modelProfile.getImage());
    }

    @Override
    public int getCount() {
        AppLog.showLog(TAG, "size "+modelPhotosList.size());
        return modelPhotosList.size();
    }
}
