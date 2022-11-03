package org.duckdns.nick2.connstatus.plugins;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.duckdns.nick2.connstatus.Global;
import org.duckdns.nick2.connstatus.MyLog;
import org.duckdns.nick2.connstatus.MyService;

public class MyWifi extends ServicePlugin {
    private final static String TAG = Global.CAT_WIFI;
    private final static String TAG2 = Global.CAT_WIFI_CM;
    private final Object mLock = new Object();
    private boolean mCont = true;
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
                MyLog.log(TAG, "ssid=" + wi.getSSID());
                MyLog.log(TAG, "rssi=" + wi.getRssi());
                MyLog.log(TAG, "freq=" + wi.getFrequency());
                MyLog.log(TAG, "speed=" + wi.getLinkSpeed());
                MyLog.log(TAG, "ip=" + getIPaddr(wi.getIpAddress()));
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
