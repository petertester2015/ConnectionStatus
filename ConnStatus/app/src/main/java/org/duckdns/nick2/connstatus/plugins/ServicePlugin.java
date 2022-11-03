package org.duckdns.nick2.connstatus.plugins;

import org.duckdns.nick2.connstatus.MyLog;
import org.duckdns.nick2.connstatus.MyService;

public abstract class ServicePlugin extends Thread {
    private final Object mLock = new Object();
    private boolean mCont = true;

    public ServicePlugin() {
        start();
        setup();
    }

    protected abstract void setup();

    protected abstract String getTag();

    protected abstract void getStatus();

    protected abstract void loopCleanup();

    protected abstract void loopSetup();

    public void endLoop() {
        mCont = false;
    }

    @SuppressWarnings("unused")
    protected void doNotify() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    public void run() {
        loopSetup();
        while (mCont) {
            synchronized (mLock) {
                try {
                    mLock.wait(10000);
                    getStatus();
                } catch (InterruptedException e) {
                    MyLog.log(getTag(), "wait: " + e);
                }
            }
        }
        loopCleanup();
    }

    @SuppressWarnings("unused")
    protected MyService getService() {
        return MyService.getCurrent();
    }
}
