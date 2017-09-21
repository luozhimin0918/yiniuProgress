package com.library.base.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.library.util.EncryptionUtils;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Mr'Dai
 * @date 2016/5/17 17:25
 * @Title: MobileLibrary
 * @Package com.dxmobile.library
 * @Description:
 */
public abstract class HttpListener<T> {

    private String url;
    private RequestQueue mQueue;
    private VolleyRequest.MyType superclassTypeParameter;

    private String uniqueIdentification;

    protected boolean onStart() {
        return true;
    }

    protected abstract void onResponse(T t);


    /**
     * 如果错误信息为空,则表示返回数据为空 或者 解析出现异常
     *
     * @param error
     */
    protected void onErrorResponse(VolleyError error) {

    }

    public long getDelayRequestTime() {
        return 0;
    }

    public void setCacheConfig(RequestQueue mQueue, String url, VolleyRequest.MyType superclassTypeParameter) {
        this.url = url;
        this.mQueue = mQueue;
        this.superclassTypeParameter = superclassTypeParameter;
    }

    public String getCacheJson() {
        try {
            Cache.Entry cacheEntry = mQueue.getCache().get(url);

            if (cacheEntry == null) {
                return null;
            } else {
                String encryptJsonData = new String(cacheEntry.data).toString();
                String decodeJsonData = EncryptionUtils.parseToString(encryptJsonData, VarConstant.KEY);
                return decodeJsonData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T getCacheT() {
        try {
            Cache.Entry cacheEntry = mQueue.getCache().get(url);

            if (cacheEntry == null) {
                return null;
            } else {
                String encryptJsonData = new String(cacheEntry.data).toString();
                String decodeJsonData = EncryptionUtils.parseToString(encryptJsonData, VarConstant.KEY);
                if (decodeJsonData != null) {

                    JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(decodeJsonData);
                    int status = object.getInteger("status");
                    String data = object.getString("data");

                    String msg = object.getString("msg");

                    if (status == 1) {
                        T resultT = null;
                        if (superclassTypeParameter.parseType == 1) {
                            resultT = JSON.parseObject(data, superclassTypeParameter.classType);
                        } else if (superclassTypeParameter.parseType == 2) {

                            Type classType = superclassTypeParameter.classType;
                            resultT = (T) JSON.parseArray(data, (Class) classType);
                        }
                        return resultT;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public String getUniqueIdentification() {
        return uniqueIdentification;
    }

    public void setUniqueIdentification(String uniqueIdentification) {
        this.uniqueIdentification = uniqueIdentification;
    }

    public void onPreCacheResponse(T preCacheData) {
        onResponse(preCacheData);
    }
}
