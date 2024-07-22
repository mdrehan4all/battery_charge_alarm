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
import android.media.MediaPlayer;
import android.media.Ringtone;
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
//    private static MediaPlayer mediaPlayer;
    private boolean isConnected = true;
    InternalStorage is;
    Context context;
    int min, max;
    String hook_on, hook_off;
    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    Ringtone ringtone;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        is = new InternalStorage(getApplicationContext());

//         mediaPlayer = MediaPlayer.create(context, R.raw.beep); // replace 'your_audio_file' with the actual file name
//         mediaPlayer.setLooping(true);

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
        // ringtone.play();

        createNotificationChannel();


        // Show a Toast message when the service is created
        //Toast.makeText(getApplicationContext(), "Battery Charge Alarm Service Created", Toast.LENGTH_SHORT).show();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);

        //IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //registerReceiver(chargerConnectionReceiver, filter);

        // createNotificationChannel();
    }

    public void shownotif(){
        min = Integer.parseInt(is.read("min.txt"));
        max = Integer.parseInt(is.read("max.txt"));
        hook_on = is.read("hook_on.txt");
        hook_off = is.read("hook_off.txt");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Battery Charge Alarm")
                .setContentText("Alarm Percentage is " + max)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(1, notificationBuilder.build());
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        min = Integer.parseInt(is.read("min.txt"));
        max = Integer.parseInt(is.read("max.txt"));
        hook_on = is.read("hook_on.txt");
        hook_off = is.read("hook_off.txt");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Battery Charge Alarm")
                .setContentText("Alarm Percentage is " + max)
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

    @Override
    public void onDestroy() {
        super.onDestroy();

//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
        ringtone.stop();

        unregisterReceiver(batteryReceiver);
        MyServiceRestartReceiver myServiceRestartReceiver=new MyServiceRestartReceiver();
        IntentFilter ifilter2 = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(myServiceRestartReceiver, ifilter2);
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

            min = Integer.parseInt(is.read("min.txt"));
            max = Integer.parseInt(is.read("max.txt"));
            hook_on = is.read("hook_on.txt");
            hook_off = is.read("hook_off.txt");


            //showNotification("Battery",""+HttpClient.max,null,new Intent(getApplicationContext(), MainActivity.class));
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            isConnected = isCharging;

            if(!isConnected){
//                if (mediaPlayer != null) {
//                    mediaPlayer.pause();
//                }
                ringtone.stop();
            }

            // Toast.makeText(context, "Changed "+isConnected, Toast.LENGTH_SHORT).show();
            int startstatus = Integer.parseInt(is.read("status.txt"));

            if(startstatus == 1) {
                // Toast.makeText(context, "Battery Charge Alarm is Running", Toast.LENGTH_SHORT).show();

                if (batteryPct >= max && isConnected) {
                    //Toast.makeText(context, "Off", Toast.LENGTH_SHORT).show();
                    // showNotification("Battery","Battery full limit reached",null,new Intent(getApplicationContext(), MainActivity.class));
//                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
//                        mediaPlayer.start();
//                    }
                    shownotif();
                    ringtone.play();

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                //HttpClient.sendGet("https://ugoods.in/api/romyai/device.php?switch=switch0&value=1024");
                                HttpClient.sendGet(hook_off);
                            } catch (Exception e) {
                            }
                        }
                    }.start();
                }

                if (batteryPct <= min && !isConnected) {
                    // showNotification("Battery","Battery full limit reached",null,new Intent(getApplicationContext(), MainActivity.class));
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                //HttpClient.sendGet("https://ugoods.in/api/romyai/device.php?switch=switch0&value=0");
                                HttpClient.sendGet(hook_on);
                            } catch (Exception e) {
                            }
                        }
                    }.start();
                }
            }else{
//                if (mediaPlayer != null) {
//                    mediaPlayer.pause();
//                }
                ringtone.stop();
            }
        }
    };

    /*private BroadcastReceiver chargerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

            if (isCharging) {
                // Log.d("ChargerConnectionReceiver", "Device is charging");
                Toast.makeText(getApplicationContext(), "Charger Connected", Toast.LENGTH_SHORT).show();
                isConnected = true;
            } else {
                // Log.d("ChargerConnectionReceiver", "Device is not charging");
                Toast.makeText(getApplicationContext(), "Charger Not Connected", Toast.LENGTH_SHORT).show();
                isConnected = false;
            }
        }
    };*/
}