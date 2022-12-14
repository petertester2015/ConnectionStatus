package com.rexisoftware.connstatus.plugins;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;

public class MyClock extends ServicePlugin {
    private final static String TAG = Global.CAT_CLOCK;

    @Override
    protected void setup() {

    }

    @Override
    protected String getTag() {
        return null;
    }

    @Override
    protected void getStatus() {
        MyLog.log(TAG, Global.getTimeDate());
    }

    @Override
    protected void loopCleanup() {

    }

    @Override
    protected void loopSetup() {

    }

    @Override
    protected long getTimeout() {
        return 1000;
    }
}
