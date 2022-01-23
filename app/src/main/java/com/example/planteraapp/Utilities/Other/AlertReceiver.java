package com.example.planteraapp.Utilities.Other;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;

import com.example.planteraapp.Activites.Home;
import com.example.planteraapp.Activites.LauncherActivity;
import com.example.planteraapp.R;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("test", -1);
        if (id > -1) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
            return;
        }
        NotificationCompat.Builder n1 = setNotification(context, intent.getStringExtra("Title"), intent.getStringExtra("BigText"));
        context.getSystemService(NotificationManager.class).notify((int) System.currentTimeMillis() / 1000, n1.build());
    }

    public NotificationCompat.Builder setNotification(Context context, String title, String bigText) {
        Intent intent = new Intent(context.getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int reqCode = (int) System.currentTimeMillis() / 1000;
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context.getApplicationContext(), LauncherActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_my_plants_view_icon_v24)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigText))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }
}
