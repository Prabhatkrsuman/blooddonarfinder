package com.example.blooddonarfinder.fragment;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.blooddonarfinder.DonateFragment;
import com.example.blooddonarfinder.NewsFeedFragment;
import com.example.blooddonarfinder.PostFragment;
import com.example.blooddonarfinder.R;
import com.example.blooddonarfinder.RequestFragment;
import com.google.android.material.tabs.TabLayout;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    int totalTabs;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1,R.string.tab_text_2, R.string.tab_text_3};
    private final HomeFragment mContext;

    public SectionsPagerAdapter(HomeFragment context, FragmentManager fm ,int totalTabs) {
        super(fm);
        mContext = context;
        this.totalTabs = totalTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                RequestFragment requestFragment = new RequestFragment();
                return requestFragment;
            case 1:
                NewsFeedFragment newsFeedFragment = new NewsFeedFragment();
                return newsFeedFragment;
            case 2:
                DonateFragment donateFragment = new DonateFragment();
                return donateFragment;
            default:
                return null;
        }
      //  return PlaceholderFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);

    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return totalTabs;
    }

}
