package com.pushamp.sdk.tasks;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pushamp.sdk.internals.Result;
import com.pushamp.sdk.internals.ResultHolder;
import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.IOException;

/**
 * Created by Andrey Kovrov on 07.03.15
 * All rights reserved.
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
            // todo:
            HttpPost register = new HttpPost("/register");
            register.setHeader(X_API_KEY, settings.getApiKey());
            HttpResponse result = httpClient.execute(register);
            if (!isSuccess(result)) {
                return ResultHolder.withError("Registration failed!");
            }
            return holder;

        } catch (IOException e) {
            Log.e(TAG, "Register failed!", e);
        }
        return ResultHolder.withError("Registration failed!");
    }


    @Override
    protected void onPostExecute(Result<String> stringResult) {
        listener.onResult(stringResult);
    }
}
