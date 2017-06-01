package com.jyh.kxt.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.datum.bean.HistoryChartBean;
import com.jyh.kxt.datum.bean.TrendBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/13.
 */

public class TrendChartLayout extends RelativeLayout {
    private HistoryChartBean data;

    public TrendChartLayout(Context context) {
        this(context, null);
    }

    public TrendChartLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void initLayout() {
        int max = data.getY_axis().getMax();
        int min = data.getY_axis().getMin();

        TrendChartView trendCharView = new TrendChartView(getContext(), min, max);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        addView(trendCharView, layoutParams);


        List<TrendBean> trendList = new ArrayList<>();
        for (HistoryChartBean.DataBean dataBean : data.getData()) {
            TrendBean trendBean = new TrendBean();
            trendBean.date = dataBean.getTime();
            trendBean.price = dataBean.getValue();
            trendList.add(trendBean);
        }
        trendCharView.setTrendData(trendList);

        TrendChartTextView textView = new TrendChartTextView(getContext());
        textView.setBackgroundResource(R.drawable.shape_trend_chart_view);
        textView.setPadding(5, 5, 5, 5);
        addView(textView);

        trendCharView.setPriceShowView(textView);
    }

    public void setData(HistoryChartBean data) {
        this.data = data;
        initLayout();
    }
}
