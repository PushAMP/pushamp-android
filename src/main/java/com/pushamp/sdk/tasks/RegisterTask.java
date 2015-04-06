package com.pushamp.sdk.tasks;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pushamp.sdk.internals.Result;
import com.pushamp.sdk.internals.ResultHolder;
import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.IOException;

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
            register.setEntity(new StringEntity(String.format("{\"device_token\":\"%s\"}", token)));
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
}
