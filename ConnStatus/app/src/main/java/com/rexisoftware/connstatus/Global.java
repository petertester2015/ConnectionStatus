package com.rexisoftware.connstatus;

import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Global {
    public static final String BROADCAST = "connectionstatus";
    public static final String CATEGORY = "cat";
    public static final String MESSAGE = "msg";

    public static final String CAT_BATTERY = "batt";
    public static final String CAT_PHONE_STATE = "phstate";
    public static final String CAT_MYSERVICE = "myservice";
    public static final String CAT_MAIN = "main";
    public static final String CAT_STATIONS = "stationthread";
    public static final String CAT_PAG_ADAPTER = "pageadapt";
    public static final String BATTDATA = "battdata";
    public static final String CELLDATA = "celldata";
    public static final String SETTINGSDATA = "settingdata";
    public static final String WIFIDATA = "wifidata";
    public static final String CAT_WIFI = "wifi";
    public static final String LOGDATA = "logdata";
    public static final String CAT_CLOCK = "clk";
    public static final String CAT_WIFI_CM = "wificm";
    //public static final String TAG = "mylog";
    public static final String NETWORKDATA = "netwdata";
    public static final String CAT_NETWORK = "netw";
    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss.SSS");
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("HHmmss");
    private static final long sAppStartTime = System.currentTimeMillis();
    private static final long sUptimeStartTime = SystemClock.uptimeMillis();
    private static MainActivity sActivity;
    private static long sActivityStartTime = System.currentTimeMillis();

    public static long getUptimeStartTime() {
        return sUptimeStartTime;
    }

    public static long getAppStartTime() {
        return sAppStartTime;
    }

    public static long getActivityStartTime() {
        return sActivityStartTime;
    }

    public static void resetActivityStartTime() {

        Global.sActivityStartTime = System.currentTimeMillis();
    }

    public static MainActivity getActivity() {
        return sActivity;
    }

    public static void setActivity(MainActivity sActivity) {
        Global.sActivity = sActivity;
    }

    public static String getTimeDate() {
        return sdf1.format(new Date());
    }

    @SuppressWarnings("unused")
    public static String getTimeMillis() {
        return sdf2.format(new Date());
    }

    public static String getTimeSec() {
        return sdf3.format(new Date());
    }

    public static String getTimeSec(long t) {
        return sdf3.format(new Date(t));
    }

    public static String getFullTime(long t) {
        return sdf1.format(new Date(t));
    }
}
