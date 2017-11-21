package com.jyh.kxt.search.presenter;

import android.app.Activity;

import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.search.json.SearchType;
import com.jyh.kxt.search.ui.SearchMainActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class SearchMainPresenter extends BasePresenter {

    @BindObject SearchMainActivity activity;
    private VolleyRequest request;

    public SearchMainPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
    }

    /**
     * 初始化导航栏
     */
    public void initNavBar() {
        request.doGet(HttpConstant.SEARCH_NAV, request.getJsonParam(), new HttpListener<List<SearchType>>() {
            @Override
            protected void onResponse(List<SearchType> o) {
                activity.initNavBar(o);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.loadError();
            }
        });
    }

    public void search(String key) {
        EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_SEARCH,key));
        SystemUtil.closeSoftInputWindow((Activity) mContext);
    }
}
