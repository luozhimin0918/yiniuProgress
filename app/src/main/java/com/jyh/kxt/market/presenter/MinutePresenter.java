package com.jyh.kxt.market.presenter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.mychart.MyLineChart;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.kline.bean.MinuteParse;
import com.github.mikephil.charting.mychart.MyBottomMarkerView;
import com.github.mikephil.charting.mychart.MyLeftMarkerView;
import com.github.mikephil.charting.mychart.MyRightMarkerView;
import com.github.mikephil.charting.mychart.MyXAxis;
import com.github.mikephil.charting.mychart.MyYAxis;
import com.jyh.kxt.market.ui.MarketDetailActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/18.
 */

public class MinutePresenter extends BaseChartPresenter<MyLineChart> {

    @BindObject MarketDetailActivity chartActivity;

    private View minuteLayout;
    private MyLineChart minuteChartView;
    public TextView minuteChartTime, minuteChartDesc;

    private MyXAxis xAxisLine;
    private MyYAxis axisRightLine;
    private MyYAxis axisLeftLine;
    private List<MarketTrendBean> minuteList;

    public MinutePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initChart() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        minuteLayout = mInflater.inflate(
                R.layout.view_market_chart_minute,
                chartActivity.chartContainerLayout,
                false);

        chartActivity.chartContainerLayout.addView(minuteLayout);
        minuteChartTime = (TextView) minuteLayout.findViewById(R.id.tv_minute_time);
        minuteChartDesc = (TextView) minuteLayout.findViewById(R.id.tv_current_price);
        minuteChartView = (MyLineChart) minuteLayout.findViewById(R.id.minute_chart);

        minuteChartView.setOnDoubleTapListener(new  Chart.OnDoubleTapListener() {
            @Override
            public void onDoubleTap() {
                chartActivity.fullScreenDisplay();
            }

            @Override
            public void onSingleTapUp() {
                longPressIndicator(minuteList.size() - 1);
            }
        });
        final ViewPortHandler viewPortHandler = minuteChartView.getViewPortHandler();
        viewPortHandler.setOnLongPressIndicatorHandler(new ViewPortHandler.OnLongPressIndicatorHandler() {
            @Override
            public void longPressIndicator(int xIndex, BarLineScatterCandleBubbleData candleData) {
                MinutePresenter.this.longPressIndicator(xIndex);
            }
        });

        minuteChartView.setScaleEnabled(false);
        minuteChartView.setDrawBorders(true);
        minuteChartView.setBorderWidth(1);
        minuteChartView.setBorderColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        minuteChartView.setDescription("");

        Legend lineChartLegend = minuteChartView.getLegend();
        lineChartLegend.setEnabled(false);

        //x轴
        xAxisLine = minuteChartView.getXAxis();
        xAxisLine.setDrawLabels(true);
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        xAxisLine.enableGridDashedLine(10f, 5f, 0f);
        xAxisLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        xAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        // xAxisLine.setLabelsToSkip(59);

