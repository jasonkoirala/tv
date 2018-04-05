package com.shirantech.sathitv.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.shirantech.sathitv.R;
import com.shirantech.sathitv.adapter.AstrologyViewPagerAdapter;

/**
 * {@link android.app.Activity} showing ui for Janam Kundali request.
 */
public class AstrologyActivity extends BaseActivity {

    TabLayout tabs;
    ViewPager viewPager;

    AstrologyViewPagerAdapter astrologyViewPagerAdapter;

    /**
     * Get the {@link Intent} to start this activity
     *
     * @param context context to initialize the Intent.
     * @return the Intent to start {@link AstrologyActivity}
     */
    public static Intent getStartIntent(Context context) {
        return new Intent(context, AstrologyActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_janam_kundali_v2);
        setUpToolbar();
        tabs = (TabLayout)  findViewById(R.id.tabs_more_apps);
        viewPager = (ViewPager)  findViewById(R.id.viewpager_more_apps);
        astrologyViewPagerAdapter = new AstrologyViewPagerAdapter(getSupportFragmentManager(),this);
        viewPager.setAdapter(astrologyViewPagerAdapter);
        /*TabLayout.Tab tab0 = tabs.getTabAt(0);
        tab0.setCustomView(astrologyViewPagerAdapter.getTabView(getResources().getString(R.string.horoscope)));
        TabLayout.Tab tab1 = tabs.getTabAt(1);
        tab1.setCustomView(astrologyViewPagerAdapter.getTabView(getResources().getString(R.string.dashboard_janam_kundali)));*/
//        viewPager.setCurrentItem(0);
        tabs.setupWithViewPager(viewPager);


    }

    private void setUpToolbar() {
        final Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.dashboard_astrology));
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
    public boolean onCreateOptionsMenu(Menu menu) {
   /*     getMenuInflater().inflate(R.menu.menu_rashifal_request, menu);
        menu.findItem(R.id.action_submit_request);*/
        return true;
    }


    /*//**
     * Show/hide submit action in menu
     *
     * @param show <code>true</code> to show the menu, <code>false</code> otherwise
     *//*
    private void showSubmitMenu(final boolean show) {
        showSubmitMenu = show;
        invalidateOptionsMenu();
    }*/


    @Override
    protected void onStop() {
        super.onStop();
    }
}
