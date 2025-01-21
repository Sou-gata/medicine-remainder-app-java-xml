package com.sougata.meditrack;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class AlarmScheduler {
    public static void schedulePeriodicAlarm(Context context, String uniqueWorkName, long delayInMillie) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationWorker.class,
                7, TimeUnit.DAYS
        )
                .setInitialDelay(delayInMillie, TimeUnit.MILLISECONDS)
                .addTag(uniqueWorkName)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(uniqueWorkName, ExistingPeriodicWorkPolicy.REPLACE, workRequest);
    }

    public static void scheduleOneTimeAlarm(Context context, String uniqueWorkName, long delayInMills) {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delayInMills, TimeUnit.MINUTES)
                .addTag(uniqueWorkName)
                .build();

        WorkManager.getInstance(context)
                .enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.REPLACE, workRequest);
    }

    public static void cancelAlarm(Context context, String uniqueWorkName) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueWorkName);
    }
}
