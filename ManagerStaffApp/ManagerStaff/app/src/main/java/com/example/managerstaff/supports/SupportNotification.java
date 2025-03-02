package com.example.managerstaff.supports;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import com.example.managerstaff.R;

public class SupportNotification extends Application {

    public static final String CHANNEL_ID_1="NOTIFICATION_NEWS";
    public static final String CHANNEL_ID_2="NOTIFICATION_MESSAGE";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Uri sound_message=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sound_notify_message);
            Uri sound_news=Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.sound_notify_news);

            AudioAttributes audioAttributes=new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            CharSequence name=getString(R.string.channel_name_1);
            String description=getString(R.string.channel_description_1);
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID_1,name,importance);
            channel.setDescription(description);
            channel.setSound(sound_news,audioAttributes);

            CharSequence name_2=getString(R.string.channel_name_2);
            String description_2=getString(R.string.channel_description_2);
            int importance_2= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel_2=new NotificationChannel(CHANNEL_ID_2,name_2,importance_2);
            channel.setDescription(description_2);
            channel.setSound(sound_message,audioAttributes);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            if(notificationManager!=null){
                notificationManager.createNotificationChannel(channel);
                notificationManager.createNotificationChannel(channel_2);
            }
        }
    }

}
