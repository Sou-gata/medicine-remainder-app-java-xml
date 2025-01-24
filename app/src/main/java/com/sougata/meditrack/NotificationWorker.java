package com.sougata.meditrack;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Operation;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class NotificationWorker extends Worker {
    private Context context;
    private static final String CHANNEL_ID = "ALARM_NOTIFICATION_CHANNEL";

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        String title = getInputData().getString("title");
        String message = getInputData().getString("message");
        int icon = getInputData().getInt("icon", R.drawable.ic_notification);
        int type = getInputData().getInt("type", 0);
        sendNotification(title, message, icon);
        if (type == 0) {
            String uniqueWorkName = getInputData().getString("tag");
            if (uniqueWorkName != null) {
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (Exception ignored) {
                }
                WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName);
            }
        }
        return Result.success();
    }

    private void sendNotification(String title, String message, int icon) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Medicine Remainder",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.enableVibration(true);

        Intent openAppIntent = new Intent(getApplicationContext(), HomeActivity.class);
        openAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(openAppPendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), notification.build());
    }
}
