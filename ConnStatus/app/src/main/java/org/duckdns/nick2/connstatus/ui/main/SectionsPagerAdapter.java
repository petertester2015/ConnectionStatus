package org.duckdns.nick2.connstatus.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.duckdns.nick2.connstatus.BatteryFragment;
import org.duckdns.nick2.connstatus.CellularFragment;
import org.duckdns.nick2.connstatus.ClockFragment;
import org.duckdns.nick2.connstatus.R;
import org.duckdns.nick2.connstatus.SettingsFragment;
import org.duckdns.nick2.connstatus.WifiFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_settings, R.string.tab_text_clock, R.string.tab_text_wifi, R.string.tab_text_cell, R.string.tab_text_battery};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 4:
                return BatteryFragment.newInstance("", "");
            case 3:
                return CellularFragment.newInstance("", "");
            case 2:
                return WifiFragment.newInstance("", "");
            case 1:
                return ClockFragment.newInstance("", "");
            case 0:
                return SettingsFragment.newInstance("", "");
            default:
                return PlaceholderFragment.newInstance(position + 1);
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