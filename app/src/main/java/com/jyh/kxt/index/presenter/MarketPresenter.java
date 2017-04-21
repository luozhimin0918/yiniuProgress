package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.index.ui.fragment.MarketFragment;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/20.
 */

public class MarketPresenter extends BasePresenter {

    @BindObject MarketFragment marketFragment;

    public MarketPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }
}
