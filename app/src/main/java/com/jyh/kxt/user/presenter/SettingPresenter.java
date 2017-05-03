package com.jyh.kxt.user.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.user.ui.SettingActivity;

/**
 * Created by Mr'Dai on 2017/3/20.
 */

public class SettingPresenter extends BasePresenter {

    @BindObject SettingActivity mSettingActivity;

    public SettingPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 推送
     */
    public void push() {
        mSettingActivity.changeBtnStatus(true, SettingActivity.TYPE_PUSH);
    }

    /**
     * 声音
     */
    public void sound() {
        mSettingActivity.changeBtnStatus(true, SettingActivity.TYPE_SOUND);
    }

    /**
     * 清理缓存
     */
    public void clear() {

    }

    /**
     * 检测版本
     */
    public void version() {

    }
}
