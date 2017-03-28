package com.jyh.kxt.user.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindActivity;
import com.jyh.kxt.user.ui.SettingActivity;

/**
 * Created by Mr'Dai on 2017/3/20.
 */

public class SettingPresenter extends BasePresenter {

    @BindActivity SettingActivity mSettingActivity;

    public SettingPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

}
