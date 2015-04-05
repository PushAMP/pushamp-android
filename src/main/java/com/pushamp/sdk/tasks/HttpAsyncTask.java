package com.pushamp.sdk.tasks;

import android.app.Activity;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

import com.pushamp.sdk.internals.ResultListener;
import com.pushamp.sdk.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @param <P> params
 * @param <A> progress
 * @param <R> result
 * @author Andrey Kovrov
 */
public abstract class HttpAsyncTask<P, A, R> extends AsyncTask<P, A, R> {

    public static final String TAG = "PushAMP-GCM-SDK";
    public static final String VERSION = "1.0.2";
    public static final String USER_AGENT = TAG + "/" + VERSION;
    public static final String X_API_KEY = "X-API-KEY";
    public static final String PUSH_AMP_HOST = "https://api.pushamp.com";
    protected final Settings settings;
    protected final Context context;
    protected final HttpClient httpClient;
    protected final ResultListener<R> listener;

    public HttpAsyncTask(Settings settings, Context context, ResultListener<R> listener) {
        this.settings = settings;
        this.context = context;
        this.listener = listener;
        this.httpClient = AndroidHttpClient.newInstance(USER_AGENT);
    }

    public boolean isSuccess(HttpResponse response) {
        return response.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
    }
}
