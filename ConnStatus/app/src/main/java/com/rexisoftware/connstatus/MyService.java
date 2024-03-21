package com.rexisoftware.connstatus;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.rexisoftware.connstatus.plugins.MyBattery;
import com.rexisoftware.connstatus.plugins.MyClock;
import com.rexisoftware.connstatus.plugins.MyGps;
import com.rexisoftware.connstatus.plugins.MyListener;
import com.rexisoftware.connstatus.plugins.MyNetwork;
import com.rexisoftware.connstatus.plugins.MyWifi;
import com.rexisoftware.connstatus.plugins.ServicePlugin;

import java.lang.reflect.Array;
import java.util.LinkedList;

public class MyService extends Service {
    private static final String TAG = Global.CAT_MYSERVICE;
    private static final Class<ServicePlugin>[] sPLUGINS = new Class[]{MyBattery.class, MyListener.class, MyWifi.class, MyClock.class, MyNetwork.class, MyGps.class};
    private static MyService sService;
    private static ServicePlugin[] sPluginInstances;

    public static MyService getCurrent() {
        return sService;
    }

    private Handler mHandler;
    private Runnable mRun = new Runnable() {
        @Override
        public void run() {
            MyLog.log(TAG, "Plugin extra setup...");
            if (mExtraSetup.size() > 0) {
                Integer arr[] = mExtraSetup.toArray(new Integer[mExtraSetup.size()]);
                for (Integer tmp : arr) {
                    MyLog.log(TAG, "Calling " + sPluginInstances[tmp].getName());
                    boolean b = sPluginInstances[tmp].doExtraSetup();
                    if (b) {
                        mExtraSetup.remove(tmp);
                    }
                }
                mHandler.postDelayed(mRun, 5000);
            }
            MyLog.log(TAG, "Plugin extra setup completed.");
        }
    };

    public IBinder onBind(Intent intent) {
        MyLog.log(TAG, "MyService.onBind: " + Intent.ACTION_SEND);
        return null;
    }

    private LinkedList<Integer> mExtraSetup = new LinkedList<Integer>();

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
                    if (sPluginInstances[i].requireExtraSetup()){
                        mExtraSetup.add(i);
                    }
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "ServicePlugin failure: " + sPLUGINS[i].getName() + ": " + t);
            }
        }
        mHandler = new Handler();
        mHandler.postDelayed(mRun, 5000);
        MyLog.log(TAG, "MyService.onCreate.");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            MyLog.log(TAG, "MyService.onStartCommand: intent=" + intent.toString() + " flags=" + flags + " startId=" + startId);
        }else{
            MyLog.log(TAG, "MyService.onStartCommand: intent=null" + " flags=" + flags + " startId=" + startId);
        }
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

