package com.coderelisher.battery_charge_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    TextView tvfirst;
    Button btn_start, btn_on, btn_off;
    EditText et_percent_off, et_percent_on;
    boolean active = false;
    InternalStorage is=new InternalStorage(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getPermission();

        // ID's
        tvfirst = (TextView) findViewById(R.id.tv_first);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_on = (Button) findViewById(R.id.btn_on);
        btn_off = (Button) findViewById(R.id.btn_off);
        et_percent_off = (EditText) findViewById(R.id.et_percent);
        et_percent_on = (EditText) findViewById(R.id.et_percent_on);

        // Logic
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);


        File file = this.getFileStreamPath("max.txt");
        if(!file.exists()){
            is.write("max.txt", "96");
        }
        File file1 = this.getFileStreamPath("min.txt");
        if(!file1.exists()){
            is.write("min.txt", "20");
        }

        String max = is.read("max.txt");
        String min = is.read("min.txt");
        et_percent_off.setText(max);
        et_percent_on.setText(min);

        // Toast.makeText(this, min+" - "+max, Toast.LENGTH_SHORT).show();


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is.write("max.txt", et_percent_off.getText().toString());
                is.write("min.txt", et_percent_on.getText().toString());

                HttpClient.max = Integer.parseInt(is.read("max.txt"));
                HttpClient.min = Integer.parseInt(is.read("min.txt"));

                if(!active){
                    Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                    startService(serviceIntent);
                    active = true;
                    btn_start.setText("Stop");
                }else{
                    Intent serviceIntent = new Intent(MainActivity.this, MyService.class);
                    stopService(serviceIntent);
                    active = false;
                    btn_start.setText("Start");
                }

            }
        });

        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            HttpClient.sendGet("https://ugoods.in/api/romyai/device.php?switch=switch0&value=0");
                        }catch (Exception e){}
                    }
                }.start();
            }
        });

        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            HttpClient.sendGet("https://ugoods.in/api/romyai/device.php?switch=switch0&value=1024");
                        }catch (Exception e){}
                    }
                }.start();
            }
        });
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale * 100;
            tvfirst.setText(String.format("Battery Percentage: %.2f%%", batteryPct));
        }
    };

    // Code For Permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            // Permission is granted. You can now post notifications.
        } else {
            // Permission is denied. Handle the case.
        }
    });
    private void getPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted. You can post notifications.
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.POST_NOTIFICATIONS)) {
                // Show an explanation to the user why you need the permission.
            } else {
                // Directly request the permission.
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            // Handle older versions if necessary
        }
    }
}