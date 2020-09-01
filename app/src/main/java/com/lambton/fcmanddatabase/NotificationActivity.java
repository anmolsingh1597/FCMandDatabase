package com.lambton.fcmanddatabase;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.lambton.fcmanddatabase.R;

public class NotificationActivity extends AppCompatActivity {

    private static final String TEST_CHANNEL_ID = "TEST_CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        
        notificationCalling();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void notificationCalling() {

        NotificationChannel testChannel = new NotificationChannel(TEST_CHANNEL_ID,"Test Channels",NotificationManager.IMPORTANCE_DEFAULT);
        testChannel.setLightColor(Color.GREEN);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(testChannel);

        Notification notification = new Notification.Builder(NotificationActivity.this)
                .setContentTitle("Test Title")
                .setContentText("Test Context")
                .setSmallIcon(R.mipmap.fgf_logo)
                .setChannelId(testChannel.getId())
                .build();

//        notificationManager.notify(notificationID, notification);

       /* NotificationCompat.Builder builder = new NotificationCompat.Builder(this, TEST_CHANNEL_ID)
                .setSmallIcon(R.mipmap.fgf_logo)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);*/
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notficaionName";
            String description = "getString(R.string.channel_description)";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(TEST_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}