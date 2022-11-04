package org.duckdns.nick2.connstatus.plugins;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import org.duckdns.nick2.connstatus.Global;
import org.duckdns.nick2.connstatus.MyLog;
import org.duckdns.nick2.connstatus.data.BatteryData;

public class MyBattery extends ServicePlugin {
    private static final String TAG = Global.CAT_BATTERY;
    private Receiver mRec;

    @Override
    protected void setup() {
        mRec = new Receiver();
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void getStatus() {

    }

    @Override
    protected void loopCleanup() {
        getService().getApplicationContext().unregisterReceiver(mRec);
    }

    @Override
    protected void loopSetup() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getService().getApplicationContext().registerReceiver(mRec, iFilter);
    }

    @Override
    protected long getTimeout() {
        return 10000;
    }

    static class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            try {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                String s;
                switch (status) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                        s = "Charging";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        s = "Discharging";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                        s = "Full";
                        break;
                    default:
                        s = "Unknown";
                }
                BatteryData.setStatus(s);
                BatteryData.setLevel("" + level);
                MyLog.log(TAG, "status=" + s + " level=" + level);
            } catch (Throwable t) {
                MyLog.log(TAG, "MyBattery: " + t);
            }
        }
    }
}
