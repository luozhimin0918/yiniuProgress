package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;

import com.jyh.kxt.R;
import com.jyh.kxt.datum.bean.TrendBean;
import com.library.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Mr'Dai on 2017/4/13.
 */

public class TrendChartView extends View {
    //框框内部的线的个数
    private final int lineCount = 1;
    //显示日期数量
    private int dataCount;

    private int marginTop;
    private int marginRight;
    private int marginLeft;
    private int marginBottom;

    private int padding;

    private Paint pointPaint;
    private Paint selectedPaint;
    private Rect frameRect;

    private List<TrendBean> trendList;
    private Double[] trendPriceArray;

    private int minPrice, maxPrice;
    //触发等级   1 蓄势不触发  2 蓄势画背景横线  3 蓄势完成画折线动画 4 点击放大圆点
    private int triggerLevel = 1;
    private int currentSelectedPosition = -1;

    private TrendChartTextView priceShowView;
    private String unit ="";

    public TrendChartView(Context context, int min, int max) {
        super(context);
        minPrice = min;
        maxPrice = max;
        initView();
    }

    private void initView() {
        marginLeft = SystemUtil.dp2px(getContext(), 48);
        marginBottom = SystemUtil.dp2px(getContext(), 42);
        marginTop = marginRight = SystemUtil.dp2px(getContext(), 4);
        padding = SystemUtil.dp2px(getContext(), 10);

        int color = getResources().getColor(R.color.blue1);
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(color);
        pointPaint.setStrokeWidth(5);
        pointPaint.setStyle(Paint.Style.FILL);

        int selectedColor = getResources().getColor(R.color.selected_point);
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setColor(selectedColor);
        selectedPaint.setStrokeWidth(5);
    }


    public void setTrendData(List<TrendBean> trendList) {
        this.trendList = trendList;
        trendPriceArray = new Double[trendList.size()];
        for (int i = 0; i < trendList.size(); i++) {
            trendPriceArray[i] = trendList.get(i).price;
        }

        calculateIntervalSize();
    }

    /**
     * 计算间隔大小
     */
    private void calculateIntervalSize() {
        if (triggerLevel == 2) {
            drawBg();
        } else {
            triggerLevel += 1;
        }
    }


    //点的位置缩放比例值
    private double pointScaleValueY;
    private int pointScaleValueX;

    private List<Point> pointFinalList;
    private List<Point> pointTemporaryList;

