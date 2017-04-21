package com.jyh.kxt.main.presenter;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;

import org.json.JSONObject;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/18.
 */

public class NewsContentPresenter extends BasePresenter {

    @BindObject NewsContentActivity newsContentActivity;
    private RequestQueue queue;
    private VolleyRequest request;

    public NewsContentPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init() {

        newsContentActivity.plRootView.loadWait();

        queue = newsContentActivity.getQueue();
        request = new VolleyRequest(mContext, queue);
        request.doGet(getUrl(), new HttpListener<Object>() {
            @Override
            protected void onResponse(Object o) {
                Log.i("INFO", "");
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    newsContentActivity.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getUrl() {

        String url = HttpConstant.NEWS_CONTENT;
        try {
            JSONObject object = new JSONObject();
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION, com.jyh.kxt.base.constant.VarConstant.HTTP_VERSION_VALUE);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM, com.jyh.kxt.base.constant.VarConstant.HTTP_SYSTEM_VALUE);
            object.put(com.jyh.kxt.base.constant.VarConstant.HTTP_ID, newsContentActivity.id);
            url = url + com.jyh.kxt.base.constant.VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
