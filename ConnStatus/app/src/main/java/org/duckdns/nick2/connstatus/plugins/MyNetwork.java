package org.duckdns.nick2.connstatus.plugins;

import org.duckdns.nick2.connstatus.Global;
import org.duckdns.nick2.connstatus.MyLog;

public class MyNetwork extends ServicePlugin {
    private final static String TAG = Global.CAT_NETWORK;
    @Override
    protected void setup() {

    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected void getStatus() {
        MyLog.log(TAG,"MyNetwork");
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
