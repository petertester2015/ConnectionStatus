package org.duckdns.nick2.connstatus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.duckdns.nick2.connstatus.plugins.MyBattery;
import org.duckdns.nick2.connstatus.plugins.MyClock;
import org.duckdns.nick2.connstatus.plugins.MyListener;
import org.duckdns.nick2.connstatus.plugins.MyWifi;

public class MyService extends Service {
    private static final String TAG = Global.CAT_MYSERVICE;
    private static MyListener sListener;
    private static MyBattery sBattery;
    private static MyService sService;
    private static MyWifi sWifi;
    private static MyClock sClk;

    public static MyService getCurrent() {
        return sService;
    }

    public IBinder onBind(Intent intent) {
        MyLog.log(TAG, "MyService.onBind: " + Intent.ACTION_SEND);
        return null;
    }

    public void onCreate() {
        MyLog.log(TAG, "MyService.onCreate...");
        sService = this;
        if (sListener == null) {
            sListener = new MyListener();
        }

        if (sBattery == null) {
            sBattery = new MyBattery();
        }
        if (sWifi == null) {
            sWifi = new MyWifi();
        }
        if (sClk == null) {
            sClk = new MyClock();
        }
        MyLog.log(TAG, "MyService.onCreate.");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.log(TAG, "MyService.onStartCommand: intent=" + intent.toString() + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    public void onDestroy() {
        MyLog.log(TAG, "MyService.onDestroy...");
        if (sBattery != null) {
            sBattery.endLoop();
            sBattery = null;
        }
        if (sListener != null) {
            sListener.endLoop();
            sListener = null;
        }
        if (sClk != null) {
            sClk.endLoop();
            sClk = null;
        }
        if (sWifi != null) {
            sWifi.endLoop();
            sWifi = null;
        }
        MyLog.log(TAG, "MyService.onDestroy.");
    }
}

