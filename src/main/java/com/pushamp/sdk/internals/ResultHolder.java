package com.pushamp.sdk.internals;

/**
 * Created by Andrey Kovrov on 07.03.15
 * All rights reserved.
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
