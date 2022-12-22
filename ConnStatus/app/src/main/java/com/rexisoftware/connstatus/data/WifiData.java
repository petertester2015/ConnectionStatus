package com.rexisoftware.connstatus.data;

import android.widget.ArrayAdapter;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;

import java.util.ArrayList;

public class WifiData {
    private final static String TAG = Global.WIFIDATA;
    private final static Object sLock = new Object();
    private final static WifiData sInstance = new WifiData();
    private final ArrayList<String> mArrayList = new ArrayList<>();
    private ArrayAdapter<String> mArrayAdapter;
    private String mSSID;
    private String mIP;
    private String mSPEED;
    private String mFREQ;
    private String mRSSI;

    private WifiData() {
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

    public static String getSsid() {
        return "SSID: " + sInstance.mSSID;
    }

    public static void setSsid(String x) {
        sInstance.mSSID = x;
    }

    public static String getIp() {
        return "IP: " + sInstance.mIP;
    }

    public static void setIp(String x) {
        sInstance.mIP = x;
    }

    public static String getSpeed() {
        return "Speed: " + sInstance.mSPEED;
    }

    public static void setSpeed(String x) {
        sInstance.mSPEED = x;
    }

    public static String getFreq() {
        return "Freq: " + sInstance.mFREQ;
    }

    public static void setFreq(String x) {
        sInstance.mFREQ = x;
    }

    public static String getRssi() {
        return "RSSI: " + sInstance.mRSSI;
    }

    public static void setRssi(String x) {
        sInstance.mRSSI = x;
    }
}
