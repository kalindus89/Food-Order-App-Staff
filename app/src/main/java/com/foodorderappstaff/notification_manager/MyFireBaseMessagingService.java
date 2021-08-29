package com.foodorderappstaff.notification_manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.foodorderappstaff.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    String title,message;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
            super.onMessageReceived(remoteMessage);

    //    System.out.println("aaaaaaaaaa "+remoteMessage.getData());
            title=remoteMessage.getData().get("Title");
            message=remoteMessage.getData().get("Message");

       /* NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_baseline_access_time_24)
                        .setContentTitle(title)
                        .setContentText(message);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());*/
        sendNotification();
    }

    private void sendNotification() {
        Intent intent = new Intent(this, ActivityTestNotification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.d_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_baseline_shopping_cart_24)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_app_pink))
                        .setColor(Color.parseColor("#FF4081"))
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        /*final Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso.get()
                        .load("https://9mmgroceryapp.nixetechnology.com/8mm_files/ajax/cat_images/117610Grocery%20%26%20Staples.jpg")
                        .resize(200, 200)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(final Bitmap bitmap, final Picasso.LoadedFrom from) {
                                notificationBuilder.setLargeIcon(bitmap);
                                notificationManager.notify(0, notificationBuilder.build());
                            }

                            @Override
                            public void onBitmapFailed(Exception e, final Drawable errorDrawable) {
                                // Do nothing?
                            }

                            @Override
                            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                                // Do nothing?
                            }
                        });
            }
        });*/
    }


}
