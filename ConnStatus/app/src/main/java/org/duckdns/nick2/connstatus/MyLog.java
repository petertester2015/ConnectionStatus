package org.duckdns.nick2.connstatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyLog extends Thread {
    public static final Charset sAscii = StandardCharsets.US_ASCII;
    private final static String TAG = "ConnStat";
    private static final boolean LOG2ANDROIDLOG = true;
    private static final MyLog instance = new MyLog();
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private final Object mLock = new Object();
    private final StringBuffer mEverything = new StringBuffer();
    private Context mContext = null;
    private ArrayList<String> mIncoming = new ArrayList<String>();
    private FileOutputStream mFos;
    private boolean mCont = true;
    private File mDataFolder = null;

    private MyLog() {
        mIncoming.add("Log started.");
        start();
    }

    public static void log(String tag, String message) {
        try {
            LogEntry tmp = new LogEntry(tag, message);
            instance.logimpl("time=" + Global.getTimeSec(tmp.getmTime()) + " cat=" + tag + " msg=" + message);
            Intent i = new Intent(Global.BROADCAST);
            i.putExtra(Global.CATEGORY, tag);
            i.putExtra(Global.MESSAGE, Global.getTimeSec(tmp.getmTime()) + ": " + message);
            LocalBroadcastManager.getInstance(MyService.getCurrent()).sendBroadcast(i);
            Log.v(TAG, "cat=" + tag + " msg=" + message);
        } catch (Throwable t) {
            Log.i(TAG, "LocalBroadcast: " + t);
        }
    }

    public static File getDataFolder() {
        return instance.mDataFolder;
    }

    public static void log(String logString) {
        instance.logimpl(logString);
    }

    public static void endlog() {
        instance.endlogimpl();
    }

    public static String getlog() {
        return instance.getlogimpl();
    }

    public static String cleanString(String str, String defaultString) {
        if (str == null) return defaultString;
        if (str.length() < 1) return defaultString;
        int i;
        byte[] tmp = str.getBytes(sAscii);
        for (i = 0; i < tmp.length; i++) {
            if (!isChar(tmp[i]) && !isNum(tmp[i])) tmp[i] = 'x';
        }

        return new String(tmp);
    }

    public static boolean isNum(char c) {
        return isNum((byte) c);
    }

    public static boolean isNum(byte b) {
        if (b < '0') return false;
        return b <= '9';
    }

    public static boolean isChar(char c) {
        return isChar((byte) c);
    }

    public static boolean isChar(byte b) {
        if (b < 'A') return false;
        if (b > 'z') return false;
        if (b <= 'Z') return true;
        return b >= 'a';
    }

    private String getlogimpl() {
        return mEverything.toString();
    }

    private void endlogimpl() {
        logimpl("Request to close log file.");
        mCont = false;
    }

    private void logimpl(String logString) {
        synchronized (mLock) {
            String tmp = "" + System.currentTimeMillis() + ":" + logString;
            mIncoming.add(tmp);
        }
    }

    public void run() {
        MyLog.log("Log thread started.");
        openfile();
        int countdown = 5;
        while (countdown > 0) {
            SystemClock.sleep(1000);
            moveIncomingToEverything();
            if (!mCont) {
                countdown--;
                mEverything.append("countdown=" + countdown + "\n");
            }
        }
        mEverything.append("Closing log file.");
        try {
            mFos.close();
        } catch (Throwable t) {
            Log.d(Global.TAG, "close file", t);
            mEverything.append("Close file: " + t + "\n");
        }
        mFos = null;
        mEverything.append("Logging terminated.\n");

        SystemClock.sleep(1000);
        System.exit(0);
    }

    private void moveIncomingToEverything() {
        ArrayList<String> tmp = null;
        synchronized (mLock) {
            if (mIncoming.size() > 0) {
                tmp = mIncoming;
                mIncoming = new ArrayList<String>();
            }
        }
        if (tmp != null) {
            for (String tmp2 : tmp) {
                if (LOG2ANDROIDLOG) {
                    Log.d(Global.TAG, tmp2);
                }
                String tmp3 = tmp2 + "\n";
                mEverything.append(tmp3);
                if (mFos != null) {
                    try {
                        mFos.write(tmp3.getBytes());
                    } catch (Throwable t) {
                        mEverything.append("file write: " + t + "\n");
                    }
                }
            }
            if (mFos != null) {
                try {
                    mFos.flush();
                    mFos.getFD().sync();
                } catch (Throwable t) {
                    mEverything.append("file flush: " + t + "\n");
                }
            }
        }
    }

    private void openfile() {
        MyLog.log("Open log file.");
        waitForContext();
        try {
            String name = "connstatus_" + "_" + sdf.format(new Date()) + ".txt";
            mDataFolder = mContext.getExternalFilesDir(null);
            mDataFolder.mkdirs();
            File f3 = new File(mDataFolder, name);
            mFos = new FileOutputStream(f3);
        } catch (Throwable t) {
            Log.e(Global.TAG, "openfile", t);
            mEverything.append("openfile:" + t);
        }
    }

    private void waitForContext() {
        MyLog.log("Waiting for context...");
        while (mContext == null) {
            SystemClock.sleep(1000);
            Activity a = Global.getActivity();
            if (a != null) {
                mContext = a.getApplicationContext();
            }
        }
        MyLog.log("Context found.");
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