package org.duckdns.nick2.connstatus;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class MyService extends Service {
    private static final String TAG = Global.CAT_MYSERVICE;
    private static MyListener sListener;
    private static MyBattery sBattery;
    private static MyService sService;

    public static MyService getCurrent() {
        return sService;
    }

    public IBinder onBind(Intent intent) {
        MyLog.log(TAG, "MyService.onBind: " + Intent.ACTION_SEND);
        return null;
    }

    public void onCreate() {
        MyLog.log(TAG, "MyService.onCreate...");
        sService = this;
        if (sListener == null) {
            MyLog.log(TAG, "MyService.onCreate1");
            sListener = new MyListener();
        }
        sListener.setService(this);
        if (sBattery == null) {
            MyLog.log(TAG, "MyService.onCreate2");
            sBattery = new MyBattery();
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            getApplicationContext().registerReceiver(sBattery, ifilter);
        }
        MyLog.log(TAG, "MyService.onCreate.");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        MyLog.log(TAG, "MyService.onStartCommand: intent=" + intent.toString() + " flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    public void onDestroy() {
        MyLog.log(TAG, "MyService.onDestroy...");
        getApplicationContext().unregisterReceiver(sBattery);
        sBattery = null;
        if (sListener != null) {
            sListener.setService(null);
            sListener = null;
        }
        MyLog.log(TAG, "MyService.onDestroy.");
    }
}

class MyListener extends PhoneStateListener {
    private static final String TAG = Global.CAT_PHONE_STATE;
    private MyService mService;
    private TelephonyManager mManager;

    public MyListener() {
        super();
        MyLog.log(TAG, "MyListener()...");
        StationReader tmp = new StationReader();
        tmp.start();
        MyLog.log(TAG, "MyListener().");
    }

    public void setService(MyService service) {
        MyLog.log(TAG, "setService...");
        try {
            if (mManager != null) {
                mManager.listen(this, PhoneStateListener.LISTEN_NONE);
                mManager = null;
            }
            mService = service;
            if (service != null) {
                mManager = (TelephonyManager) MyService.getCurrent().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                mManager.listen(this, LISTEN_CELL_INFO | LISTEN_CELL_LOCATION | LISTEN_SERVICE_STATE | LISTEN_SIGNAL_STRENGTHS);
                MyLog.log(TAG, "New listener setup");
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "Unable to listen to Telephony events: " + t);
            mManager = null;
        }
        MyLog.log(TAG, "setService.");
    }

    public void onCallForwardingIndicatorChanged(boolean cfi) {

    }

    public void onCallStateChanged(int state, String phoneNumber) {

    }

    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        try {
            for (CellInfo tmp : cellInfo) {
                MyLog.log(TAG, "cellinfo=" + tmp.toString());
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "onSignalStrengthsChanged: " + t);
        }
    }

    public void onCellLocationChanged(CellLocation location) {
        try {
            MyLog.log(TAG, "celllocation=" + location.toString());
        } catch (Throwable t) {
            MyLog.log(TAG, "onCellLocationChanged: " + t);
        }
    }

    public void onDataActivity(int direction) {
        try {
            MyLog.log(TAG, "dataactivity=" + direction);
        } catch (Throwable t) {
            MyLog.log(TAG, "onDataActivity: " + t);
        }
    }

    public void onDataConnectionStateChanged(int state, int networkType) {
        try {
            MyLog.log(TAG, "dataconnection=" + state + " network=" + networkType);
        } catch (Throwable t) {
            MyLog.log(TAG, "onDataConnectionStateChanged: " + t);
        }
    }

    public void onDataConnectionStateChanged(int state) {
        try {
            MyLog.log(TAG, "dataconnection=" + state);
        } catch (Throwable t) {
            MyLog.log(TAG, "onDataConnectionStateChanged: " + t);
        }
    }

    public void onMessageWaitingIndicatorChanged(boolean mwi) {

    }

    public void onServiceStateChanged(ServiceState serviceState) {
        try {
            StringBuilder sb = new StringBuilder();
            int state = serviceState.getState();
            sb.append("state=");
            sb.append(getState(state));
            sb.append(" op=");
            sb.append(serviceState.getOperatorNumeric());
            sb.append(" manual=");
            sb.append(serviceState.getIsManualSelection());
            MyLog.log(TAG, sb.toString());
        } catch (Throwable t) {
            MyLog.log(TAG, "onServiceStateChanged: " + t);
        }
    }

    private String getState(int state) {
        switch (state) {
            case ServiceState.STATE_IN_SERVICE:
                return "InService";
            case ServiceState.STATE_OUT_OF_SERVICE:
                return "OutOfService";
            case ServiceState.STATE_EMERGENCY_ONLY:
                return "Emergency";
            case ServiceState.STATE_POWER_OFF:
                return "PowerOff";
            default:
                return "Unknown: " + state;
        }
    }

    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        try {
            String type;
            if (mManager != null) {
                if (ActivityCompat.checkSelfPermission(MyService.getCurrent().getApplicationContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    type = "NoPermission";
                } else {
                    type = getNetworkType(mManager.getNetworkType());
                }
                MyLog.log(TAG, ""
                        + "sig=" + signalStrength.getLevel()
                        + " type=" + type
                        + " net=" + mManager.getNetworkCountryIso() + "." + mManager.getNetworkOperatorName()
                        + " sim=" + mManager.getSimCountryIso() + "." + mManager.getSimOperatorName()
                );
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "onSignalStrengthsChanged: " + t);
        }
    }

    private String getNetworkType(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "Unknown";
            default:
                return "Other:" + networkType;
        }
    }

    private String getCellInfo(CellInfo tmp) {
        StringBuilder sb = new StringBuilder();
        try {
            if (tmp instanceof CellInfoLte) {
                CellInfoLte tmp2 = (CellInfoLte) tmp;
                sb.append("LTE: dbm=");
                sb.append(tmp2.getCellSignalStrength().getDbm());
                CellIdentityLte ci = tmp2.getCellIdentity();
                if (ci != null) {
                    sb.append(" mcc=");
                    sb.append(getMcc(ci));
                    sb.append(" mnc=");
                    sb.append(getMnc(ci));
                    sb.append(" ci=");
                    sb.append(getCid(ci));
                }
            } else if (tmp instanceof CellInfoWcdma) {
                CellInfoWcdma tmp2 = (CellInfoWcdma) tmp;
                sb.append("WCDMA: dbm=");
                sb.append(tmp2.getCellSignalStrength().getDbm());
                CellIdentityWcdma ci = tmp2.getCellIdentity();
                if (ci != null) {
                    sb.append(" mcc=");
                    sb.append(getMcc(ci));
                    sb.append(" mnc=");
                    sb.append(getMnc(ci));
                    sb.append(" lac=");
                    sb.append(getLac(ci));
                }
            } else if (tmp instanceof CellInfoCdma) {
                CellInfoCdma tmp2 = (CellInfoCdma) tmp;
                sb.append("CDMA: dbm=");
                sb.append(tmp2.getCellSignalStrength().getDbm());
                CellIdentityCdma ci = tmp2.getCellIdentity();
                if (ci != null) {
                    sb.append(" lat=");
                    sb.append(ci.getLatitude());
                    sb.append(" long=");
                    sb.append(ci.getLongitude());
                    sb.append(" id=");
                    sb.append(ci.getBasestationId());
                    sb.append(" net=");
                    sb.append(ci.getNetworkId());
                }
            } else if (tmp instanceof CellInfoGsm) {
                CellInfoGsm tmp2 = (CellInfoGsm) tmp;
                sb.append("GSM: dbm=");
                sb.append(tmp2.getCellSignalStrength().getDbm());
                CellIdentityGsm ci = tmp2.getCellIdentity();
                if (ci != null) {
                    sb.append(" mcc=");
                    sb.append(getMcc(ci));
                    sb.append(" mnc=");
                    sb.append(getMnc(ci));
                    sb.append(" cid=");
                    sb.append(getCid(ci));
                    sb.append(" lac=");
                    sb.append(getLac(ci));
                }
            } else {
                sb.append(tmp.toString());
            }
        } catch (Throwable t) {
            return "CellInfo: " + t;
        }
        return sb.toString();
    }

    private int getLac(CellIdentityWcdma ci) {
        int tmp = ci.getLac();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getLac(CellIdentityGsm ci) {
        int tmp = ci.getLac();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getCid(CellIdentityLte ci) {
        int tmp = ci.getCi();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getCid(CellIdentityGsm ci) {
        int tmp = ci.getCid();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityGsm ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityGsm ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityWcdma ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityWcdma ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityLte ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityLte ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0)
            tmp = -1;
        return tmp;
    }

    public void onUserMobileDataStateChanged(boolean enabled) {

    }

    class StationReader extends Thread {
        private static final String TAG = Global.CAT_STATIONS;

        public void run() {
            while (true) {
                SystemClock.sleep(5000);
                readStations();
            }
        }

        private void readStations() {
            MyLog.log(TAG, "readStations...");
            try {
                int count = 0;
                if (mManager != null) {
                    List<CellInfo> ci;
                    if (ActivityCompat.checkSelfPermission(mService.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ci = null;
                        MyLog.log(TAG, "readStations missing permission");
                    } else {
                        MyLog.log(TAG, "readStations getAllCellInfo()...");
                        ci = mManager.getAllCellInfo();
                    }
                    if (ci != null) {
                        MyLog.log(TAG, "readStations iterate stations...");
                        for (CellInfo tmp : ci) {
                            count++;
                            MyLog.log(TAG, "#" + count + " cell=" + getCellInfo(tmp));
                        }
                    }
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "readStations:" + t);
            }
            MyLog.log(TAG, "readStations.");
        }
    }
}

class MyBattery extends BroadcastReceiver {
    private static final String TAG = Global.CAT_BATTERY;

    public MyBattery() {
        super();
        MyLog.log(TAG, "MyBattery listener instance created");
    }

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
            MyLog.log(TAG, "status=" + s + " level=" + level);
        } catch (Throwable t) {
            MyLog.log(TAG, "MyBattery: " + t);
        }
    }
}
