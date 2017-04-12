package com.jyh.kxt.datum.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.datum.ui.fragment.DataFragment;

/**
 * Created by Mr'Dai on 2017/4/12.
 */

public class DataPresenter extends BasePresenter {

    @BindObject DataFragment dataFragment;

    public DataPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestTopNavigationData() {

    }

    public void requestLeftNavigationData() {

    }
}
