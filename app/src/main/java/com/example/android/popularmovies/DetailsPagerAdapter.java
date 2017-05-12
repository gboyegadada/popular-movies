package com.example.android.popularmovies;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.List;

/**
 * Created by Gboyega.Dada on 5/10/2017.
 */

public class DetailsPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private List<String> mTitles;

    public DetailsPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments, List<String> labels) {
        super(supportFragmentManager);
        this.mFragments = fragments;
        this.mTitles = labels;
    }


    @Override
    public int getCount() {

        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return this.mFragments.get(position);
    }


    @Override
    public Parcelable saveState() {

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mTitles.get(position);
    }
}
