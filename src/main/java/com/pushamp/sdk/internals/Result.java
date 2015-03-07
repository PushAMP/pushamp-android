package com.pushamp.sdk.internals;

/**
 * Created by Andrey Kovrov on 07.03.15
 * All rights reserved.
 */
public interface Result<T> {

    String getMessage();

    T getResult();

    boolean isSuccess();
}
