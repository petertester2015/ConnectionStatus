package org.duckdns.nick2.connstatus;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BatteryData {
    private static Object sLock = new Object();
    private static BatteryData sInstance = new BatteryData();
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;

    public BatteryData() {
        mArrayAdapter = new ArrayAdapter<String>(MainActivity.sCurr.getApplicationContext(), android.R.layout.simple_list_item_1, android.R.id.text1, mArrayList);
    }

    public static void addData(String tmp) {
        synchronized (sLock) {
            sInstance.mArrayList.add(tmp);
            sInstance.mArrayAdapter.notifyDataSetChanged();
        }
    }

    public static ArrayAdapter<String> getAdapter() {
        return sInstance.mArrayAdapter;
    }
}
