package org.duckdns.nick2.connstatus.data;

import android.widget.ArrayAdapter;

import org.duckdns.nick2.connstatus.Global;
import org.duckdns.nick2.connstatus.MyLog;

import java.util.ArrayList;

public class NetworkData {
    private final static String TAG = Global.NETWORKDATA;
    private final static Object sLock = new Object();
    private final static NetworkData sInstance = new NetworkData();
    private final ArrayList<String> mArrayList = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;

    private NetworkData() {
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
