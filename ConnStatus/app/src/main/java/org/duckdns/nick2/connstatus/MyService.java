package org.duckdns.nick2.connstatus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.duckdns.nick2.connstatus.plugins.MyBattery;
import org.duckdns.nick2.connstatus.plugins.MyClock;
import org.duckdns.nick2.connstatus.plugins.MyListener;
import org.duckdns.nick2.connstatus.plugins.MyNetwork;
import org.duckdns.nick2.connstatus.plugins.MyWifi;
import org.duckdns.nick2.connstatus.plugins.ServicePlugin;

public class MyService extends Service {
    private static final String TAG = Global.CAT_MYSERVICE;
    private static final Class<ServicePlugin>[] sPLUGINS = new Class[]{MyBattery.class, MyListener.class, MyWifi.class, MyClock.class, MyNetwork.class};
    private static MyService sService;
    private static ServicePlugin[] sPluginInstances;

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
        if (sPluginInstances == null) {
            sPluginInstances = new ServicePlugin[sPLUGINS.length];
        }
        for (int i = 0; i < sPLUGINS.length; i++) {
            try {
                if (sPluginInstances[i] == null) {
                    sPluginInstances[i] = sPLUGINS[i].newInstance();
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "ServicePlugin failure: " + sPLUGINS[i].getName() + ": " + t);
            }
        }
        MyLog.log(TAG, "MyService.onCreate.");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.log(TAG, "MyService.onStartCommand: intent=" + intent.toString() + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    public void onDestroy() {
        MyLog.log(TAG, "MyService.onDestroy...");
        for (int i = 0; i < sPLUGINS.length; i++) {
            if (sPluginInstances[i] != null) {
                sPluginInstances[i].endLoop();
                sPluginInstances[i] = null;
            }
        }
        MyLog.log(TAG, "MyService.onDestroy.");
    }
}

