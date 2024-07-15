package com.coderelisher.battery_charge_alarm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;

public class Settings extends AppCompatActivity {


    InternalStorage is=new InternalStorage(Settings.this);

    EditText et_maxp,et_minp, et_hook_on, et_hook_off;
    Button btn_save_settings, btn_settings_back, btn_hook_on, btn_hook_off;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_settings_back = (Button) findViewById(R.id.btn_settings_back);
        btn_save_settings = (Button) findViewById(R.id.btn_save_settings);
        btn_hook_on = (Button) findViewById(R.id.btn_hook_on);
        btn_hook_off = (Button) findViewById(R.id.btn_hook_off);
        et_hook_on = (EditText) findViewById(R.id.et_hook_on);
        et_hook_off = (EditText) findViewById(R.id.et_hook_off);
        et_maxp = (EditText) findViewById(R.id.et_maxp);
        et_minp = (EditText) findViewById(R.id.et_minp);

        // Read Memory and update views
        File file = this.getFileStreamPath("max.txt");
        if(!file.exists()){
            is.write("max.txt", "99");
        }
        File file1 = this.getFileStreamPath("min.txt");
        if(!file1.exists()){
            is.write("min.txt", "10");
        }
        File file2 = this.getFileStreamPath("hook_on.txt");
        if(!file2.exists()){
            is.write("hook_on.txt", "https://ugoods.in/api/romyai/device.php?switch=switch0&value=0");
        }
        File file3 = this.getFileStreamPath("hook_off.txt");
        if(!file3.exists()){
            is.write("hook_off.txt", "https://ugoods.in/api/romyai/device.php?switch=switch0&value=1024");
        }
        et_maxp.setText(is.read("max.txt"));
        et_minp.setText(is.read("min.txt"));

        et_hook_on.setText(is.read("hook_on.txt"));
        et_hook_off.setText(is.read("hook_off.txt"));
        //https://ugoods.in/api/romyai/device.php?switch=switch0&value=1024
        //https://ugoods.in/api/romyai/device.php?switch=switch0&value=0

        btn_settings_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxp = et_maxp.getText().toString();
                String minp = et_minp.getText().toString();
                String hookon = et_hook_on.getText().toString();
                String hookoff = et_hook_off.getText().toString();

                // Toast.makeText(Settings.this, hookoff+" and "+hookon, Toast.LENGTH_SHORT).show();

                is.write("max.txt", ""+maxp);
                is.write("min.txt", ""+minp);
                is.write("hook_on.txt", ""+hookon);
                is.write("hook_off.txt", ""+hookoff);

                Toast.makeText(Settings.this, "Saved", Toast.LENGTH_SHORT).show();
            }
        });

        btn_hook_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // https://ugoods.in/api/romyai/device.php?switch=switch0&value=0
                            HttpClient.sendGet(""+is.read("hook_on.txt"));
                        }catch (Exception e){}
                    }
                }.start();
            }
        });

        btn_hook_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            // https://ugoods.in/api/romyai/device.php?switch=switch0&value=1024
                            HttpClient.sendGet(""+is.read("hook_off.txt"));
                        }catch (Exception e){}
                    }
                }.start();
            }
        });
    }
}