    /**
     * 计算所有点的位置
     */
    private void calculatePointPosition() {
        triggerLevel = 3; //重置蓄势

        dataCount = trendPriceArray.length;

        int pointCha = maxPrice - minPrice;
        if (pointCha == 0) {
            pointCha = 1;
        }

        pointScaleValueX = (frameRect.width() - (padding * 2)) / (dataCount - 1);
        pointScaleValueY = (double) frameRect.height() / (double) pointCha;

        pointFinalList = new ArrayList<>();
        pointTemporaryList = new ArrayList<>();

        for (int i = 0; i < dataCount; i++) {
            Point point = generateCoordsPoint(i);
            pointFinalList.add(point);
        }


        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                boolean isInvalidate = true;
                int temporaryPosition = 0;

                while (isInvalidate) {
                    if (pointTemporaryList.size() >= dataCount) {
                        isInvalidate = false;
                        subscriber.onCompleted();
                        return;
                    }
                    SystemClock.sleep(50);
                    pointTemporaryList.add(pointFinalList.get(temporaryPosition));
                    temporaryPosition++;

                    subscriber.onNext(null);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        triggerLevel = 4; //背景绘制
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String jsonStr) {
                        invalidate();
                    }
                });
    }

    /**
     * 生成坐标点
     *
     * @param position
     */
    private Point generateCoordsPoint(int position) {
        Double price = trendPriceArray[position];

        int pointPositionX = padding + frameRect.left + position * pointScaleValueX;
        int pointPositionY = frameRect.height() - (int) ((price - minPrice) * pointScaleValueY) + 10;

        return new Point(pointPositionX, pointPositionY);
    }

    private void drawLineChart(Canvas canvas) {
        for (int i = 0; i < pointTemporaryList.size(); i++) {
            canvas.drawCircle(pointTemporaryList.get(i).x, pointTemporaryList.get(i).y, 10, pointPaint);
        }
        for (int i = 0; i < pointTemporaryList.size() - 1; i++) {
            Point startPoint = pointTemporaryList.get(i);
            Point endPoint = pointTemporaryList.get(i + 1);
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, pointPaint);
        }
    }

    private void drawSelectedPoint(Canvas canvas) {
        for (int i = 0; i < pointFinalList.size(); i++) {
            if (i == currentSelectedPosition) {
                canvas.drawCircle(pointFinalList.get(i).x, pointFinalList.get(i).y, 10, pointPaint);
                canvas.drawCircle(pointFinalList.get(i).x, pointFinalList.get(i).y, 16, selectedPaint);
            } else {
                canvas.drawCircle(pointFinalList.get(i).x, pointFinalList.get(i).y, 10, pointPaint);
            }
        }
        for (int i = 0; i < pointFinalList.size() - 1; i++) {
            Point startPoint = pointFinalList.get(i);
            Point endPoint = pointFinalList.get(i + 1);
            canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, pointPaint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {


        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                try {
                    int moveX = (int) event.getX();
                    int moveY = (int) event.getY();

                    if (frameRect.contains(moveX, moveY)) {
                        int position = (moveX - frameRect.left) / pointScaleValueX;
                        Point point = pointFinalList.get(position);

                        long timeLong = Long.parseLong(trendList.get(position).date) * 1000;
                        CharSequence format = DateFormat.format("yyyy-MM-dd", timeLong);

                        priceShowView.setPoint(point);
                        priceShowView.setText("日期" + format + "\n"+unit+":" + trendPriceArray[position]);
                        if (currentSelectedPosition != position) {
                            invalidate();
                        }
                        currentSelectedPosition = position;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        super.onTouchEvent(event);
        return true;
    }


    /**
     * 画出,网格线和年月日
     */
    private void drawBg() {
        //重置背景
        setBackground(null);


        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.GRAY);
        linePaint.setStrokeWidth(1);
        Bitmap initBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(initBitmap);

        linePaint.setStyle(Paint.Style.STROKE);

        int width = getWidth();
        int height = getHeight();

        frameRect = new Rect(marginLeft, marginTop, width - marginRight, height - marginBottom);
        canvas.drawRect(frameRect, linePaint);

        priceShowView.setFrameRect(frameRect);

        //画出2条线
        for (int i = 1; i <= lineCount; i++) {
            int lineMultiple = (frameRect.height() / (lineCount + 1)) * i;
            canvas.drawLine(
                    marginLeft,
                    lineMultiple + frameRect.top,
                    marginLeft + frameRect.width(),
                    lineMultiple + frameRect.top,
                    linePaint);
        }

        int fontSize = SystemUtil.dp2px(getContext(), 13);
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.GRAY);
        txtPaint.setStrokeWidth(1);
        txtPaint.setTextSize(fontSize);

        String[] lineMultipleTxt = new String[3];
        int cha = maxPrice - minPrice;

        double itemPadding;
        if (cha == 0 || cha == 1) {
            itemPadding = 0.5f;
        } else {
            itemPadding = Math.ceil((double)cha / 2);
        }


        double leiJia = minPrice;
        for (int i = 2; i >= 0; i--) {
            lineMultipleTxt[i] = String.valueOf(leiJia);
            leiJia = leiJia + itemPadding;
        }

        for (int i = 0; i < lineMultipleTxt.length; i++) {
            int lineMultiple = (frameRect.height() / (lineCount + 1)) * i;

            String leftTxt = lineMultipleTxt[i];
            Rect txtRect = new Rect();
            txtPaint.getTextBounds(leftTxt, 0, leftTxt.length() - 1, txtRect);

            canvas.drawText(
                    lineMultipleTxt[i],
                    marginLeft - txtRect.width() - marginRight * 3,
                    lineMultiple + frameRect.top + fontSize,
                    txtPaint);

        }

        setBackground(new BitmapDrawable(initBitmap));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (triggerLevel == 2) {
            drawBg();
            calculatePointPosition();
        }

        //蓄势 背景技能发完毕,开始画点
        if (triggerLevel == 3) {
            drawLineChart(canvas);
        }

        if (triggerLevel == 4) {
            drawSelectedPoint(canvas);
        }
    }

    public void setPriceShowView(TrendChartTextView priceShowView) {
        this.priceShowView = priceShowView;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
