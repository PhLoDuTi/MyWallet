package com.tdtu.mywallet;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class QuickExpenseNotificationService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static boolean notificationCreated = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!notificationCreated) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = createNotificationChannel();
                showForegroundNotification(channelId);
            } else {
                showNotification();
            }
            notificationCreated = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void showForegroundNotification(String channelId) {
        Log.d("NotificationService", "Showing notification");
        Intent addExpenseIntent = new Intent(this, NewExpenseMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                addExpenseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.wallet)
                .setContentTitle("Add a new expense")
                .setContentText("Tap to quickly add a new expense")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSound(null);

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private String createNotificationChannel() {
        String channelId = "expense_channel";
        String channelName = "Expense Channel";

        NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        notificationCreated = false;
        return channelId;
    }

    private void showNotification() {
        Log.d("NotificationService", "Showing notification");

        Intent addExpenseIntent = new Intent(this, NewExpenseMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                addExpenseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // Create an intent to cancel the notification
        Intent cancelIntent = new Intent("com.example.EXPENSE_NOTIFICATION_CANCEL");
        cancelIntent.putExtra("notification_id", NOTIFICATION_ID);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                cancelIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "expense_channel")
                .setSmallIcon(R.drawable.wallet)
                .setContentTitle("Add a new expense")
                .setContentText("Tap to quickly add a new expense")
                .setContentIntent(pendingIntent)
                .setDeleteIntent(cancelPendingIntent)
                .setOngoing(true)
                .setSound(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("expense_channel");
        }

        Notification notification = builder.build();

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
        notificationCreated = false;
    }
}
