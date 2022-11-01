package org.duckdns.nick2.connstatus;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.duckdns.nick2.connstatus.databinding.ActivityMainBinding;
import org.duckdns.nick2.connstatus.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int REQID_LOC = 54321;
    private static final int REQID_PHONE = 54322;
    private static final int REQID_LOC2 = 54323;
    private static final String TAG = Global.CAT_MAIN;
    public static MainActivity sCurr;
    private static Object sLock = new Object();

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String cat = intent.getStringExtra(Global.CATEGORY);
            String message = intent.getStringExtra(Global.MESSAGE);
            synchronized (sLock) {
                // TODO: Add code here :)
                Log.i(TAG, "cat=" + cat + " msg=" + message);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sCurr = this;
        MyLog.log(TAG, "onCreate...");

        org.duckdns.nick2.connstatus.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        MyLog.log(TAG, "onCreate3");

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(Global.BROADCAST));

        MyLog.log(TAG, "onCreate.");
    }

    protected void onResume() {
        super.onResume();
        MyLog.log(TAG, "onResume");
        checkPermissionsAndStartService();
    }

    protected void onPause() {
        super.onPause();
        MyLog.log(TAG, "onPause");
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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG, "Permission FINE LOC granted by user");
                    checkPermissionsAndStartService();
                } else {
                    MyLog.log(TAG, "Permission FINE LOC not granted by user");
                }
                return;
            case REQID_LOC:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MyLog.log(TAG, "Permission COARSE LOC granted by user");
                    checkPermissionsAndStartService();
                } else {
                    MyLog.log(TAG, "Permission COARSE LOC not granted by user");
                }
                return;
            case REQID_PHONE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
