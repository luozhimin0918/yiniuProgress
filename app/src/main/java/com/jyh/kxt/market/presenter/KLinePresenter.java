package com.jyh.kxt.market.presenter;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.kline.bean.KLineParse;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/19.
 */

public class KLinePresenter extends BasePresenter {

    @BindObject MarketDetailActivity chartActivity;

    private XAxis xAxisK;
    private YAxis axisLeftK;
    private YAxis axisRightK;

    private KLineParse mKLineParse;

    private ArrayList<Entry> line5Entries, line10Entries, line30Entries;

    private CombinedChart combinedchart = null;
    private ViewPortHandler.OnLongPressIndicatorHandler onLongPressIndicatorHandler;

    public KLinePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initChart(ViewPortHandler.OnLongPressIndicatorHandler onLongPressIndicatorHandler) {
        this.onLongPressIndicatorHandler = onLongPressIndicatorHandler;

        ViewPortHandler viewPortHandler = chartActivity.combinedchart.getViewPortHandler();
        viewPortHandler.setOnLongPressIndicatorHandler(onLongPressIndicatorHandler);

        chartActivity.combinedchart.setDrawBorders(true);
        chartActivity.combinedchart.setBorderWidth(1);
        chartActivity.combinedchart.setBorderColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        chartActivity.combinedchart.setDescription("");
        chartActivity.combinedchart.setDragEnabled(true);
        chartActivity.combinedchart.setScaleYEnabled(false);

        Legend combinedChartLegend = chartActivity.combinedchart.getLegend();
        combinedChartLegend.setEnabled(false);
        xAxisK = chartActivity.combinedchart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));

        axisLeftK = chartActivity.combinedchart.getAxisLeft();
        axisLeftK.setDrawGridLines(false);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(false);

        axisRightK = chartActivity.combinedchart.getAxisRight();
        axisRightK.setDrawLabels(true);
        axisRightK.setDrawGridLines(true);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightK.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        axisRightK.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        chartActivity.combinedchart.setDragDecelerationEnabled(true);
        chartActivity.combinedchart.setDragDecelerationFrictionCoef(0.2f);
    }


    public void setData(List<MarketTrendBean> kLineList, View chartView) {
        setData(kLineList, chartView, -1);
    }

    //可以刷新多个  15分的数据  30分的数据等
    public void setData(List<MarketTrendBean> kLineList, View chartView, int fromSource) {

        if (chartView instanceof CombinedChart) {
            combinedchart = (CombinedChart) chartView;
        }

//        if (chartView.getTag() == null && fromSource != -1) {
//            ChartTagData chartTagData = new ChartTagData();
//            chartTagData.setTag(String.valueOf(fromSource));
//            chartView.setTag(chartTagData);
//        }


        mKLineParse = new KLineParse();
        mKLineParse.setKLineList(kLineList);

        ArrayList<String> xLabelList = new ArrayList<>();
        ArrayList<CandleEntry> candleEntries = new ArrayList<>();

        line5Entries = new ArrayList<>();
        line10Entries = new ArrayList<>();
        line30Entries = new ArrayList<>();

        for (int i = 0; i < kLineList.size(); i++) {

            xLabelList.add(kLineList.get(i).getQuotetime());

            candleEntries.add(
                    new CandleEntry(i,
                            (float) kLineList.get(i).getHigh(),
                            (float) kLineList.get(i).getLow(),
                            (float) kLineList.get(i).getOpen(),
                            (float) kLineList.get(i).getClose()));

            if (i >= 4) { //MA 5
                line5Entries.add(new Entry(getMaValueSum(i - 4, i) / (float) 5, i));
            }
            if (i >= 9) {//MA 10
                line10Entries.add(new Entry(getMaValueSum(i - 9, i) / (float) 10, i));
            }
            if (i >= 29) {//MA 30
                line30Entries.add(new Entry(getMaValueSum(i - 29, i) / (float) 30, i));
            }
        }

        /**
         * 每个K值的属性
         */
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine:"+fromSource);
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(true);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.marker_line));
        candleDataSet.setValueTextSize(10f);
        candleDataSet.setDrawValues(false);
        candleDataSet.setShadowColorSameAsCandle(true);

        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.decline_color));
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.decline_color));
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.rise_color));

        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);

        candleDataSet.setShadowWidth(1f);
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        CandleData candleData = new CandleData(xLabelList, candleDataSet);

        /**
         * 如果点数少于MA的值,防止指数不够崩溃
         */
        ArrayList<ILineDataSet> maEntityList = new ArrayList<>();
        if (kLineList.size() >= 30) {
            maEntityList.add(setMaLine(5, line5Entries));
            maEntityList.add(setMaLine(10, line10Entries));
            maEntityList.add(setMaLine(30, line30Entries));

        } else if (kLineList.size() >= 10 && kLineList.size() < 30) {

            maEntityList.add(setMaLine(5, line5Entries));
            maEntityList.add(setMaLine(10, line10Entries));

        } else if (kLineList.size() >= 5 && kLineList.size() < 10) {

            maEntityList.add(setMaLine(5, line5Entries));

        }

        //先移除基线
