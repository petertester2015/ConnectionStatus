package com.rexisoftware.connstatus.plugins;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;
import com.rexisoftware.connstatus.MyService;

public class MyNetwork extends ServicePlugin {
    private final static String TAG = Global.CAT_NETWORK;
    private ConnectivityManager.NetworkCallback mCallb;
    private WifiManager mWifiMgr;

    @Override
    protected void setup() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mCallb = new ConnectivityManager.NetworkCallback() {
            public void onAvailable(Network network) {
                MyLog.log(TAG, TAG + " onAvailable: " + network.toString());
                doNotify();
            }

            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                MyLog.log(TAG, TAG + " onCapabilitiesChanged: " + network.toString());
                doNotify();
            }

            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                MyLog.log(TAG, TAG + " onLinkPropertiesChanged: " + cm.getNetworkInfo(network).getTypeName() + " " + linkProperties.getLinkAddresses().toString());
                doNotify();
            }

            public void onLosing(Network network, int maxMsToLive) {
                MyLog.log(TAG, TAG + " onLosing: " + network.toString());
                doNotify();
            }

            public void onLost(Network network) {
                MyLog.log(TAG, TAG + " onLost: " + network.toString());
                doNotify();
            }

            public void onUnavailable() {
                MyLog.log(TAG, TAG + " onUnavailable");
                doNotify();
            }
        };
        NetworkRequest request =
                new NetworkRequest.Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        //.setIncludeOtherUidNetworks(true)
                        .build();
        cm.registerNetworkCallback(request, mCallb);
    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected void getStatus() {
        ConnectivityManager cm;
        cm = (ConnectivityManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] nets = cm.getAllNetworks();
        if ( nets != null){
            for (Network tmp : nets){
                NetworkInfo ni = cm.getNetworkInfo(tmp);
                String name = ni.getTypeName() + "_" + ni.getSubtypeName();
                String state = ni.getState().toString();
                LinkProperties lp = cm.getLinkProperties(tmp);
                String ip = lp.getLinkAddresses().toString();
                MyLog.log(TAG, "name=" + name + " state=" + state + " ip=" + ip);
            }
        }
    }

    @Override
    protected void loopCleanup() {

    }

    @Override
    protected void loopSetup() {

    }

    @Override
    protected long getTimeout() {
        return 10000;
    }
}
