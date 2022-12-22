package com.rexisoftware.connstatus;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.rexisoftware.connstatus.fragments.BatteryFragment;
import com.rexisoftware.connstatus.fragments.CellularFragment;
import com.rexisoftware.connstatus.fragments.ClockFragment;
import com.rexisoftware.connstatus.fragments.LogFragment;
import com.rexisoftware.connstatus.fragments.NetworkFragment;
import com.rexisoftware.connstatus.fragments.SettingsFragment;
import com.rexisoftware.connstatus.fragments.WifiFragment;

import com.rexisoftware.connstatus.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final static String TAG = Global.CAT_PAG_ADAPTER;

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_clk, R.string.tab_text_settings, R.string.tab_text_clock, R.string.tab_text_network, R.string.tab_text_wifi, R.string.tab_text_cell, R.string.tab_text_battery};
    private static Fragment[] sFragments;
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        MyLog.log(TAG, "SectionPagerAdapter()");
        sFragments = new Fragment[TAB_TITLES.length];
        sFragments[0] = ClockFragment.newInstance("", "");
        sFragments[1] = SettingsFragment.newInstance("", "");
        sFragments[2] = LogFragment.newInstance("", "");
        sFragments[3] = NetworkFragment.newInstance("", "");
        sFragments[4] = WifiFragment.newInstance("", "");
        sFragments[5] = CellularFragment.newInstance("", "");
        sFragments[6] = BatteryFragment.newInstance("", "");
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        MyLog.log(TAG, "getItem " + position);
        return sFragments[position];
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