//        axisRightK.removeAllLimitLines();
        /**
         * 基准线的位置宽度等
         */
//        LimitLine ll = new LimitLine(mKLineParse.getBaseValue()); //基线位置
//        ll.setLineWidth(1f);
//        ll.setLineColor(ContextCompat.getColor(mContext, R.color.marker_line));
//        ll.enableDashedLine(10f, 10f, 0f);
//        ll.setTextSize(10);
//        ll.setTextColor(ContextCompat.getColor(mContext, R.color.font_color4));
//        ll.setLabel(mKLineParse.getBaseValue() + "");
//        ll.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
//        axisRightK.addLimitLine(ll);

        CombinedData combinedData = new CombinedData(xLabelList);
        LineData lineData = new LineData(xLabelList, maEntityList);
        combinedData.setData(candleData);
        combinedData.setData(lineData);

        combinedchart.setData(combinedData);
        combinedchart.moveViewToX(kLineList.size() - 1);

        ViewPortHandler viewPortHandlerCombined = combinedchart.getViewPortHandler();
        float xMaxScale = calculateMaxScale(xLabelList.size());
        viewPortHandlerCombined.setMaximumScaleX(xMaxScale);
        Matrix matrixCombined = viewPortHandlerCombined.getMatrixTouch();

        //最大缩放值
        matrixCombined.postScale(xMaxScale / 3, 1f);

        combinedchart.moveViewToX(kLineList.size() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                combinedchart.setAutoScaleMinMaxEnabled(true);
                combinedchart.notifyDataSetChanged();
                combinedchart.invalidate();
            }
        }, 300);

        //默认选中最后一条数据
        longPressIndicator(kLineList.size() - 1);
    }

    private float getMaValueSum(Integer a, Integer b) {
        float sum = 0;
        for (int i = a; i <= b; i++) {
            sum += mKLineParse.getKLineList().get(i).getClose();
        }
        return sum;
    }

    private float calculateMaxScale(float count) {
        float max = count / 127 * 5;
        return max;
    }

    @NonNull
    private LineDataSet setMaLine(int ma, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + ma);

        if (ma == 5) {
            lineDataSetMa.setHighlightEnabled(false);
            lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
            lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.marker_line));
        } else {/*此处必须得写*/
            lineDataSetMa.setHighlightEnabled(false);
        }

        lineDataSetMa.setDrawValues(false);
        if (ma == 5) {
            int color = ContextCompat.getColor(mContext, R.color.ma5);
            lineDataSetMa.setColor(color);
            chartActivity.tvMa5.setTextColor(color);
        } else if (ma == 10) {
            int color = ContextCompat.getColor(mContext, R.color.ma10);
            lineDataSetMa.setColor(color);
            chartActivity.tvMa10.setTextColor(color);
        } else {
            int color = ContextCompat.getColor(mContext, R.color.ma30);
            lineDataSetMa.setColor(color);
            chartActivity.tvMa30.setTextColor(color);
        }
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    public void longPressIndicator(int xIndex) {
        try {
            Entry md5Entry = null;
            try {
                md5Entry = line5Entries.get(xIndex - 4);
            } catch (Exception e) {
            }
            Entry md10Entry = null;
            try {
                md10Entry = line10Entries.get(xIndex - 9);
            } catch (Exception e) {
            }
            Entry md30Entry = null;
            try {
                md30Entry = line30Entries.get(xIndex - 29);
            } catch (Exception e) {
            }

            String ma5 = mContext.getResources().getString(R.string.ma5);
            String ma10 = mContext.getResources().getString(R.string.ma10);
            String ma30 = mContext.getResources().getString(R.string.ma30);

            chartActivity.tvMa5.setText(md5Entry != null ? String.format(ma5, md5Entry.getVal()) : "");
            chartActivity.tvMa10.setText(md10Entry != null ? String.format(ma10, md10Entry.getVal()) : "");
            chartActivity.tvMa30.setText(md30Entry != null ? String.format(ma30, md30Entry.getVal()) : "");

            MarketTrendBean marketTrendBean = mKLineParse.getKLineList().get(xIndex);

            chartActivity.tvKLineKaiPan.setText("开盘价:" + marketTrendBean.getOpen());
            chartActivity.tvKLineZuiGao.setText("最高价:" + marketTrendBean.getHigh());
            chartActivity.tvKLineZuiDi.setText("最低价:" + marketTrendBean.getLow());
            chartActivity.tvKLineShouPan.setText("收盘价:" + marketTrendBean.getClose());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
