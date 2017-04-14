package com.jyh.kxt.av.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class VideoPresenter extends BasePresenter {

    @BindObject VideoFragment videoFragment;

    public int index = 0;

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

    public void more() {
        Intent intent = new Intent(mContext, ClassifyActivity.class);
        intent.putExtra(IntentConstant.INDEX, index);
        ((Activity) mContext).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
    }
}
