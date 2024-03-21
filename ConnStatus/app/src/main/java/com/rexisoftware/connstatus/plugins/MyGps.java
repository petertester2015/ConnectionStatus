package com.rexisoftware.connstatus.plugins;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;

import androidx.annotation.NonNull;

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;
import com.rexisoftware.connstatus.data.BatteryData;

public class MyGps extends ServicePlugin {
    private static final String TAG = Global.CAT_GPS;
    private Receiver mRec;
    private LocationManager mLocation;

    @Override
    protected void setup() {
        MyLog.log(TAG, "setup");
        mRec = new Receiver();
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    protected void getStatus() {
        try {
            if (mLocation != null) {
                if (getService().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location l = mLocation.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (l != null) {
                        MyLog.log(TAG, "lat="+l.getLatitude() + " long=" + l.getLongitude() + " alt=" + l.getAltitude() + " sat=" + l.getExtras().getInt("satellites"));
                    } else {
                        MyLog.log(TAG, "Location not determined");
                    }
                }
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "getStatus: " + t);
        }
    }

    @Override
    protected void loopCleanup() {
        MyLog.log(TAG, "loopCleanup");
    }

    @Override
    protected void loopSetup() {
        MyLog.log(TAG, "loopSetup");
    }

    public boolean requireExtraSetup() {
        MyLog.log(TAG, "requireExtraSetup");
        return true;
    }

    public boolean doExtraSetup() {
        boolean ret = false;
        MyLog.log(TAG, "extraSetup...");
        try {
            if (mLocation == null) {
                mLocation = (LocationManager) getService().getApplicationContext().getSystemService(LOCATION_SERVICE);
            }
            boolean checkGPS = mLocation.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (checkGPS) {
                if (getService().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 5.0f, mRec);
                }
            }
            ret = true;
        } catch (Throwable t) {
            MyLog.log(TAG, "extraSetup: " + t);
        }
        MyLog.log(TAG, "extraSetup.");
        return ret;
    }

    @Override
    protected long getTimeout() {
        return 10000;
    }

    static class Receiver implements LocationListener {
        public Receiver() {
            MyLog.log(TAG, "Receiver");
        }

        @Override
        public void onLocationChanged(@NonNull Location location) {
            MyLog.log(TAG, "MyGPS: " + location);
        }
    }
}
