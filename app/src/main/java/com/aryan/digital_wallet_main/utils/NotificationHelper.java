package com.aryan.digital_wallet_main.utils;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aryan.digital_wallet_main.R;
import com.aryan.digital_wallet_main.activities.MainActivity;
import com.aryan.digital_wallet_main.database.CardEntity;

import java.util.List;


public class NotificationHelper {

    private static final String CHANNEL_ID = "digital_wallet_channel";
    private static final String CHANNEL_NAME = "Digital Wallet Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for Digital Wallet app";
    private static final int NOTIFICATION_ID = 1001;

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void showExpiringCardsNotification(List<CardEntity> expiringCards) {
        if (expiringCards == null || expiringCards.isEmpty()) {
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );


        String title = "Cards Expiring Soon";
        StringBuilder content = new StringBuilder();
        content.append("You have ").append(expiringCards.size())
                .append(" card(s) expiring in the next 7 days: ");

        for (int i = 0; i < Math.min(expiringCards.size(), 3); i++) {
            content.append(expiringCards.get(i).getCardName());
            if (i < Math.min(expiringCards.size(), 3) - 1) {
                content.append(", ");
            }
        }

        if (expiringCards.size() > 3) {
            content.append(", and ").append(expiringCards.size() - 3).append(" more");
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content.toString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    public void showNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
        } catch (SecurityException e) {

            e.printStackTrace();
        }
    }
}