package com.jyh.kxt.index.presenter;

import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.ui.fragment.DatumFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/4/25.
 */

public class DatumPresenter extends BasePresenter {

    @BindObject DatumFragment datumFragment;

    public DatumPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    /**
     * 注册 筛选的 代理方法
     *
     * @param filtrateView
     */

    @BindView(R.id.ol_state) OptionLayout olState;
    @BindView(R.id.ol_importance) OptionLayout olImportance;
    @BindView(R.id.ol_area) OptionLayout olArea;
    @BindView(R.id.ol_judge) OptionLayout olJudge;

    public void registerFiltrateAgency(View filtrateView) {
        ButterKnife.bind(this, filtrateView);

        olState.simpleInitConfig();
        olImportance.simpleInitConfig();
        olArea.simpleInitConfig();

        olJudge.simpleInitConfig();
        olJudge.setMaxSelectCount(2);


    }
}
