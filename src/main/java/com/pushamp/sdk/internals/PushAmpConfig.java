package com.pushamp.sdk.internals;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Andrey Kovrov on 07.03.15
 * All rights reserved.
 */
public class PushAmpConfig {

    private final static String JSON_KEY_API_HOST = "apiHost";
    private final static String JSON_KEY_VENDOR_ID = "vendorId";
    private final static String JSON_KEY_API_KEY = "apiKey";
    private final static String JSON_KEY_PUSH_AMP = "pushamp";
    private static final String JSON_KEY_SENDER_ID = "senderId";
    private static final String TAG = "PUSHAMPCONFIG";
    public static final String CONFIG_FILE_NAME = "pushamp_config.json";
    private String apiKey;
    private String host;
    private String vendorId;
    private String senderId;


    public static PushAmpConfig parse(JSONObject aJSONObject) {
        PushAmpConfig config = new PushAmpConfig();
        try {
            JSONObject root = aJSONObject.getJSONObject(JSON_KEY_PUSH_AMP);
            config.apiKey = root.getString(JSON_KEY_API_KEY);
            config.host = root.getString(JSON_KEY_API_HOST);
            config.vendorId = root.getString(JSON_KEY_VENDOR_ID);
            config.senderId = root.getString(JSON_KEY_SENDER_ID);
        } catch (JSONException e) {
            Log.e(TAG, "Can't parse pushamp configuration file!", e);
        }
        return config;
    }

    public static PushAmpConfig parseWithContext(Context aContext) {
        PushAmpConfig config = null;
        String jsonString;
        try {
            InputStream fileStream = aContext.getAssets().open(CONFIG_FILE_NAME);
            InputStreamReader e = new InputStreamReader(fileStream);
            BufferedReader reader = new BufferedReader(e);
            String line;
            StringBuilder jsonBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null)
                jsonBuilder.append(line).append('\n');
            jsonString = jsonBuilder.toString();
            JSONObject jsonConfig;
            if (!TextUtils.isEmpty(jsonString)) {
                try {
                    jsonConfig = new JSONObject(jsonString);
                    config = parse(jsonConfig);
                } catch (JSONException ex) {
                    Log.e(TAG, String.format("unable to parse json: [%s]", jsonString), ex);
                }
            }
        } catch (IOException ex) {
            Log.d(TAG, "Can't read configuration file!", ex);
        }
        return config;
    }


    public String getApiKey() {
        return apiKey;
    }

    public String getHost() {
        return host;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getSenderId() {
        return senderId;
    }
}
