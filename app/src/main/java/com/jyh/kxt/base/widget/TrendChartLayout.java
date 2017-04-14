package com.jyh.kxt.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.datum.bean.TrendBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Mr'Dai on 2017/4/13.
 */

public class TrendChartLayout extends RelativeLayout {
    public TrendChartLayout(Context context) {
        this(context, null);
    }

    public TrendChartLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TrendChartLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initLayout();
    }

    private void initLayout() {

        TrendChartView trendCharView = new TrendChartView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );
        addView(trendCharView, layoutParams);

        Random random = new Random();
        int max = 850;
        int min = 820;

        List<TrendBean> trendList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            TrendBean trendBean = new TrendBean();
            trendBean.date = "2017-03-1" + i;
            trendBean.price = Double.parseDouble(String.valueOf(random.nextInt(max) % (max - min + 1) + min));
            trendList.add(trendBean);
        }
        trendCharView.setTrendData(trendList);


        TrendChartTextView textView = new TrendChartTextView(getContext());
        textView.setBackgroundResource(R.drawable.shape_trend_chart_view);
        textView.setPadding(5, 5, 5, 5);
        addView(textView);

        trendCharView.setPriceShowView(textView);
    }
}
