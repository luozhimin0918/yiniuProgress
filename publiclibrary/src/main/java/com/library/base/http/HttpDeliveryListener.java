package com.library.base.http;

/**
 * Created by Mr'Dai on 2017/9/14.
 */

public abstract class HttpDeliveryListener<T> {

    public abstract void onResponse(T t);

    public abstract void onErrorResponse();
}
