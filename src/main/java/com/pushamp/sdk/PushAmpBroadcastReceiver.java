package com.pushamp.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;


@SuppressWarnings("unused")
public class PushAmpBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                onError(context, intent);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                onPushReceived(context, intent, extras);
            }
        }
        setResultCode(Activity.RESULT_OK);
    }

    public void onError(Context context, Intent intent) {

    }

    public void onPushReceived(Context context, Intent intent, Bundle notification) {
        Log.d("push received", notification.toString());
    }
}
