package com.jyh.kxt.main.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsContentJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;


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
        request.doGet(getUrl(request), new HttpListener<NewsContentJson>() {
            @Override
            protected void onResponse(NewsContentJson news) {
                if (news != null)
                    newsContentActivity.setView(news);
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

    private String getUrl(VolleyRequest volleyRequest) {

        String url = HttpConstant.NEWS_CONTENT;
        try {
            JSONObject object = volleyRequest.getJsonParam();
            object.put(VarConstant.HTTP_ID, newsContentActivity.id);
            url = url + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
