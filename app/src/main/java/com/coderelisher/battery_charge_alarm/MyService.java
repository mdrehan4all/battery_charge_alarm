package com.coderelisher.battery_charge_alarm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyService extends Service {
    public MyService() {

    }

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    float battery;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Show a Toast message when the service is created
        Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);

        // createNotificationChannel();
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Battery Charge Alarm")
                .setContentText("Service is running... Full Charge Limit is " + HttpClient.max)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, notificationBuilder.build());

        // Do the task here
        // stopSelf(); // Uncomment this if you want to stop the service immediately after the task

        return START_NOT_STICKY;

        // showNotification("Battery Charge Alarm","Started",null,new Intent(this,MainActivity.class));
        //return START_NOT_STICKY;
    }

//    public void showNotification(String heading, String description, String imageUrl, Intent intent){
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_IMMUTABLE);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,"channelID")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(heading)
//                .setContentText(description)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        // int notificationId = 1;
//        int notificationId=description.hashCode();
//        createChannel(notificationManager);
//        notificationManager.notify(notificationId, notificationBuilder.build());
//        createChannel(notificationManager);
//    }

//    public void createChannel(NotificationManager notificationManager){
//        if (Build.VERSION.SDK_INT < 26) {
//            return;
//        }
//        NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
//        channel.setDescription("Description");
//        notificationManager.createNotificationChannel(channel);
//    }

    /*
    public void showNotification(){

    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Foreground Service Channel";
            String description = "Channel for Foreground Service";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float) scale * 100;
            battery = batteryPct;

            //showNotification("Battery",""+HttpClient.max,null,new Intent(getApplicationContext(), MainActivity.class));

            if(batteryPct >= HttpClient.max){
                // showNotification("Battery","Battery full limit reached",null,new Intent(getApplicationContext(), MainActivity.class));
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

            if(batteryPct <= HttpClient.min){
                // showNotification("Battery","Battery full limit reached",null,new Intent(getApplicationContext(), MainActivity.class));
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

            // tvfirst.setText(String.format("Battery Percentage: %.2f%%", batteryPct));
        }
    };
}