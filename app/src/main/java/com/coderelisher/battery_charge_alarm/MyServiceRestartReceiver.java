package com.coderelisher.battery_charge_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyServiceRestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Receiver", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        context.startService(new Intent(context, MyService.class));
        if (action.equals(Intent.ACTION_POWER_CONNECTED)) {

        }
    }
}
