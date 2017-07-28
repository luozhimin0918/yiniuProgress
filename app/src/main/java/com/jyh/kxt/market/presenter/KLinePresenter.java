package com.jyh.kxt.market.presenter;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarLineScatterCandleBubbleData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.mychart.MyLineChart;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jyh.kxt.R;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.market.kline.bean.KLineParse;
import com.jyh.kxt.market.kline.bean.MarketTrendBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/7/19.
 */

public class KLinePresenter extends BaseChartPresenter<CombinedChart> {

    @BindObject MarketDetailActivity chartActivity;
    /**
     * K线图的Chart
     */
    private View kLineLayout;
    private CombinedChart combinedChart;
    private TextView tvMa5, tvMa10, tvMa30;
    private TextView tvKLineKaiPan, tvKLineZuiGao, tvKLineZuiDi, tvKLineShouPan;


    private XAxis xAxisK;
    private YAxis axisLeftK;
    private YAxis axisRightK;

    private KLineParse mKLineParse;

    private List<MarketTrendBean> kLineList;
    private ArrayList<Entry> line5Entries, line10Entries, line30Entries;

    public KLinePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initChart() {
        /**
         *  默认控件的初始化
         */
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        kLineLayout = mInflater.inflate(
                R.layout.view_market_chart_kline,
                chartActivity.chartContainerLayout,
                false);
        chartActivity.chartContainerLayout.addView(kLineLayout);

        combinedChart = (CombinedChart) kLineLayout.findViewById(R.id.combinedchart);
        tvMa5 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma5);
        tvMa10 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma10);
        tvMa30 = (TextView) kLineLayout.findViewById(R.id.kline_tv_ma30);

        tvKLineKaiPan = (TextView) kLineLayout.findViewById(R.id.kline_tv_kaipan);
        tvKLineZuiGao = (TextView) kLineLayout.findViewById(R.id.kline_tv_zuigao);
        tvKLineZuiDi = (TextView) kLineLayout.findViewById(R.id.kline_tv_zuidi);
        tvKLineShouPan = (TextView) kLineLayout.findViewById(R.id.kline_tv_shoupan);
        combinedChart.setOnDoubleTapListener(new Chart.OnDoubleTapListener() {
            @Override
            public void onDoubleTap() {
                chartActivity.fullScreenDisplay();
            }

            @Override
            public void onSingleTapUp() {
                //默认选中最后一条数据
                longPressIndicator(kLineList.size() - 1);
            }
        });

        ViewPortHandler viewPortHandler = combinedChart.getViewPortHandler();
        viewPortHandler.setOnLongPressIndicatorHandler(new ViewPortHandler.OnLongPressIndicatorHandler() {
            @Override
            public void longPressIndicator(int xIndex, BarLineScatterCandleBubbleData candleData) {
                KLinePresenter.this.longPressIndicator(xIndex);
            }
        });

        /**
         * 默认Chart的初始化
         */
        combinedChart.setDrawBorders(true);
        combinedChart.setBorderWidth(1);
        combinedChart.setBorderColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        combinedChart.setDescription(null);
        combinedChart.setDragEnabled(true);
        combinedChart.setScaleYEnabled(false);

        Legend combinedChartLegend = combinedChart.getLegend();
        combinedChartLegend.setEnabled(false);
        xAxisK = combinedChart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setDrawGridLines(false);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));

        axisLeftK = combinedChart.getAxisLeft();
        axisLeftK.setDrawGridLines(false);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(false);

        axisRightK = combinedChart.getAxisRight();
        axisRightK.setDrawLabels(true);
        axisRightK.setDrawGridLines(true);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setGridColor(ContextCompat.getColor(mContext, R.color.minute_grayLine));
        axisRightK.setTextColor(ContextCompat.getColor(mContext, R.color.minute_zhoutv));
        axisRightK.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisRightK.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                DecimalFormat df = new DecimalFormat("######0.00");
                return df.format(value);
