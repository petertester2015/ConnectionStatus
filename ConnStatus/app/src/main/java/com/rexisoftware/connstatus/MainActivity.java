package com.rexisoftware.connstatus;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.rexisoftware.connstatus.data.BatteryData;
import com.rexisoftware.connstatus.data.CellularData;
import com.rexisoftware.connstatus.data.LogData;
import com.rexisoftware.connstatus.data.NetworkData;
import com.rexisoftware.connstatus.data.WifiData;

import com.rexisoftware.connstatus.R;

import com.rexisoftware.connstatus.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQID_LOC = 54321;
    private static final int REQID_PHONE = 54322;
    private static final int REQID_LOC2 = 54323;
    private static final String TAG = Global.CAT_MAIN;
    private static final Object sLock = new Object();
    private static MainActivity sCurrent;
    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String cat = intent.getStringExtra(Global.CATEGORY);
            String message = intent.getStringExtra(Global.MESSAGE);
            synchronized (sLock) {
                if (Global.CAT_BATTERY.equals(cat)) {
                    BatteryData.addData(message);
                } else if (Global.CAT_STATIONS.equals(cat) || Global.CAT_PHONE_STATE.equals(cat)) {
                    CellularData.addData(message);
                } else if (Global.CAT_WIFI.equals(cat) || Global.CAT_WIFI_CM.equals(cat)) {
                    WifiData.addData(message);
                } else if (Global.CAT_NETWORK.equals(cat)) {
                    NetworkData.addData(message);
                }
                LogData.addData("cat=" + cat + " msg=" + message);
            }
        }
    };
    private Handler mHandler;

    private static void planNextUpdate() {
        try {
            Handler h = sCurrent.mHandler;
            if (h != null) h.postDelayed(sCurrent.mRun, 500);
        } catch (Throwable t) {
            MyLog.log(TAG, "planNextUpdate: " + t);
        }
    }

    private void updateDisplay() {
        updateDisplay_battery();
        updateDisplay_wifi();
        updateDisplay_clk();
        planNextUpdate();
    }

    private String getUptime(long t) {
        long sec = t / 1000;
        long min = sec / 60;
        sec = sec - min * 60;
        long h = min / 60;
        min = min - h * 60;
        long d = h / 24;
        h = h - d * 24;

        return "" + d + "d" + h + "h" + min + "m" + sec + "s";
    }

    private final Runnable mRun = () -> updateDisplay();

    private void updateDisplay_clk() {
        try {
            long t = SystemClock.uptimeMillis() - Global.getUptimeStartTime();
            long tt = System.currentTimeMillis();
            TextView tv;
            tv = findViewById(R.id.textAppTime1);
            if (tv != null) tv.setText(Global.getFullTime(Global.getAppStartTime()));
            tv = findViewById(R.id.textActTime1);
            if (tv != null) tv.setText(Global.getFullTime(Global.getActivityStartTime()));
            tv = findViewById(R.id.textUpTime1);
            if (tv != null) tv.setText(getUptime(t));

            tv = findViewById(R.id.textAppTime2);
            long ttt = tt - Global.getAppStartTime();
            if (tv != null) tv.setText(getUptime(ttt));
            tv = findViewById(R.id.textActTime2);
            if (tv != null) tv.setText(getUptime(tt - Global.getActivityStartTime()));
            tv = findViewById(R.id.textUpTime2);
            long t4 = ttt - t;
            if (tv != null) tv.setText(getUptime(t4));
        } catch (Throwable t) {
            //MyLog.log(TAG, "updateDisplay_battery: " + t);
        }
    }

    private void updateDisplay_battery() {
        try {
            TextView tv1 = findViewById(R.id.battery_status);
            if (tv1 != null) tv1.setText(BatteryData.getStatus());
            TextView tv2 = findViewById(R.id.battery_level);
            if (tv2 != null) tv2.setText(BatteryData.getLevel());
        } catch (Throwable t) {
            //MyLog.log(TAG, "updateDisplay_battery: " + t);
        }
    }

    private void updateDisplay_wifi() {
        try {
            TextView tv;
            tv = findViewById(R.id.wifi_ssid);
            if (tv != null) tv.setText(WifiData.getSsid());
            tv = findViewById(R.id.wifi_ip);
            if (tv != null) tv.setText(WifiData.getIp());
            tv = findViewById(R.id.wifi_speed);
            if (tv != null) tv.setText(WifiData.getSpeed());
            tv = findViewById(R.id.wifi_freq);
            if (tv != null) tv.setText(WifiData.getFreq());
            tv = findViewById(R.id.wifi_rssi);
            if (tv != null) tv.setText(WifiData.getRssi());
        } catch (Throwable t) {
            //MyLog.log(TAG, "updateDisplay_wifi: " + t);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Global.resetActivityStartTime();

        MyLog.log(TAG, "onCreate...");
        sCurrent = this;
        Global.setActivity(this);

        com.rexisoftware.connstatus.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MyLog.log(TAG, "onCreate1");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        MyLog.log(TAG, "onCreate2");

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        MyLog.log(TAG, "onCreate3");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(Global.BROADCAST));

        MyLog.log(TAG, "onCreate.");
    }

    protected void onResume() {
        super.onResume();
        MyLog.log(TAG, "onResume...");
        checkPermissionsAndStartService();
        MyLog.log(TAG, "onResume.");
        mHandler = new Handler();
        try {
            sCurrent.mHandler.post(sCurrent.mRun);
        } catch (Throwable t) {
            MyLog.log(TAG, "notifyUpdatedData: " + t);
        }
    }

    protected void onPause() {
        super.onPause();
        MyLog.log(TAG, "onPause");
        sCurrent.mHandler = null;
    }

    protected void onStart() {
        super.onStart();
        MyLog.log(TAG, "onStart");
    }

    protected void onStop() {
        super.onStop();
        MyLog.log(TAG, "onStop");
    }

    protected void onDestroy() {
        super.onDestroy();
        MyLog.log(TAG, "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    protected void onRestart() {
        super.onRestart();
        MyLog.log(TAG, "onRestart");
    }

    private void checkPermissionsAndStartService() {
        boolean ok1 = false;
        boolean ok2 = false;
        boolean ok3 = false;
        int permissionCheck1 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED) {
            MyLog.log(TAG, "Ask user for permission COARSE LOC");
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQID_LOC);
        } else {
            MyLog.log(TAG, "Permission COARSE LOC has already been granted");
            ok1 = true;
        }
        int permissionCheck2 = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
            MyLog.log(TAG, "Ask user for permission PHONE STATE");
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQID_PHONE);
        } else {
            MyLog.log(TAG, "Permission PHONE STATE has already been granted");
            ok2 = true;
        }
        int permissionCheck3 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck3 != PackageManager.PERMISSION_GRANTED) {
            MyLog.log(TAG, "Ask user for permission FINE LOC");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQID_LOC2);
        } else {
            MyLog.log(TAG, "Permission FINE LOC has already been granted");
            ok3 = true;
        }
        if (ok1 && ok2 && ok3) {
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQID_LOC2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG, "Permission FINE LOC granted by user");
                    checkPermissionsAndStartService();
                } else {
                    MyLog.log(TAG, "Permission FINE LOC not granted by user");
                }
                return;
            case REQID_LOC:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG, "Permission COARSE LOC granted by user");
                    checkPermissionsAndStartService();
                } else {
                    MyLog.log(TAG, "Permission COARSE LOC not granted by user");
                }
                return;
            case REQID_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG, "Permission PHONE STATE granted by user");
                    checkPermissionsAndStartService();
                } else {
                    MyLog.log(TAG, "Permission PHONE STATE not granted by user");
                }
                return;
        }
        MyLog.log(TAG, "Some unknown permission req=" + requestCode);
    }




}
