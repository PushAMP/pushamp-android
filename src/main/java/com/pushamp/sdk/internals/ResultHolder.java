package com.pushamp.sdk.internals;

/**
 * pushamp.com
 * All rights reserved.
 *
 * @author Andrey Kovrov
 */
public class ResultHolder<T> implements Result<T> {

    private final String msg;
    private final boolean isOk;
    private final T result;


    private ResultHolder(String msg) {
        result = null;
        isOk = false;
        this.msg = msg;
    }

    public ResultHolder(T result) {
        this.result = result;
        isOk = true;
        msg = "";
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public T getResult() {
        return null;
    }

    @Override
    public boolean isSuccess() {
        return isOk && result != null;
    }


    public static <M> ResultHolder<M> withError(String msg) {
        return new ResultHolder<>(msg);
    }
}
