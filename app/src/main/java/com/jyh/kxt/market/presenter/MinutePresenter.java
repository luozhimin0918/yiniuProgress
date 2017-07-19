package com.jyh.kxt.market.presenter;

import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.kline.bean.MinuteParse;
import com.jyh.kxt.market.kline.mychart.MyBottomMarkerView;
import com.jyh.kxt.market.kline.mychart.MyLeftMarkerView;
import com.jyh.kxt.market.kline.mychart.MyRightMarkerView;
import com.jyh.kxt.market.kline.mychart.MyXAxis;
import com.jyh.kxt.market.kline.mychart.MyYAxis;
import com.jyh.kxt.market.ui.MarketDetailActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/18.
 */

public class MinutePresenter extends BasePresenter {

    @BindObject MarketDetailActivity chartActivity;

    private MyXAxis xAxisLine;
    private MyYAxis axisRightLine;

    public MinutePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initChart(ViewPortHandler.OnLongPressIndicatorHandler onLongPressIndicatorHandler) {

        final ViewPortHandler viewPortHandler = chartActivity.minuteChartView.getViewPortHandler();
        viewPortHandler.setOnLongPressIndicatorHandler(onLongPressIndicatorHandler);

        chartActivity.minuteChartView.setScaleEnabled(false);
        chartActivity.minuteChartView.setDrawBorders(true);
        chartActivity.minuteChartView.setBorderWidth(1);
        chartActivity.minuteChartView.setBorderColor(chartActivity.getResources().getColor(R.color.minute_grayLine));
        chartActivity.minuteChartView.setDescription("");

        Legend lineChartLegend = chartActivity.minuteChartView.getLegend();
        lineChartLegend.setEnabled(false);

        //x轴
        xAxisLine = chartActivity.minuteChartView.getXAxis();
        xAxisLine.setDrawLabels(true);
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        xAxisLine.enableGridDashedLine(10f, 5f, 0f);
        xAxisLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        xAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        // xAxisLine.setLabelsToSkip(59);

        //隐藏左边的Y轴数据
        MyYAxis axisLeft = chartActivity.minuteChartView.getAxisLeft();
        axisLeft.setDrawLabels(false);
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);

        //右边y
        axisRightLine = chartActivity.minuteChartView.getAxisRight();
        axisRightLine.setLabelCount(5, true);
        axisRightLine.setDrawLabels(true);
        axisRightLine.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat df = new DecimalFormat("######0.00");
                return df.format(value);
            }
        });

        axisRightLine.setDrawGridLines(true);
        axisRightLine.setDrawAxisLine(false);

        axisRightLine.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));

    }

    public void setData(List<MarketTrendBean> minuteList) {
        MinuteParse mMinuteParse = new MinuteParse();
        mMinuteParse.setMarketTrendBeanList(minuteList);
        mMinuteParse.parseMinuteList();

        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(chartActivity, R.layout.mymarkerview);
        MyRightMarkerView rightMarkerView = new MyRightMarkerView(chartActivity, R.layout.mymarkerview);
        MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(chartActivity, R.layout.mymarkerview);
        chartActivity.minuteChartView.setMarker(leftMarkerView, rightMarkerView, bottomMarkerView);

        //控件设置
        xAxisLine.setXLabels(mMinuteParse.getXLabels());
        axisRightLine.setAxisMinValue((float) mMinuteParse.getMinValue());
        axisRightLine.setAxisMaxValue((float) mMinuteParse.getMaxValue()); //基准线的位置宽度等

        //基线位置
        LimitLine ll = new LimitLine(mMinuteParse.getBaseValue());
        ll.setLineWidth(1f);
        ll.setLineColor(ContextCompat.getColor(mContext, R.color.line_color));
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setTextSize(10);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        ll.setLabel(mMinuteParse.getBaseValue() + "");
        axisRightLine.addLimitLine(ll);

        ArrayList<String> xLabelList = new ArrayList<>();
        ArrayList<Entry> lineCJEntries = new ArrayList<>();

        for (int i = 0, j = 0; i < mMinuteParse.getMarketTrendBeanList().size(); i++, j++) {
            MarketTrendBean marketTrendBean = mMinuteParse.getMarketTrendBeanList().get(j);
            xLabelList.add(marketTrendBean.getQuotetime());
            if (marketTrendBean == null) {
                lineCJEntries.add(new Entry(Float.NaN, i));
                continue;
            }

            lineCJEntries.add(new Entry((float) marketTrendBean.getClose(), i));
        }
        LineDataSet lineDataSet1 = new LineDataSet(lineCJEntries, "收盘价");
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setCircleRadius(0);
        lineDataSet1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        lineDataSet1.setHighLightColor(ContextCompat.getColor(mContext, R.color.line_color));
        lineDataSet1.setDrawFilled(true);

        //谁为基准
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(lineDataSet1);

        /*注老版本LineData参数可以为空，最新版本会报错，修改进入ChartData加入if判断*/
        LineData cd = new LineData(xLabelList, sets);
        chartActivity.minuteChartView.setData(cd);
        chartActivity.minuteChartView.invalidate();//刷新图
    }
}