//                if (value == (int) value) {
//                    return String.valueOf((int)value);
//                } else {
//                    DecimalFormat df = new DecimalFormat("######0.00");
//                    return df.format(value);
//                }
            }
        });
        combinedChart.setDragDecelerationEnabled(true);
        combinedChart.setDragDecelerationFrictionCoef(0.2f);
    }


    public void setData(List<MarketTrendBean> kLineList) {
        setData(kLineList, -1);
    }

    //可以刷新多个  15分的数据  30分的数据等
    @Override
    public void setData(List<MarketTrendBean> kLineList, int fromSource) {

        this.kLineList = kLineList;

//        if (chartView.getTag() == null && fromSource != -1) {
//            ChartTagData chartTagData = new ChartTagData();
//            chartTagData.setTag(String.valueOf(fromSource));
//            chartView.setTag(chartTagData);
//        }


        mKLineParse = new KLineParse();
        mKLineParse.setKLineList(kLineList);

        ArrayList<String> xLabelList = new ArrayList<>();
        ArrayList<String> xFormatList = new ArrayList<>();

        ArrayList<CandleEntry> candleEntries = new ArrayList<>();

        line5Entries = new ArrayList<>();
        line10Entries = new ArrayList<>();
        line30Entries = new ArrayList<>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < kLineList.size(); i++) {

            xLabelList.add(kLineList.get(i).getQuotetime());

            try {
                Date parseDate = simpleDateFormat.parse(kLineList.get(i).getQuotetime());
                long dateTimeLong = parseDate.getTime();

                String dateTimeLabel = "";
                switch (fromSource) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                        dateTimeLabel = DateFormat.format("MM-dd HH:mm", dateTimeLong).toString();
                        break;
                    case 5:
                    case 6:
                        dateTimeLabel = DateFormat.format("yyyy-MM-dd", dateTimeLong).toString();
                        break;
                    case 7:
                        dateTimeLabel = DateFormat.format("yyyy-MM", dateTimeLong).toString();
                        break;
                }
                xFormatList.add(dateTimeLabel);


            } catch (Exception e) {
                e.printStackTrace();
            }

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

        xAxisK.setFormatList(xFormatList);

        /**
         * 每个K值的属性
         */
        CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine:" + fromSource);
        candleDataSet.setDrawHorizontalHighlightIndicator(true);
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

        combinedChart.setData(combinedData);

        ViewPortHandler viewPortHandlerCombined = combinedChart.getViewPortHandler();
        float xMaxScale = calculateMaxScale(xLabelList.size());
        viewPortHandlerCombined.setMaximumScaleX(xMaxScale);
        Matrix matrixCombined = viewPortHandlerCombined.getMatrixTouch();
        //最大缩放值
        matrixCombined.reset();
        matrixCombined.postScale(xMaxScale/ 2, 1f);
        combinedChart.moveViewToX(kLineList.size() - 1);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                combinedChart.setAutoScaleMinMaxEnabled(true);
                combinedChart.notifyDataSetChanged();
                combinedChart.invalidate();
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
            tvMa5.setTextColor(color);
        } else if (ma == 10) {
            int color = ContextCompat.getColor(mContext, R.color.ma10);
            lineDataSetMa.setColor(color);
            tvMa10.setTextColor(color);
        } else {
            int color = ContextCompat.getColor(mContext, R.color.ma30);
            lineDataSetMa.setColor(color);
            tvMa30.setTextColor(color);
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

            tvMa5.setText(md5Entry != null ? String.format(ma5, md5Entry.getVal()) : "");
            tvMa10.setText(md10Entry != null ? String.format(ma10, md10Entry.getVal()) : "");
            tvMa30.setText(md30Entry != null ? String.format(ma30, md30Entry.getVal()) : "");

            MarketTrendBean marketTrendBean = mKLineParse.getKLineList().get(xIndex);

            tvKLineKaiPan.setText("开盘价:" + marketTrendBean.getOpen());
            tvKLineZuiGao.setText("最高价:" + marketTrendBean.getHigh());
            tvKLineZuiDi.setText("最低价:" + marketTrendBean.getLow());
            tvKLineShouPan.setText("收盘价:" + marketTrendBean.getClose());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View getChartLayout() {
        return kLineLayout;
    }

    @Override
    public void removeHighlight() {
        combinedChart.mChartTouchListener.onSingleTapUp(null);
    }

    @Override
    public CombinedChart getChartView() {
        return combinedChart;
    }
}
