package com.library.base.http;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.library.util.EncryptionUtils;
import com.library.util.LogUtil;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by DaiYao on 2016/9/26.
 * VolleySyncHttp volleySyncHttp = VolleySyncHttp.getInstance();
 * try
 * {
 * RequestFuture<String> future1 = volleySyncHttp.syncGet(mQueue, url2);
 * String result1 = future1.get();
 * Log.e(TAG, "future1: " + result1);
 * } catch (InterruptedException e)
 * {
 * e.printStackTrace();
 * } catch (ExecutionException e)
 * {
 * e.printStackTrace();
 * }
 */
public class VolleySyncHttp {
    private static VolleySyncHttp mVolleySyncHttp;

    public static VolleySyncHttp getInstance() {
        if (mVolleySyncHttp == null) {
            mVolleySyncHttp = new VolleySyncHttp();
        }
        return mVolleySyncHttp;
    }

    public String syncGet(RequestQueue mQueue, String url) {
        try {
            String response = syncGet(mQueue, url, null, "sync").get();
            String parseEncrypt = EncryptionUtils.parseToString(response, VarConstant.KEY);

            org.json.JSONObject object = new JSONObject(parseEncrypt);
            int status = object.getInt("status");
            String data = object.getString("data");
            String msg = object.optString("msg");

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String syncGet(RequestQueue mQueue, String url, com.alibaba.fastjson.JSONObject mParams) {
        try {

            LogUtil.e("同步请求参数:", "" + mParams);
            url = url + EncryptionUtils.createJWT(VarConstant.KEY, mParams.toString());

            String response = syncGet(mQueue, url, null, "sync").get();
            String parseEncrypt = EncryptionUtils.parseToString(response, VarConstant.KEY);

            org.json.JSONObject object = new JSONObject(parseEncrypt);
            int status = object.getInt("status");
            String data = object.getString("data");
            String msg = object.optString("msg");

            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public com.alibaba.fastjson.JSONObject getJsonParam() {
        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
        jsonObject.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);
        return jsonObject;
    }


    public RequestFuture<String> syncGet(RequestQueue mQueue, String url, Map<String, String> mParams, String tag) {
        RequestFuture<String> futureA = RequestFuture.newFuture();

        ResponseResult responseResult = new ResponseResult(futureA);
        ResponseError responseError = new ResponseError(futureA);
        FastStringRequest request = new FastStringRequest
                (
                        Request.Method.GET,
                        url,
                        mParams,
                        responseResult,
                        responseError
                );
        request.setTag(tag);
        futureA.setRequest(request);
        mQueue.add(request);

        return futureA;
    }

    class ResponseResult implements Response.Listener<String> {
        private RequestFuture<String> futureA;

        public ResponseResult(RequestFuture<String> futureA) {
            this.futureA = futureA;
        }

        @Override
        public void onResponse(String response) {
            futureA.onResponse(response);
        }
    }

    class ResponseError implements Response.ErrorListener {
        private RequestFuture<String> futureA;

        public ResponseError(RequestFuture<String> futureA) {
            this.futureA = futureA;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            futureA.onErrorResponse(error);
        }
    }
}
