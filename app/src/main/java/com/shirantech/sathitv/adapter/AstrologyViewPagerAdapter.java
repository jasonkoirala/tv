package com.shirantech.sathitv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.fragment.HoroscopeListFragment;
import com.shirantech.sathitv.fragment.JanamKundaliFragment;
import com.shirantech.sathitv.widget.CustomFontTextView;

/**
 * Created by prativa on 7/22/15.
 */
public class AstrologyViewPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;


    public AstrologyViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = HoroscopeListFragment.newInstance();
                break;
            case 1:
                fragment = JanamKundaliFragment.newInstance();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {

        return 2;

    }

 /*   public View getTabView(String name) {
        // Given you have a custom layout in `res/layout/custom_tab.xml` with a TextView and ImageView
        View v = LayoutInflater.from(context).inflate(R.layout.tab_indicator, null);
        CustomFontTextView customTextView = (CustomFontTextView) v.findViewById(R.id.tv_group_id);
        customTextView.setTextSize(16);

        customTextView.setText(name);

        return v;
    }
*/

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getResources().getString(R.string.horoscope);

            case 1:
                return context.getResources().getString(R.string.dashboard_janam_kundali);
        }
        return null;
    }
}
