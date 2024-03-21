package com.rexisoftware.connstatus.plugins;

import com.rexisoftware.connstatus.MyLog;
import com.rexisoftware.connstatus.MyService;

public abstract class ServicePlugin extends Thread {
    private final Object mLock = new Object();
    private boolean mCont = true;

    public ServicePlugin() {
        super();
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

    protected MyService getService() {
        return MyService.getCurrent();
    }

    public boolean requireExtraSetup(){
        return false;
    }
    public boolean doExtraSetup(){
        return true;
    }
}
