package com.jyh.kxt.market.kline.bean;

import android.util.SparseArray;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/13.
 */

public class MinuteParse {
    private List<MarketTrendBean> marketTrendBeanList;

    private double minValue;
    private double maxValue;
    private SparseArray<String> dateLabels = new SparseArray<>();
    private float baseValue;

    public List<MarketTrendBean> getMarketTrendBeanList() {
        return marketTrendBeanList;
    }

    public void setMarketTrendBeanList(List<MarketTrendBean> marketTrendBeanList) {
        this.marketTrendBeanList = marketTrendBeanList;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public SparseArray<String> getXLabels() {
        return dateLabels;
    }

    /**
     * 分时线的基准值以  最后一条数据为准
     * @return
     */
    public float getBaseValue() {
        MarketTrendBean marketTrendBean = marketTrendBeanList.get(marketTrendBeanList.size() - 1);
        return (float) marketTrendBean.getLow();
    }

    public void parseMinuteList() {
        int labelsPadding = marketTrendBeanList.size() / 4;
        for (int i = 1; i <= 4; i++) {
            int labelPosition = labelsPadding * i - 1;
            MarketTrendBean marketTrendBean = marketTrendBeanList.get(labelPosition);//坐标位置-1

            dateLabels.put(labelPosition, marketTrendBean.getQuotetime().split(" ")[1]);
        }

        //默认使用第一个值当最小值
        minValue = marketTrendBeanList.get(0).getLow();

        for (MarketTrendBean marketTrendBean : marketTrendBeanList) {
            double lowValue = marketTrendBean.getLow();

            if (lowValue > maxValue) {
                maxValue = lowValue;
            } else if (lowValue < minValue) {
                minValue = lowValue;
            }
        }


    }

}
