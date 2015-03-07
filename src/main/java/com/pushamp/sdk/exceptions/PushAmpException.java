package com.pushamp.sdk.exceptions;

/**
 * Created by Andrey Kovrov on 10.02.15
 * All rights reserved.
 */
public class PushAmpException extends RuntimeException {

    public PushAmpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
