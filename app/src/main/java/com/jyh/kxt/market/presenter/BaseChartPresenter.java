package com.jyh.kxt.market.presenter;

import android.view.View;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/26.
 */

public abstract class BaseChartPresenter<T> extends BasePresenter {


    public BaseChartPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public abstract void setData(List<MarketTrendBean> marketList, int fromSource);

    public abstract View getChartLayout();

    public abstract <T extends View> T getChartView();

    public abstract void removeHighlight();
}
