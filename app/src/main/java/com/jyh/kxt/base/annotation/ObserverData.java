package com.jyh.kxt.base.annotation;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/25.
 */

public interface ObserverData<T> {
    void callback(T t);

    void onError(Exception e);
}