        //左边的Y轴数据
        axisLeftLine = minuteChartView.getAxisLeft();
        axisLeftLine.setLabelCount(5, true);
        axisLeftLine.setDrawLabels(true);
        axisLeftLine.setDrawGridLines(true);
        axisLeftLine.setDrawAxisLine(false);
        axisLeftLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        axisLeftLine.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisLeftLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));

        axisLeftLine.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat df = new DecimalFormat("######0.00");
                return df.format(value);
            }
        });

        //右边y
        axisRightLine = minuteChartView.getAxisRight();
        axisRightLine.setLabelCount(5, true);
        axisRightLine.setDrawLabels(true);
        axisRightLine.setDrawGridLines(false);
        axisRightLine.setDrawAxisLine(false);
        axisRightLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        axisRightLine.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightLine.setAxisLineColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));

        axisRightLine.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                float lastPrice = Float.parseFloat(chartActivity.marketSocketBean.zuoshou);
                float zdfValue = (value - lastPrice) / lastPrice * 100;
                DecimalFormat df = new DecimalFormat("######0.00");
                return df.format(zdfValue) + "%";
            }
        });
    }

    @Override
    public void setData(List<MarketTrendBean> minuteList, int fromSource) {
        this.minuteList = minuteList;

        MinuteParse mMinuteParse = new MinuteParse();
        mMinuteParse.setMarketTrendBeanList(minuteList);
        mMinuteParse.parseMinuteList();

        MyLeftMarkerView leftMarkerView = new MyLeftMarkerView(chartActivity, R.layout.mymarkerview);
        MyRightMarkerView rightMarkerView = new MyRightMarkerView(chartActivity, R.layout.mymarkerview);
        MyBottomMarkerView bottomMarkerView = new MyBottomMarkerView(chartActivity, R.layout.mymarkerview);
        minuteChartView.setMarker(leftMarkerView, rightMarkerView, bottomMarkerView);

        //控件设置
        xAxisLine.setXLabels(mMinuteParse.getXLabels());

        //先移除基线
//        axisLeftLine.removeAllLimitLines();
//        //基线位置
//        LimitLine ll = new LimitLine(mMinuteParse.getBaseValue());
//        ll.setLineWidth(1f);
//        ll.setLineColor(ContextCompat.getColor(mContext, R.color.marker_line));
//        ll.enableDashedLine(10f, 10f, 0f);
//        ll.setTextSize(10);
//        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        ll.setTextColor(ContextCompat.getColor(mContext, R.color.font_color4));
//        ll.setLabel(mMinuteParse.getBaseValue() + "");
//        axisLeftLine.addLimitLine(ll);

        ArrayList<String> xLabelList = new ArrayList<>();
        ArrayList<Entry> lineCJEntries = new ArrayList<>();


        for (int i = 0, j = 0; i < mMinuteParse.getMarketTrendBeanList().size(); i++, j++) {
            MarketTrendBean marketTrendBean = mMinuteParse.getMarketTrendBeanList().get(j);
            xLabelList.add(marketTrendBean.getQuotetime());



            if (marketTrendBean == null) {
                lineCJEntries.add(new Entry(Float.NaN, i));
                continue;
            }

            Entry object = new Entry((float) marketTrendBean.getClose(), i);
            lineCJEntries.add(object);
        }
        LineDataSet lineDataSet1 = new LineDataSet(lineCJEntries, "minute");
        lineDataSet1.setDrawValues(false);
        lineDataSet1.setCircleRadius(0);
        lineDataSet1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        lineDataSet1.setHighLightColor(ContextCompat.getColor(mContext, R.color.marker_line));
        lineDataSet1.setDrawFilled(true);

        float lastPrice = Float.parseFloat(chartActivity.marketSocketBean.zuoshou);
        lineDataSet1.setLastPrice(lastPrice);

        //谁为基准
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(lineDataSet1);

        /*注老版本LineData参数可以为空，最新版本会报错，修改进入ChartData加入if判断*/
        LineData cd = new LineData(xLabelList, sets);
        minuteChartView.setData(cd);
        minuteChartView.invalidate();//刷新图

        longPressIndicator(minuteList.size() - 1);
    }

    public void longPressIndicator(int xIndex) {
        MarketTrendBean marketTrendBean = minuteList.get(xIndex);
        String text = "价位:" + marketTrendBean.getClose();
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 3, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        minuteChartDesc.setText(builder);

        minuteChartTime.setText(marketTrendBean.getQuotetime());
    }

    public void notifyDataChanged(MarketTrendBean marketTrendBean) {
        try {
            minuteList.add(marketTrendBean);
            setData(minuteList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLimitLine(float baseValue) {
        axisLeftLine.removeAllLimitLines();
        //基线位置
        LimitLine ll = new LimitLine(baseValue);
        ll.setLineWidth(1f);
        ll.setLineColor(ContextCompat.getColor(mContext, R.color.marker_line));
        ll.enableDashedLine(10f, 10f, 0f);
        ll.setTextSize(10);
        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll.setTextColor(ContextCompat.getColor(mContext, R.color.font_color4));
        ll.setLabel(baseValue + "");
        axisLeftLine.addLimitLine(ll);
    }

    @Override
    public View getChartLayout() {
        return minuteLayout;
    }
    @Override
    public void removeHighlight() {
        minuteChartView.mChartTouchListener.onSingleTapUp(null);
    }
    @Override
    public View getChartView() {
        return minuteChartView;
    }

}
