package com.jyh.kxt.main.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.main.ui.activity.FlashActivity;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/5.
 */

public class FlashActivityPresenter extends BasePresenter {

    @BindObject FlashActivity flashActivity;

    public FlashActivityPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }
}
