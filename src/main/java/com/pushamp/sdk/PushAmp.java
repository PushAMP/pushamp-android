package com.pushamp.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import com.pushamp.sdk.exceptions.PushAmpException;
import com.pushamp.sdk.internals.PushAmpConfig;
import com.pushamp.sdk.internals.Result;
import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.tasks.RegisterTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
@SuppressWarnings("unused")
public class PushAmp {


    public static final String TAG = "PushAMP-GCM-SDK";
    public static final String VERSION = "1.0.2";
    public static final String USER_AGENT = TAG + "/" + VERSION;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final Settings settings;
    private final Context context;
    private String deviceToken;
    private static volatile PushAmp instance;

    private PushAmp(Context context) {
        this.context = context;
        PushAmpConfig parse = PushAmpConfig.parseWithContext(context);
        settings = new Settings();
        settings.setApiKey(parse.getApiKey());
        settings.setSenderId(parse.getSenderId());
    }


    public synchronized static PushAmp getInstance(Context context) {
        if (instance == null) {
            instance = new PushAmp(context);
        }
        return instance;
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * Register device
     *
     * @throws com.pushamp.sdk.exceptions.PushAmpException,
     */
    public void registerForRemoteNotifications() throws PushAmpException {
        registerInternal(settings);
    }

    private void registerInternal(Settings settings) {
        new RegisterTask(settings, context, new ResultListener<Result<String>>() {
            @Override
            public void onResult(Result<String> result) {
                if (result.isSuccess()) {
                    setDeviceToken(result.getResult());
                }
            }
        }).execute();
    }

    /**
     * Get the stored device token either from the instance variable or check in the stored preferences
     *
     * @return String the device token
     * @throws com.pushamp.sdk.exceptions.PushAmpException,
     */
    public String getDeviceToken() throws PushAmpException {
        //is it in the instance variable?
        if (deviceToken != null && !deviceToken.isEmpty()) {
            return deviceToken;
        }
        //check is the app settingsc

        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        deviceToken = preferences.getString(getDeviceTokenKey(), "");
        return deviceToken;
    }

    /**
     * Set the device token. Also stores in the application shared preferences.
     *
     * @param deviceToken String representation of device token
     */
    private void setDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            this.deviceToken = deviceToken;
            SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getDeviceTokenKey(), deviceToken);
            editor.apply();
        }
    }

    /**
     * Returns the key used to look up SharedPreferences for this module.
     *
     * @return The key used to look up saved preferences
     */
    private String getDeviceTokenKey() throws PushAmpException {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return String.format("com.pushamp.api.deviceToken:%s", packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new PushAmpException("Could not get package name: ", e);
        }
    }
}
