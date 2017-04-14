package com.jyh.kxt.index.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.index.ui.ClassifyActivity;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/14.
 */

public class ClassifyPresenter extends BasePresenter {

    @BindObject
    ClassifyActivity classifyActivity;

    public ClassifyPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }
}
