package com.library.base.http;

/**
 * Created by Mr'Dai on 2017/8/15.
 */

public interface HttpCallBack {
    enum Status {
        SUCCESS, ERROR
    }

    void onResponse(Status status);
}
