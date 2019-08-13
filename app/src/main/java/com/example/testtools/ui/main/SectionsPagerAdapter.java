package com.example.testtools.ui.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.testtools.Fragment.Fragment_CommSetting;
import com.example.testtools.R;
import com.example.testtools.Fragment.Fragment_IP_Register;
import com.example.testtools.Fragment.Fragment_Stress_Test;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_stress_test, R.string.tab_IP_register,R.string.tab_CommSetting};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                Fragment_Stress_Test stress_test = new Fragment_Stress_Test();
                return stress_test;
            case 1:
                Fragment_IP_Register fragmentIp_register = new Fragment_IP_Register();
                return fragmentIp_register;
            case 2:
                Fragment_CommSetting commSetting = new Fragment_CommSetting();
                return commSetting;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }
}