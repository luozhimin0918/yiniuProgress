package com.jyh.kxt.main.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.flash.FlashContentJson;
import com.jyh.kxt.main.ui.activity.FlashActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class FlashActivityPresenter extends BasePresenter {

    @BindObject FlashActivity flashActivity;
    private VolleyRequest request;
    private String id;

    public FlashActivityPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 初始化数据
     */
    public void init(String id) {
        this.id = id;
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        request.doGet(getUrl(), new HttpListener<FlashContentJson>() {
            @Override
            protected void onResponse(FlashContentJson flash) {
                if (flash == null) flashActivity.plRootView.loadEmptyData();
                flashActivity.init(flash);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                flashActivity.plRootView.loadError();
            }
        });
    }

    public String getUrl() {
        String url = HttpConstant.FLASH_INFO;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_ID, id);
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }
}
