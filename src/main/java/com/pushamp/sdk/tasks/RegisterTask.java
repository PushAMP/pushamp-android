package com.pushamp.sdk.tasks;

import android.content.Context;
import android.os.Build;
import android.util.JsonWriter;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pushamp.sdk.internals.Result;
import com.pushamp.sdk.internals.ResultHolder;
import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Formatter;

import static android.os.Build.BOARD;
import static android.os.Build.BOOTLOADER;
import static android.os.Build.BRAND;
import static android.os.Build.DEVICE;
import static android.os.Build.HARDWARE;
import static android.os.Build.HOST;
import static android.os.Build.ID;
import static android.os.Build.MANUFACTURER;
import static android.os.Build.MODEL;
import static android.os.Build.PRODUCT;
import static android.os.Build.SERIAL;
import static android.os.Build.TAGS;
import static android.os.Build.TIME;
import static android.os.Build.TYPE;
import static android.os.Build.USER;
import static android.os.Build.VERSION.*;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
public class RegisterTask extends HttpAsyncTask<Void, Void, Result<String>> {


    public RegisterTask(Settings settings, Context context, ResultListener<Result<String>> listener) {
        super(settings, context, listener);
    }

    @Override
    protected Result<String> doInBackground(Void... params) {
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String token = gcm.register(settings.getSenderId());
            ResultHolder<String> holder = new ResultHolder<>(token);
            if (!holder.isSuccess()) {
                return holder;
            }
            HttpPost register = new HttpPost(getHostWithApiPath() + "/register");
            register.setHeader(X_API_KEY, settings.getApiKey());
            register.setEntity(new StringEntity(new Device(token).toJson()));
            HttpResponse result = httpClient.execute(register);
            if (!isSuccess(result)) {
                return ResultHolder.withError("Registration failed!");
            }
            return holder;

        } catch (Exception any) {
            Log.e(TAG, "Register failed!", any);
        }
        return ResultHolder.withError("Registration failed!");
    }


    @Override
    protected void onPostExecute(Result<String> stringResult) {
        listener.onResult(stringResult);
    }

    private static class Device implements Serializable {

        private String device_token;
        private String build_id;
        private String build_type;
        private String build_tags;
        private String build_product;
        private String build_device;
        private String build_board;
        private String build_manufacturer;
        private String build_brand;
        private String build_model;
        private String build_bootloader;
        private String build_hardware;
        private String build_serial;
        private String build_user;
        private String build_host;
        private long build_time;
        private String version_incremental;
        private String version_release;
        private int version_sdk_number;
        private String version_codename;


        private Device(String device_token) {
            this.device_token = device_token;
            build_id = ID;
            build_type = TYPE;
            build_tags = TAGS;
            build_product = PRODUCT;
            build_device = DEVICE;
            build_board = BOARD;
            build_manufacturer = MANUFACTURER;
            build_brand = BRAND;
            build_model = MODEL;
            build_bootloader = BOOTLOADER;
            build_hardware = HARDWARE;
            build_serial = SERIAL;
            build_user = USER;
            build_host = HOST;
            build_time = TIME;
            version_incremental = INCREMENTAL;
            version_release = RELEASE;
            version_sdk_number = SDK_INT;
            version_codename = CODENAME;
        }

        String toJson() {
            JSONObject object = new JSONObject();
            try {
                object.put("device_token", device_token);
                object.put("build_id", build_id);
                object.put("build_type", build_type);
                object.put("build_tags", build_tags);
                object.put("build_product", build_product);
                object.put("build_device", build_device);
                object.put("build_board", build_board);
                object.put("build_manufacturer", build_manufacturer);
                object.put("build_brand", build_brand);
                object.put("build_model", build_model);
                object.put("build_bootloader", build_bootloader);
                object.put("build_hardware", build_hardware);
                object.put("build_serial", build_serial);
                object.put("build_user", build_user);
                object.put("build_host", build_host);
                object.put("build_time", build_time);
                object.put("version_incremental", version_incremental);
                object.put("version_release", version_release);
                object.put("version_sdk", version_sdk_number);
                object.put("version_codename", version_codename);
                return object.toString();
            } catch (JSONException e) {
                Log.e(TAG, "", e);
            }
            return String.format("{\"device_token\":\"%s\"}", device_token);
        }
    }
}
