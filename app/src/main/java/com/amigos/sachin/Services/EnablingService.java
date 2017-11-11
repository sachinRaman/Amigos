package com.amigos.sachin.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.amigos.sachin.R;

/**
 * Created by Sachin on 11/3/2017.
 */

public class EnablingService extends Service {

    String myId = "";
    Context context;
    private static int FOREGROUND_ID = 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        /*context = getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("com.amigos.sachin",Context.MODE_PRIVATE);
        myId = sp.getString("myId","");

        startForeground(FOREGROUND_ID,
                buildForegroundNotification());

        Intent startChatService = new Intent(this, ChatService.class);
        startService(startChatService);*/


        return START_NOT_STICKY;
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true)
                .setContentTitle("Amigos")
                .setContentText("")
                .setSmallIcon(R.drawable.logo);
        return(b.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*stopSelf();
        stopForeground(true);*/
    }
}
