package com.coderelisher.battery_charge_alarm;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyServiceRestartReceiver extends BroadcastReceiver {

    Context c;

    @Override
    public void onReceive(Context context, Intent intent) {
        c = context;
        // Toast.makeText(context.getApplicationContext(), "Receiver", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        if(!isMyServiceRunning(MyService.class)) {
            context.startService(new Intent(context, MyService.class));
        }

        // if (action.equals(Intent.ACTION_POWER_CONNECTED)) {}
    }

    public boolean isMyServiceRunning(Class<MyService> serviceClass) {
        ActivityManager manager =(ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
