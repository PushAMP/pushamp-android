package com.pushamp.sdk.exceptions;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
public class PushAmpException extends RuntimeException {

    public PushAmpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
