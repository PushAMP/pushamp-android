package com.pushamp.sdk;

import com.loopj.android.http.JsonHttpResponseHandler;
import android.util.Log;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;


public class PushAMPResponseHandler extends JsonHttpResponseHandler {
    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Error error;
        //prefer errorResponse
        if (errorResponse != null) {
            error = new Error(errorResponse.toString(), throwable);
        }
        else {
            error = new Error(throwable.getMessage(), throwable);
        }

        handle((JSONObject)null, statusCode, error);
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        handle(response, statusCode, null);
    }
    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        handle(response, statusCode, null);
    }

    /**
     * Default handlers just do some logging
     *
     * @param success
     * @param statusCode
     * @param error
     */
    public void handle(JSONObject success, int statusCode, Error error) {
        if(error != null) {
            Log.e(PushAMP.TAG, error.getMessage(), error.getCause());
            return;
        }
        Log.d(PushAMP.TAG, success.toString());
    }

    /**
     * Default handlers just do some logging
     *
     * @param success
     * @param statusCode
     * @param error
     */
    public void handle(JSONArray success, int statusCode, Error error) {
        if(error != null) {
            Log.e(PushAMP.TAG, error.getMessage(), error.getCause());
            return;
        }
        Log.d(PushAMP.TAG, success.toString());
    }
}