package com.jyh.kxt.base.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.jyh.kxt.R;
import com.jyh.kxt.datum.bean.TrendBean;
import com.library.util.SystemUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/13.
 */

public class TrendChartView extends View {
    //框框内部的线的个数
    private final int lineCount = 2;
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
    private int minPricePoint, maxPricePoint;
    //触发等级   1 蓄势不触发  2 蓄势画背景横线  3 蓄势完成画折线动画 4 点击放大圆点
    private int triggerLevel = 1;
    private int currentSelectedPosition = -1;

    private TrendChartTextView priceShowView;

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

        minPricePoint = minPrice;
        maxPricePoint = maxPrice;

        if (triggerLevel == 2) {
            drawBg();
        } else {
            triggerLevel += 1;
        }
    }


    //点的位置缩放比例值
    private int pointScaleValueY;
    private int pointScaleValueX;

    private List<Point> pointFinalList;
    private List<Point> pointTemporaryList;

    /**
     * 计算所有点的位置
     */
    private void calculatePointPosition() {
        triggerLevel = 3; //重置蓄势

        dataCount = trendPriceArray.length;

        pointScaleValueX = (frameRect.width() - (padding * 2)) / (dataCount - 1);
        pointScaleValueY = frameRect.height() / (maxPricePoint - minPricePoint);

        pointFinalList = new ArrayList<>();
        pointTemporaryList = new ArrayList<>();

        for (int i = 0; i < dataCount; i++) {
            Point point = generateCoordsPoint(i);
            pointFinalList.add(point);
            pointTemporaryList.add(new Point(point.x, frameRect.height()));
        }

        ValueAnimator valueAnimator = ValueAnimator.ofInt(maxPricePoint, minPricePoint);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int animatedValue = (int) valueAnimator.getAnimatedValue();

                for (int i = 0; i < pointTemporaryList.size(); i++) {

                    Point temporaryPoint = pointTemporaryList.get(i);
                    Point finalPoint = pointFinalList.get(i);

                    if (temporaryPoint.y > finalPoint.y) {
                        int pointPositionY = frameRect.height() - (maxPricePoint - animatedValue) * pointScaleValueY;
                        temporaryPoint.y = pointPositionY;
                    }
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                triggerLevel = 4;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                triggerLevel = 4;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.start();
    }

    /**
     * 生成坐标点
     *
     * @param position
     */
    private Point generateCoordsPoint(int position) {
        Double price = trendPriceArray[position];

        int pointPositionX = padding + frameRect.left + position * pointScaleValueX;
        int pointPositionY = frameRect.height() - (int) ((price - minPricePoint) * pointScaleValueY);

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
                        priceShowView.setText("日期"+format+"\n价格:" + trendPriceArray[position]);
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
            canvas.drawLine(marginLeft, lineMultiple + frameRect.top, marginLeft + frameRect.width(), lineMultiple +
                    frameRect.top, linePaint);
        }

        int fontSize = SystemUtil.dp2px(getContext(), 13);
        Paint txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.GRAY);
        txtPaint.setStrokeWidth(1);
        txtPaint.setTextSize(fontSize);

        String[] lineMultipleTxt = new String[4];
        int cha = maxPrice - minPrice;
        int yuShu = cha % 3;

        int itemPadding = yuShu == 0 ? cha / 3 : (cha - yuShu) / 3;

        int leiJia = minPrice;
        for (int i = 3; i >= 0; i--) {
            lineMultipleTxt[i] = String.valueOf(leiJia);
            leiJia = leiJia + itemPadding + (yuShu == 0 ? 0 : 1);
        }

        for (int i = 0; i < lineMultipleTxt.length; i++) {
            int lineMultiple = (frameRect.height() / (lineCount + 1)) * i;

            String leftTxt = lineMultipleTxt[i];
            Rect txtRect = new Rect();
            txtPaint.getTextBounds(leftTxt, 0, leftTxt.length() - 1, txtRect);

            canvas.drawText(
                    lineMultipleTxt[i],
                    marginLeft - txtRect.width() - marginRight * 3,
                    lineMultiple + txtRect.height() + frameRect.top,
                    txtPaint);

        }

        /*for (int i = 0; i < trendList.size(); i++) {
            TrendBean trendBean = trendList.get(i);

            long timeLong = Long.parseLong(trendBean.date) * 1000;
            CharSequence format = DateFormat.format("yyyy-MM-dd", timeLong);

            Point point = generateCoordsPoint(i);

            canvas.drawText(
                    format.toString(),
                    point.x,
                    frameRect.bottom,
                    txtPaint);
        }*/


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
}
