package com.coderelisher.battery_charge_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView tvfirst;
    Button btn_start, btn_on, btn_off;
    EditText et_percent, et_percent_on;
    boolean active = false;

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

        // ID's
        tvfirst = (TextView) findViewById(R.id.tv_first);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_on = (Button) findViewById(R.id.btn_on);
        btn_off = (Button) findViewById(R.id.btn_off);
        et_percent = (EditText) findViewById(R.id.et_percent);
        et_percent_on = (EditText) findViewById(R.id.et_percent_on);

        // Logic
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpClient.max = Integer.parseInt(et_percent.getText().toString());
                HttpClient.min = Integer.parseInt(et_percent_on.getText().toString());

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

}