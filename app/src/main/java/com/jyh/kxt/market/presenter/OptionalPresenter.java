package com.jyh.kxt.market.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.ui.fragment.OptionalFragment;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class OptionalPresenter extends BasePresenter {
    @BindObject OptionalFragment optionalFragment;

    public OptionalPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateAdapter() {

    }
}
