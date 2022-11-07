package org.duckdns.nick2.connstatus;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyLog {
    private final static String TAG = "ConnStat";

    public static void log(String tag, String message) {
        try {
            LogEntry tmp = new LogEntry(tag, message);
            Intent i = new Intent(Global.BROADCAST);
            i.putExtra(Global.CATEGORY, tag);
            i.putExtra(Global.MESSAGE, Global.getTimeSec(tmp.getmTime()) + ": " + message);
            LocalBroadcastManager.getInstance(MyService.getCurrent()).sendBroadcast(i);
            Log.v(TAG, "cat=" + tag + " msg=" + message);
        } catch (Throwable t) {
            Log.i(TAG, "LocalBroadcast: " + t);
        }
    }
}

class LogEntry {
    private final long mTime;
    private final String mTag;
    private final String mMsg;

    private LogEntry() {
        mTime = System.currentTimeMillis();
        mTag = "";
        mMsg = "";
    }

    public LogEntry(String tag, String msg) {
        mTime = System.currentTimeMillis();
        mTag = tag;
        mMsg = msg;
    }

    public long getmTime() {
        return mTime;
    }

    public String getmTag() {
        return mTag;
    }

    public String getmMsg() {
        return mMsg;
    }
}