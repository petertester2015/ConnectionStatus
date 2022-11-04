package org.duckdns.nick2.connstatus;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class CellularData {
    private final static String TAG = Global.CELLDATA;
    private final static Object sLock = new Object();
    private final static CellularData sInstance = new CellularData();
    private final ArrayList<String> mArrayList = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;

    private CellularData() {
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
}

