package com.library.base.http;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.library.util.EncryptionUtils;
import com.library.util.JsonUtil;
import com.library.util.LogUtil;
import com.library.util.SystemUtil;
import com.library.widget.window.ToastView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mr'Dai
 * @date 2016/5/17 17:50
 * @Title: MobileLibrary
 * @Package com.dxmobile.library.util
 * @Description:
 */
public class VolleyRequest {

    private static final int OUT_TIME = 15 * 1000;

    private Context mContext;
    private RequestQueue mQueue;

    private String msg;

    //反射得到要解析的数据
    private MyType superclassTypeParameter;

    //Tag 决定退出Activity 是否保留请求 或者作为判断表示 例如结束所有今天的请求
    private String httpTag;

    //是否弹出不成功的提示
    private boolean isToastFailed = true;

    private boolean isDefaultDecode = true;

    private boolean isEnablePreCache = false;

    private String uniqueIdentification;

    public VolleyRequest(Context mContext, RequestQueue mQueue) {
        this.mContext = mContext;
        this.mQueue = mQueue;
    }

    public void setTag(String httpTag) {
        this.httpTag = httpTag;
    }

    public void setToastFailed(boolean toastFailed) {
        isToastFailed = toastFailed;
    }

    public void setDefaultDecode(boolean defaultDecode) {
        isDefaultDecode = defaultDecode;
    }

    public <T> void doGet(String url, HttpListener<T> mHttpListener) {
        enqueue(Request.Method.GET, url, null, mHttpListener);
    }

    public <T> void doGet(String url, Map<String, String> mParams, HttpListener<T> mHttpListener) {
        enqueue(Request.Method.GET, url, mParams, mHttpListener);
    }

    public <T> void doGet(String url, JSONObject mParams, HttpListener<T> mHttpListener) {
        try {
            LogUtil.e("参数:", "" + mParams);
            url = url + EncryptionUtils.createJWT(VarConstant.KEY, mParams.toString());
            enqueue(Request.Method.GET, url, null, mHttpListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void doPost(String url, HttpListener<T> mHttpListener) {
        enqueue(Request.Method.POST, url, null, mHttpListener);
    }

    public <T> void doPost(String url, Map<String, String> mParams, HttpListener<T> mHttpListener) {
        enqueue(Request.Method.POST, url, mParams, mHttpListener);
    }

    public <T> void doPost(String url, JSONObject mParams, HttpListener<T> mHttpListener) {
        try {
            LogUtil.e("参数:", "" + mParams);
            String jwt = EncryptionUtils.createJWT(VarConstant.KEY, mParams.toString());
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("content", jwt);

            doPost(url, hashMap, mHttpListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private <T> void enqueue(final int method,
                             final String url,
                             final Map<String, String> mParams,
                             final HttpListener<T> mHttpListener) {

        mHttpListener.setUniqueIdentification(uniqueIdentification);

        this.superclassTypeParameter = getSuperclassTypeParameter(mHttpListener.getClass());
        mHttpListener.setCacheConfig(mQueue, url, superclassTypeParameter);
        SsX509TrustManager.allowAllSSL();

        long delayRequestTime = mHttpListener.getDelayRequestTime();
        new Handler(mContext.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                boolean isContinue = mHttpListener.onStart();
                if (!isContinue) {
                    return;
                }
                StringRequest stringRequest = new StringRequest(method, url, mParams, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (response == null || "".equals(response.trim())) {
                                LogUtil.e("toJsonString", "响应: response 为空");

                                mHttpListener.onErrorResponse(null);
                                return;
                            }
                            if (isDefaultDecode) {
                                String json = EncryptionUtils.parseToString(response, VarConstant.KEY);
                                try {
                                    LogUtil.e("toJsonString", "响应:" + json);

                                    org.json.JSONObject object = new org.json.JSONObject(json);
                                    int status = object.getInt("status");
                                    String data = object.getString("data");
                                    msg = object.optString("msg");

                                    //新增积分提示
                                    String integralMsg = object.optString("integral_msg");
                                    if (!TextUtils.isEmpty(integralMsg)) {
                                        ToastView.makeTextGold(mContext, integralMsg);
                                    }

                                    if (status == 1) {
                                        T resultT = null;
                                        if (superclassTypeParameter.parseType == 1) {
                                            resultT = JSON.parseObject(data, superclassTypeParameter.classType);
                                        } else if (superclassTypeParameter.parseType == 2) {

                                            Type classType = superclassTypeParameter.classType;
                                            resultT = (T) JsonUtil.parseArray(data, (Class) classType);
                                        }
                                        mHttpListener.onResponse(resultT);

                                        if (isEnablePreCache) {
                                            PreCacheHttpResponse.getInstance().addCacheData(url, resultT);
                                        }

                                    } else {
                                        mHttpListener.onErrorResponse(new VolleyError(msg));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    mHttpListener.onErrorResponse(null);
                                }
                            } else {
                                mHttpListener.onResponse((T) response);
                            }
                        } catch (Exception e) {
                            try {
                                mHttpListener.onErrorResponse(null);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            mHttpListener.onErrorResponse(error);
                            if (!isToastFailed) {
                                ToastView.makeText3(mContext, "网络出错:" + error.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                stringRequest.setRetryPolicy(new DefaultRetryPolicy(OUT_TIME, 1, 1.0f));
                stringRequest.setTag(httpTag);

                mQueue.add(stringRequest);
            }
        }, delayRequestTime);
    }

    public String getMag() {
        return msg;
    }

    /**
     * 反射取得值
     *
     * @param subclass
     * @return
     */
    MyType getSuperclassTypeParameter(Class<?> subclass) {
        Type superclass = subclass.getGenericSuperclass();

        ParameterizedType parameterizedType = (ParameterizedType) superclass;
        Type resultType = parameterizedType.getActualTypeArguments()[0];

        MyType myType;
        if (resultType instanceof ParameterizedType) {
            ParameterizedType listType = (ParameterizedType) resultType;
            resultType = listType.getActualTypeArguments()[0];
            myType = new MyType(resultType, 2);

        } else {
            myType = new MyType(resultType, 1);
        }

        return myType;
    }

    public void enablePreCache() {
        isEnablePreCache = true;
    }


    class MyType {
        public MyType(Type classType, int parseType) {
            this.classType = classType;
            this.parseType = parseType;
        }

        public Type classType;
        public int parseType;//解析类型1则直接转成Class   如果为2 则解析成List集合
    }

    public JSONObject getJsonParam() {
        JSONObject jsonObject = new JSONObject();
        SystemUtil.getVersionName(mContext);
        jsonObject.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
        jsonObject.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);
        return jsonObject;
    }

    public void setUniqueIdentification(String uniqueIdentification) {
        this.uniqueIdentification = uniqueIdentification;
    }

    public String getUniqueIdentification() {
        return uniqueIdentification;
    }
}
