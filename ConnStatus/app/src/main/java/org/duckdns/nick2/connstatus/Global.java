package org.duckdns.nick2.connstatus;

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

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss.SSS");
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("HHmmss");

    public static String getTimeDate() {
        return sdf1.format(new Date());
    }

    public static String getTimeMillis() {
        return sdf2.format(new Date());
    }

    public static String getTimeSec() {
        return sdf3.format(new Date());
    }
}
