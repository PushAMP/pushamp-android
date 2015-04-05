package com.pushamp.sdk.internals;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
public interface ResultListener<T> {

    void onResult(T result);

}
