package org.duckdns.nick2.connstatus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.duckdns.nick2.connstatus.databinding.ActivityMainBinding;
import org.duckdns.nick2.connstatus.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int REQID_LOC = 54321;
    private static final int REQID_PHONE = 54322;
    private static String TAG = "main";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyLog.log(TAG, "onCreate");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
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
    }

    protected void onResume() {
        super.onResume();
        MyLog.log(TAG, "onResume");
        checkPermissionsAndStartService();
    }

    private void checkPermissionsAndStartService() {
        boolean ok1 = false;
        boolean ok2 = false;
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
        if (ok1 && ok2) {
            startService(new Intent(MainActivity.this, MyService.class));
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
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
