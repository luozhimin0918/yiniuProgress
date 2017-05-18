package com.jyh.kxt.av.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.av.json.VideoNavJson;
import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.RegexValidateUtil;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class VideoPresenter extends BasePresenter {

    @BindObject VideoFragment videoFragment;

    public int index = 0;
    private VolleyRequest request;

    public VideoPresenter(IBaseView iBaseView) {
        super(iBaseView);
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

    public void init() {

        videoFragment.plRootView.loadWait();

        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        request.doGet(HttpConstant.VIDEO_NAV, new HttpListener<List<VideoNavJson>>() {
            @Override
            protected void onResponse(List<VideoNavJson> videoNavJsons) {
                if (videoNavJsons != null)
                    videoFragment.ininTab(videoNavJsons);
                else
                    onErrorResponse(null);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    videoFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
