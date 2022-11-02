package org.duckdns.nick2.connstatus;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class BatteryData {
    private final static String TAG = Global.BATTDATA;
    private static Object sLock = new Object();
    private static BatteryData sInstance = new BatteryData();
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private ArrayAdapter<String> mArrayAdapter;

    public BatteryData() {
        MyLog.log(TAG, "BatteryData()");
    }

    public static void addData(String tmp) {
        MyLog.log(TAG, "addData: " + tmp);
        synchronized (sLock) {
            try {
                sInstance.mArrayList.add(tmp);
                while (sInstance.mArrayAdapter.getCount() > 10){
                    sInstance.mArrayList.remove(0);
                }
                if (sInstance.mArrayAdapter != null) {
                    sInstance.mArrayAdapter.notifyDataSetChanged();
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "addData: " + t);
            }
        }
    }

    public static ArrayList<String> getArray() {
        MyLog.log(TAG, "getArray");
        return sInstance.mArrayList;
    }

    public static void setAdapter(ArrayAdapter<String> aa) {
        MyLog.log(TAG, "setAdapter");
        sInstance.mArrayAdapter = aa;
    }
}
