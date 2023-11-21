package com.tdtu.mywallet.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

public class NotificationCancelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notification_id", 0);

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.cancel(notificationId);
    }
}

