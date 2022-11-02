package org.duckdns.nick2.connstatus;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyLog {
    private final static String TAG = "ConnStat";

    public static void log(String tag, String message) {
        try {
            Intent i = new Intent(Global.BROADCAST);
            i.putExtra(Global.CATEGORY, tag);
            i.putExtra(Global.MESSAGE, Global.getTimeSec() + ": " + message);
            LocalBroadcastManager.getInstance(MyService.getCurrent()).sendBroadcast(i);
        } catch (Throwable t) {
            Log.i(TAG, "LocalBroadcast: " + t);
        }
    }
}
