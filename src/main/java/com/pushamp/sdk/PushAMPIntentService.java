package com.pushamp.sdk;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;


public class PushAMPIntentService extends IntentService {
    public PushAMPIntentService(){
        super("PushAMPIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);

        onHandleIntent(intent, messageType, extras);
        //release the lock
        PushAMPBroadcastReceiver.completeWakefulIntent(intent);
    }

    protected void onHandleIntent(Intent intent, String messageType, Bundle extras) {
        Log.i("PushAMP-SDK", messageType);
        Log.i("PushAMP-SDK", "Received:" + extras.toString());
    }
}
