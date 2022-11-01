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

    private static SimpleDateFormat sdf1 = new SimpleDateFormat(
            "yyyyMMdd_HHmmss");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat(
            "HHmmss.SSS");

    public static String getTimeDate() {
        return sdf1.format(new Date());
    }

    public static String getTimeMillis() {
        return sdf2.format(new Date());
    }
}