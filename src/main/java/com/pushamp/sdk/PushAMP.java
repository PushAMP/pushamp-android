package com.pushamp.sdk;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.pushamp.sdk.exceptions.PushAMPException;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * PushAMP Client
 * <p/>
 * PushAMP pushAmp = new PushAMP("yourApiKey", "deviceToken", yorActivity);
 * pushAmp.registerForRemoteNotifications();
 */
@SuppressWarnings("unused")
public class PushAMP {

    public static final String TAG = "PushAMP-GCM-SDK";
    public static final String Version = "1.0.2";
    public static final String PushAMPAPIHost = "https://api.pushamp.com";
    public static final String UserAgent = TAG + "/" + Version;
    public static final AsyncHttpClient httpClient = new AsyncHttpClient();
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String apiKey;
    private String senderId;
    private String deviceToken;
    private Activity delegate;

    public PushAMP(String apiKey, String token, Activity delegate) {
        this.apiKey = apiKey;
        this.senderId = token;
        this.delegate = delegate;
        httpClient.setUserAgent(UserAgent);
    }

    /**
     * Register device
     *
     * @throws PushAMPException,
     */
    public void registerForRemoteNotifications() throws PushAMPException {
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "No valid Google Play Services APK found.");
            return;
        }
        doRegistrationInBackground();
    }

    /**
     * Get the stored device token either from the instance variable or check in the stored preferences
     *
     * @return String the device token
     * @throws PushAMPException,
     */
    public String getDeviceToken() throws PushAMPException {
        //is it in the instance variable?
        if (deviceToken != null && !deviceToken.isEmpty()) {
            return deviceToken;
        }
        //check is the app settings
        SharedPreferences preferences = delegate.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        deviceToken = preferences.getString(getDeviceTokenKey(), "");
        return deviceToken;
    }

    public void registerDeviceToken(String deviceToken) throws PushAMPException {
        registerDeviceTokenToChannel(deviceToken, null);
    }

    /**
     * Registers a device token with PushAMP. If a channel is passed, then to subscribes the device token to that channel.
     *
     * @param deviceToken - Device token returned by the
     * @param channel     - Optional channel to subscribe the device token
     * @throws PushAMPException,
     * @throws IllegalArgumentException
     */
    public void registerDeviceTokenToChannel(String deviceToken, String channel) throws PushAMPException {
        RequestParams params = new RequestParams();
        params.put("device_token", this.deviceToken);
        params.put("auth_token", this.apiKey);
        if (channel != null) {
            params.put("channel", channel);
        }

        post("/register", params, null);
    }

    /**
     * Verify you credentials
     *
     * @param handler your handler
     * @throws PushAMPException
     * @throws IllegalArgumentException
     */
    public void verifyCredentials(PushAMPResponseHandler handler) throws PushAMPException {
        RequestParams params = new RequestParams("auth_token", this.apiKey);
        get("/verify_credentials", params, handler);
    }

    /**
     * Subscribe to channel
     *
     * @param channel channel name
     * @throws PushAMPException
     * @throws IllegalArgumentException
     */
    public void subscribe(String channel) throws PushAMPException {
        subscribe(channel, null);
    }

    /**
     * Subscribe to channel
     *
     * @param channel channel name
     * @param handler your handler
     * @throws PushAMPException
     */
    public void subscribe(String channel, PushAMPResponseHandler handler) throws PushAMPException {
        RequestParams params = new RequestParams("auth_token", this.apiKey, "device_token", this.deviceToken, "channel", channel);
        post("/subscribe", params, handler);
    }

    /**
     * Unsubscribe from the channel
     *
     * @param channel channel name
     * @throws PushAMPException
     */
    public void unsubscribe(String channel) throws PushAMPException {
        unsubscribe(channel, null);
    }

    /**
     * Unsubscribe from the channel
     *
     * @param channel channel name
     * @throws PushAMPException
     * @throws IllegalArgumentException
     */
    public void unsubscribe(String channel, PushAMPResponseHandler handler) throws PushAMPException {
        RequestParams params = new RequestParams("auth_token", this.apiKey, "device_token", this.deviceToken, "channel", channel);
        post("/unsubscribe", params, handler);
    }

    /**
     * Unsubscribe from all channels.
     *
     * @throws PushAMPException
     */
    public void unsubscribeAll() throws PushAMPException {
        unsubscribeAll(null);
    }

    /**
     * Unsubscribe from all channels.
     *
     * @throws PushAMPException
     */
    public void unsubscribeAll(PushAMPResponseHandler handler) throws PushAMPException {
        String url = String.format("/devices/%s", this.deviceToken);
        RequestParams params = new RequestParams("auth_token", this.apiKey, "channel_list", "");
        put(url, params, handler);
    }

    /**
     * Receive all subscribed channels
     *
     * @throws PushAMPException
     */
    public void receiveChannels(PushAMPResponseHandler handler) throws PushAMPException {
        String url = String.format("/devices/%s", this.deviceToken);
        RequestParams params = new RequestParams("auth_token", this.apiKey);
        get(url, params, handler);
    }

    /**
     * Replace the current channel subscriptions with the provided list.
     *
     * @param channels list of channels
     * @throws PushAMPException
     */
    public void subscribe(List<String> channels) throws PushAMPException {
        subscribe(channels, null);
    }

    /**
     * Replace the current channel subscriptions with the provided list.
     *
     * @param channels list of channels
     * @param handler  your handler
     * @throws PushAMPException
     */
    public void subscribe(List<String> channels, PushAMPResponseHandler handler) {
        String url = String.format("/devices/%s", this.deviceToken);
        RequestParams params = new RequestParams("auth_token", this.apiKey, "channel_list", join(channels.iterator(), ","));
        put(url, params, handler);
    }

    private void post(String url, RequestParams params, PushAMPResponseHandler handler) throws PushAMPException {
        httpClient.post(PushAMPAPIHost + url, params, createOrGet(handler));
    }

    private void put(String url, RequestParams params, PushAMPResponseHandler handler) throws PushAMPException {
        httpClient.put(PushAMPAPIHost + url, params, createOrGet(handler));
    }

    private void get(String url, RequestParams params, PushAMPResponseHandler handler) throws PushAMPException {
        httpClient.get(PushAMPAPIHost + url, params, createOrGet(handler));
    }

    private PushAMPResponseHandler createOrGet(PushAMPResponseHandler handler) {
        if (handler == null) {
            handler = new PushAMPResponseHandler();
        }
        return handler;
    }

    private boolean isGooglePlayServicesAvailable() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(delegate);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, delegate, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                delegate.finish();
            }
            return false;
        }
        return true;
    }

    //Helper Method:
    private static String join(final Iterator<String> iterator, final String separator) {
        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        final String first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }


    /**
     * Set the device token. Also stores in the application shared preferences.
     *
     * @param deviceToken String representation of device token
     */
    private void setDeviceToken(String deviceToken) {
        if (deviceToken != null) {
            this.deviceToken = deviceToken;
            SharedPreferences preferences = delegate.getSharedPreferences(TAG, Context.MODE_PRIVATE);
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
    private String getDeviceTokenKey() throws PushAMPException {
        Context context = delegate.getApplicationContext();
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return String.format("com.pushamp.api.deviceToken:%s", packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new PushAMPException("Could not get package name: ", e);
        }
    }

    private void doRegistrationInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = getDeviceToken();
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(delegate.getApplicationContext());
                    if (token == null || token.isEmpty()) {
                        token = gcm.register(senderId);
                        setDeviceToken(token);
                        Log.d(TAG, "Received token: " + token);
                    } else {
                        Log.d(TAG, "Reusing token: " + token);
                    }
                } catch (IOException ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    Log.e(TAG, "Register failed!", ex);
                }
                return token;
            }

            protected void onPostExecute(String deviceToken) {
                registerDeviceToken(deviceToken);
            }
        }.execute(null, null, null);
    }


}
