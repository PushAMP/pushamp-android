package com.pushamp.sdk.exceptions;

/**
 * Created by Andrey Kovrov on 10.02.15
 * All rights reserved.
 */
public class PushAMPException extends RuntimeException {

    public PushAMPException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
