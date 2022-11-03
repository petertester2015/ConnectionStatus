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

    protected abstract long getTimeout();

    public void endLoop() {
        mCont = false;
        doNotify();
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
                    getStatus();
                    mLock.wait(getTimeout());
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
