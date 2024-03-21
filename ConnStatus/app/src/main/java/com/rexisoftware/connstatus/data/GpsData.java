package com.rexisoftware.connstatus.data;

import android.widget.ArrayAdapter;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;

import java.util.ArrayList;

public class GpsData {
    private final static String TAG = Global.GPSDATA;
    private final static Object sLock = new Object();
    private final static GpsData sInstance = new GpsData();
    private final ArrayList<String> mArrayList = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;
    private String mStatus = "?";
    private String mLevel = "?";

    private GpsData() {
    }


    public static void addData(String tmp) {
        synchronized (sLock) {
            try {
                sInstance.mArrayList.add(tmp);
                while (sInstance.mArrayList.size() > 15) {
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

    public static ArrayList<String> getDataArray() {
        return sInstance.mArrayList;
    }

    public static void setAdapter(ArrayAdapter<String> aa) {
        sInstance.mArrayAdapter = aa;
    }

    public static String getStatus() {
        return "Status: " + sInstance.mStatus;
    }

    public static void setStatus(String s) {
        sInstance.mStatus = s;
    }

    public static String getLevel() {
        return "Level: " + sInstance.mLevel;
    }

    public static void setLevel(String s) {
        sInstance.mLevel = s;
    }
}
