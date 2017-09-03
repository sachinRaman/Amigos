package com.amigos.sachin.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Sachin on 8/31/2017.
 */

public class StartServicesAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(ChatService.class.getName()));
    }
}
