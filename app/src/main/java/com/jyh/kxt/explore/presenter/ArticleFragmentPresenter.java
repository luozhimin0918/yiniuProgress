package com.jyh.kxt.explore.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.explore.json.NewsNavJson;
import com.jyh.kxt.explore.ui.fragment.ArticleFragment;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/6/9.
 */

public class ArticleFragmentPresenter extends BasePresenter {
    private VolleyRequest request;
    @BindObject ArticleFragment articleFragment;
    public int index;

    public ArticleFragmentPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        articleFragment.plRootView.loadWait();
        request.doGet(HttpConstant.EXPLORE_BLOG_NAV, request.getJsonParam(), new HttpListener<List<NewsNavJson>>() {
            @Override
            protected void onResponse(List<NewsNavJson> newsNavJson) {
                if (newsNavJson == null || newsNavJson.size() == 0) {
                    articleFragment.plRootView.loadEmptyData();
                    return;
                }
                articleFragment.init(newsNavJson);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                articleFragment.plRootView.loadError();
            }
        });
    }

    public void addOnPageChangeListener(ViewPager vpNewsList) {
        vpNewsList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void more(String[] tabs) {
        Intent intent = new Intent(mContext, ClassifyActivity.class);
        intent.putExtra(IntentConstant.INDEX, index);
        intent.putExtra(IntentConstant.ACTIONNAV, tabs);
        ((Activity) mContext).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
    }

}
