package com.pushamp.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pushamp.sdk.exceptions.PushAmpException;
import com.pushamp.sdk.internals.Result;
import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.tasks.RegisterTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * PushAMP activity
 * <p/>
 */
@SuppressWarnings("unused")
public class PushAmpActivityLauncher extends Activity {

    public static final String TAG = "PushAMP-GCM-SDK";
    public static final String Version = "1.0.2";
    public static final String PushAMPAPIHost = "https://api.pushamp.com";
    public static final String UserAgent = TAG + "/" + Version;
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public HttpClient httpClient = AndroidHttpClient.newInstance(UserAgent);
    private String deviceToken;
    Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "No valid Google Play Services APK found.");
            return;
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "No valid Google Play Services APK found.");
            return;
        }
        new RegisterTask(settings, this, new ResultListener<Result<String>>() {
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
        //check is the app settings
        SharedPreferences preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        deviceToken = preferences.getString(getDeviceTokenKey(), "");
        return deviceToken;
    }

    public void registerDeviceToken(String deviceToken) throws PushAmpException {
        registerDeviceTokenToChannel(deviceToken, null);
    }

    /**
     * Registers a device token with PushAMP. If a channel is passed, then to subscribes the device token to that channel.
     *
     * @param deviceToken - Device token returned by the
     * @param channel     - Optional channel to subscribe the device token
     * @throws com.pushamp.sdk.exceptions.PushAmpException,
     * @throws IllegalArgumentException
     */
    public void registerDeviceTokenToChannel(String deviceToken, String channel) throws PushAmpException {
       /* RequestParams params = new RequestParams();
        params.put("device_token", this.deviceToken);
        params.put("auth_token", this.apiKey);
        if (channel != null) {
            params.put("channel", channel);
        }*/
        HttpPost requestBase = new HttpPost(PushAMPAPIHost + "/register");
        execute(requestBase, "registerDeviceTokenToChannel");
    }

    /**
     * Verify you credentials
     *
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     * @throws IllegalArgumentException
     */
    public void verifyCredentials() throws PushAmpException {
        //RequestParams params = new RequestParams("auth_token", this.apiKey);
        HttpGet requestBase = new HttpGet(PushAMPAPIHost + "/verify_credentials");
        execute(requestBase, "verifyCredentials");
    }


    /**
     * Subscribe to channel
     *
     * @param channel channel name
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     */
    public void subscribe(String channel) throws PushAmpException {
        //RequestParams params = new RequestParams("auth_token", this.apiKey, "device_token", this.deviceToken, "channel", channel);
        execute(new HttpPost(PushAMPAPIHost + "/subscribe"), "subscribe");
    }


    /**
     * Unsubscribe from the channel
     *
     * @param channel channel name
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     */
    public void unsubscribe(String channel) throws PushAmpException {
        // RequestParams params = new RequestParams("auth_token", this.apiKey, "device_token", this.deviceToken, "channel", channel);
        execute(new HttpPost(PushAMPAPIHost + "/unscribe"), "unsubscribe");
    }


    /**
     * Unsubscribe from all channels.
     *
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     */
    public void unsubscribeAll() throws PushAmpException {
        String url = String.format("/devices/%s", this.deviceToken);
        // RequestParams params = new RequestParams("auth_token", this.apiKey, "channel_list", "");
        execute(new HttpPut(PushAMPAPIHost + url), "unsubscribeAll");
    }

    /**
     * Receive all subscribed channels
     *
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     */
    public Set<String> receiveChannels() throws PushAmpException {
        String url = String.format("/devices/%s", this.deviceToken);
        //RequestParams params = new RequestParams("auth_token", this.apiKey);
        HttpGet requestBase = new HttpGet(PushAMPAPIHost + url);
        execute(requestBase, "receiveChannels");
        return Collections.emptySet();
    }

    /**
     * Replace the current channel subscriptions with the provided list.
     *
     * @param channels list of channels
     * @throws com.pushamp.sdk.exceptions.PushAmpException
     */
    public void subscribe(List<String> channels) {
        String url = String.format("/devices/%s", this.deviceToken);
        // RequestParams params = new RequestParams("auth_token", this.apiKey, "channel_list", join(",", channels));
        execute(new HttpPut(PushAMPAPIHost + url), "subscribe");
    }

    private HttpResponse execute(HttpRequestBase requestBase, String methodName) {
        try {
            requestBase.setHeader("X-API-KEY", settings.apiKey);
            HttpResponse response = httpClient.execute(requestBase);
        } catch (IOException e) {
            Log.e(TAG, String.format("Can't execute method [%s]", methodName), e);
        }
        return null;
    }


    private boolean isGooglePlayServicesAvailable() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Set the device token. Also stores in the application shared preferences.
     *
     * @param deviceToken String representation of device token
     */
    private void setDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            this.deviceToken = deviceToken;
            SharedPreferences preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
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
        Context context = getApplicationContext();
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
