package com.coderelisher.battery_charge_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyServiceRestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyService.class));
    }
}
