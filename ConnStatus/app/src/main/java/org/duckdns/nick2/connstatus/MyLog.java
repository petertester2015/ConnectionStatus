package org.duckdns.nick2.connstatus;

import android.util.Log;

public class MyLog {
    public static void log(String tag, String message) {
        Log.i("ConnStat", tag + ":" + message);
    }
}
