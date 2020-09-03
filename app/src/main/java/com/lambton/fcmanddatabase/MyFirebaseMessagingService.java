package com.lambton.fcmanddatabase;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lambton.fcmanddatabase.R;
import com.lambton.fcmanddatabase.model.ApplicationStatus;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    ApplicationStatus applicationStatus;

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Foreground: " + message.getNotification().getTitle())
                .setContentText("Foreground: " + message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.tracker)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("notifications");

        applicationStatus = new ApplicationStatus("Foreground: " + message.getNotification().getTitle(),"Foreground: " +message.getNotification().getBody());

        myRef.push().setValue(applicationStatus);
        Log.d("TAG", "onMessageReceived: " + "Foreground: " + message.getNotification().getTitle());
    }



    private void alertToCloseApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Test dialog");
        builder.setIcon(R.mipmap.fgf_logo);
        builder.setMessage("Content");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Do something
                dialog.dismiss();
            }});

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
