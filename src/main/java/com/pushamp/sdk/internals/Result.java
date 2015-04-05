package com.pushamp.sdk.internals;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
public interface Result<T> {

    String getMessage();

    T getResult();

    boolean isSuccess();
}
