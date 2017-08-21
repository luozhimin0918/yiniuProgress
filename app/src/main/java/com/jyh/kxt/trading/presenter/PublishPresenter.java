package com.jyh.kxt.trading.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.trading.ui.PublishActivity;

import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.GlideEngine;

/**
 * Created by Mr'Dai on 2017/8/21.
 */

public class PublishPresenter extends BasePresenter {

    @BindObject PublishActivity publishActivity;

    public PublishPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 显示图片仓库
     */
    public void showPictureStorage() {

        int currentPictureCount = publishActivity.publishPicturesLayout.getChildCount();
        int residueCount = 4 - currentPictureCount;

        Picker.from(publishActivity)
                .count(residueCount)
                .enableCamera(true)
                .setEngine(new GlideEngine())
                .forResult(1000);
    }
}
