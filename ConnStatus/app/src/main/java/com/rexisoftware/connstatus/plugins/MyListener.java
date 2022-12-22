package com.rexisoftware.connstatus.plugins;

import static android.telephony.PhoneStateListener.LISTEN_CELL_INFO;
import static android.telephony.PhoneStateListener.LISTEN_CELL_LOCATION;
import static android.telephony.PhoneStateListener.LISTEN_SERVICE_STATE;
import static android.telephony.PhoneStateListener.LISTEN_SIGNAL_STRENGTHS;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

import com.rexisoftware.connstatus.Global;
import com.rexisoftware.connstatus.MyLog;
import com.rexisoftware.connstatus.MyService;

import java.util.List;

public class MyListener extends ServicePlugin {
    private static final String TAG = Global.CAT_PHONE_STATE;
    private TelephonyManager mManager;
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
        try {
            int count = 0;
            if (mManager != null) {
                List<CellInfo> ci;
                if (ActivityCompat.checkSelfPermission(getService().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ci = null;
                    MyLog.log(TAG, "readStations missing permission");
                } else {
                    ci = mManager.getAllCellInfo();
                }
                if (ci != null) {
                    for (CellInfo tmp : ci) {
                        count++;
                        MyLog.log(TAG, "#" + count + " cell=" + getCellInfo(tmp));
                    }
                }
            }
        } catch (Throwable t) {
            MyLog.log(TAG, "readStations:" + t);
        }
    }

    @Override
    protected void loopCleanup() {

    }

    @Override
    protected void loopSetup() {
        try {
            mManager = (TelephonyManager) getService().getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            mManager.listen(mRec, LISTEN_CELL_INFO | LISTEN_CELL_LOCATION | LISTEN_SERVICE_STATE | LISTEN_SIGNAL_STRENGTHS);
            mManager.listen(mRec, PhoneStateListener.LISTEN_NONE);
        } catch (Throwable t) {
            MyLog.log(TAG, "Unable to listen to Telephony events: " + t);
        }
    }

    @Override
    protected long getTimeout() {
        return 10000;
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
//            case TelephonyManager.NETWORK_TYPE_NR:
//                return "NR";
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
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getLac(CellIdentityGsm ci) {
        int tmp = ci.getLac();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getCid(CellIdentityLte ci) {
        int tmp = ci.getCi();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getCid(CellIdentityGsm ci) {
        int tmp = ci.getCid();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityGsm ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityGsm ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityWcdma ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityWcdma ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMnc(CellIdentityLte ci) {
        int tmp = ci.getMnc();
        if (tmp > 100 || tmp < 0) tmp = -1;
        return tmp;
    }

    private int getMcc(CellIdentityLte ci) {
        int tmp = ci.getMcc();
        if (tmp > 1000000000 || tmp < 0) tmp = -1;
        return tmp;
    }

    private class Receiver extends PhoneStateListener {
        public void onCallForwardingIndicatorChanged(boolean cfi) {
            doNotify();
        }

        public void onCallStateChanged(int state, String phoneNumber) {
            doNotify();
        }

        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            try {
                for (CellInfo tmp : cellInfo) {
                    MyLog.log(TAG, "cellinfo=" + tmp.toString());
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "onSignalStrengthsChanged: " + t);
            }
            doNotify();
        }

        public void onCellLocationChanged(CellLocation location) {
            try {
                MyLog.log(TAG, "celllocation=" + location.toString());
            } catch (Throwable t) {
                MyLog.log(TAG, "onCellLocationChanged: " + t);
            }
            doNotify();
        }

        public void onDataActivity(int direction) {
            try {
                MyLog.log(TAG, "dataactivity=" + direction);
            } catch (Throwable t) {
                MyLog.log(TAG, "onDataActivity: " + t);
            }
            doNotify();
        }

        public void onDataConnectionStateChanged(int state, int networkType) {
            try {
                MyLog.log(TAG, "dataconnection=" + state + " network=" + networkType);
            } catch (Throwable t) {
                MyLog.log(TAG, "onDataConnectionStateChanged: " + t);
            }
            doNotify();
        }

        public void onDataConnectionStateChanged(int state) {
            try {
                MyLog.log(TAG, "dataconnection=" + state);
            } catch (Throwable t) {
                MyLog.log(TAG, "onDataConnectionStateChanged: " + t);
            }
            doNotify();
        }

        public void onMessageWaitingIndicatorChanged(boolean mwi) {
            doNotify();
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
            doNotify();
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
                    MyLog.log(TAG, "" + "sig=" + signalStrength.getLevel() + " type=" + type + " net=" + mManager.getNetworkCountryIso() + "." + mManager.getNetworkOperatorName() + " sim=" + mManager.getSimCountryIso() + "." + mManager.getSimOperatorName());
                }
            } catch (Throwable t) {
                MyLog.log(TAG, "onSignalStrengthsChanged: " + t);
            }
            doNotify();
        }

        public void onUserMobileDataStateChanged(boolean enabled) {
            doNotify();
        }
    }
}
