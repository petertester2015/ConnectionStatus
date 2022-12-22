package com.rexisoftware.connstatus.plugins;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;
import com.rexisoftware.connstatus.MyService;
import com.rexisoftware.connstatus.data.WifiData;

public class MyWifi extends ServicePlugin {
    private final static String TAG = Global.CAT_WIFI;
    private final static String TAG2 = Global.CAT_WIFI_CM;
    private ConnectivityManager.NetworkCallback mCallb;
    private WifiManager mWifiMgr;

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void getStatus() {
        try {
            WifiInfo wi = mWifiMgr.getConnectionInfo();
            if (wi != null) {
                String x = wi.getSSID();
                WifiData.setSsid(x);
                MyLog.log(TAG, "ssid=" + x);

                x = "" + wi.getRssi();
                WifiData.setRssi(x);
                MyLog.log(TAG, "rssi=" + x);

                x = "" + wi.getFrequency();
                WifiData.setFreq(x);
                MyLog.log(TAG, "freq=" + x);

                x = "" + wi.getLinkSpeed();
                WifiData.setSpeed(x);
                MyLog.log(TAG, "speed=" + x);

                x = getIPaddr(wi.getIpAddress());
                WifiData.setIp(x);
                MyLog.log(TAG, "ip=" + x);
            } else {
                MyLog.log(TAG, "not connected");
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "getStatus: " + t);
        }
    }

    @Override
    protected void loopCleanup() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.unregisterNetworkCallback(mCallb);
    }

    @Override
    protected void loopSetup() {
        mWifiMgr = (WifiManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected long getTimeout() {
        return 10000;
    }

    protected void setup() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mCallb = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(Network network) {
                MyLog.log(TAG, TAG2 + " onAvailable: " + network.toString());
                doNotify();
            }

            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                MyLog.log(TAG, TAG2 + " onCapabilitiesChanged: " + network.toString());
                doNotify();
            }

            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                MyLog.log(TAG, TAG2 + " onLinkPropertiesChanged: " + network.toString());
                doNotify();
            }

            public void onLosing(Network network, int maxMsToLive) {
                MyLog.log(TAG, TAG2 + " onLosing: " + network.toString());
                doNotify();
            }

            public void onLost(Network network) {
                MyLog.log(TAG, TAG2 + " onLost: " + network.toString());
                doNotify();
            }

            public void onUnavailable() {
                MyLog.log(TAG, TAG2 + " onUnavailable");
                doNotify();
            }
        };
        cm.registerDefaultNetworkCallback(mCallb);
    }

    private String getIPaddr(int ipAddress) {
        int a = ipAddress & 255;
        ipAddress >>= 8;
        int b = ipAddress & 255;
        ipAddress >>= 8;
        int c = ipAddress & 255;
        ipAddress >>= 8;
        int d = ipAddress & 255;
        return a + "." + b + "." + c + "." + d;
    }

}
