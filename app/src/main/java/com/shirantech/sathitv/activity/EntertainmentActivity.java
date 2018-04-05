package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.EntertainmentViewPagerAdapter;

/**
 * {@link android.app.Activity} showing ui for Janam Kundali request.
 */
public class EntertainmentActivity extends BaseActivity {

    TabLayout tabs;
    ViewPager viewPager;
    EntertainmentViewPagerAdapter entertainmentViewPagerAdapter;



    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link EntertainmentActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, EntertainmentActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entertainment);
        setUpToolbar();
        tabs = (TabLayout)  findViewById(R.id.tabsEntertainment);
        viewPager = (ViewPager)  findViewById(R.id.viewpagerEntertainment);
        entertainmentViewPagerAdapter = new EntertainmentViewPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(entertainmentViewPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(viewPager);


    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_entertainment));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
