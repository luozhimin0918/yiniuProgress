package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.format.DateFormat;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.HighlightLineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by Philipp Jahoda on 11/07/15.
 * 高亮
 */
public abstract class LineScatterCandleRadarRenderer extends DataRenderer {

    /**
     * path that is used for drawing highlight-lines (drawLines(...) cannot be used because of dashes)
     */
    private Path mHighlightLinePath = new Path();

    public LineScatterCandleRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    //K线图滑动时日期跟随效果
    public void drawHighlightLines(Canvas c, float[] pts, ILineScatterCandleRadarDataSet set, HighlightLineData
            highlightLineData) {
        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());
        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {
            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(pts[0], mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(pts[0], mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);

            String dateTimeLabel = highlightLineData.getDateTime();
            if (dateTimeLabel != null) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    if ("minute".equals(set.getLabel())) {
                        Date parse = simpleDateFormat.parse(highlightLineData.getDateTime());
                        dateTimeLabel = DateFormat.format("HH:mm", parse.getTime()).toString();
                    } else {
                        String mySetLabel = set.getLabel();
                        if ("KLine".equals(mySetLabel.split(":")[0])) {
                            //符合K线要求
                            String fromSource = mySetLabel.split(":")[1];
                            Date parse = simpleDateFormat.parse(highlightLineData.getDateTime());
                            switch (fromSource) {
                                case "1":
                                case "2":
                                case "3":
                                case "4":
                                    dateTimeLabel = DateFormat.format("MM-dd HH:mm", parse.getTime()).toString();
                                    break;
                                case "5":
                                case "6":
                                    dateTimeLabel = DateFormat.format("MM-dd", parse.getTime()).toString();
                                    break;
                                case "7":
                                    dateTimeLabel = DateFormat.format("yyyy-MM", parse.getTime()).toString();
                                    break;
                            }

                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //增加日期
                float right = pts[0];

                int textPadding = 3;
                float labelLineHeight = Utils.calcTextHeight(mHighlightDatePaint, dateTimeLabel);
                float labelLineWidth = Utils.calcTextWidth(mHighlightDatePaint, dateTimeLabel);


                mHighlightDatePaint.setColor(set.getHighLightColor());
                float rectLeft = right - labelLineWidth / 2 - textPadding;
                float rectRight = right + labelLineWidth / 2 + textPadding;

                float viewDrawWidth = (rectRight - rectLeft) / 2;
                //如果在最左边或者最右边,则不要遮挡
                RectF contentRect = mViewPortHandler.getContentRect();
                if (rectLeft < contentRect.left) { //最左边了
                    right = contentRect.left + viewDrawWidth;
                } else if (rectRight > contentRect.right) {
                    right = contentRect.right - viewDrawWidth;
                }

                if ("minute".equals(set.getLabel())) {
                    float top = mViewPortHandler.contentTop();
                    c.drawRect(new RectF(right - labelLineWidth / 2 - textPadding,
                                    top,
                                    right + labelLineWidth / 2 + textPadding,
                                    top + labelLineHeight + textPadding),
                            mHighlightDatePaint);

                    mHighlightDatePaint.setColor(Color.WHITE);
                    c.drawText(dateTimeLabel,
                            right - labelLineWidth / 2,
                            top + labelLineHeight, mHighlightDatePaint);
                } else {
                    float bottom = mViewPortHandler.contentBottom();
                    c.drawRect(new RectF(right - labelLineWidth / 2 - textPadding,
                                    bottom - labelLineHeight - textPadding,
                                    right + labelLineWidth / 2 + textPadding,
                                    bottom + textPadding),
                            mHighlightDatePaint);

                    mHighlightDatePaint.setColor(Color.WHITE);
                    c.drawText(dateTimeLabel,
                            right - labelLineWidth / 2,
                            bottom, mHighlightDatePaint);
                }
            }
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(mHighlightLinePath, mHighlightPaint);

            int xIndex = highlightLineData.getxIndex();
            Entry entryForXIndex = set.getEntryForXIndex(xIndex);
            if (entryForXIndex != null) {
                String price = String.valueOf(entryForXIndex.getVal());

                int textPadding = 3;

                /**
                 * 绘制价格
                 */
                float labelLineHeight = Utils.calcTextHeight(mHighlightDatePaint, price);
                float labelLineWidth = Utils.calcTextWidth(mHighlightDatePaint, price);

                mHighlightDatePaint.setColor(set.getHighLightColor());

                float left = mViewPortHandler.contentLeft();
                c.drawRect(new RectF(left,
                                pts[1] - labelLineHeight - textPadding,
                                left + labelLineWidth + textPadding,
                                pts[1] + textPadding),
                        mHighlightDatePaint);

                mHighlightDatePaint.setColor(Color.WHITE);
                c.drawText(price,
                        left,
                        pts[1], mHighlightDatePaint);

                /**
                 * 绘制涨跌幅
                 */
                LineDataSet lineDataSet = (LineDataSet) set;
                float newPrice = Float.parseFloat(price);
                double zdfValue = ((newPrice - lineDataSet.getLastPrice()) / lineDataSet.getLastPrice()) * 100;
                DecimalFormat df = new DecimalFormat("######0.0000");
                String zdf = df.format(zdfValue) + "%";

                labelLineHeight = Utils.calcTextHeight(mHighlightDatePaint, zdf);
                labelLineWidth = Utils.calcTextWidth(mHighlightDatePaint, zdf);
                mHighlightDatePaint.setColor(set.getHighLightColor());
                //右边的
                float right = mViewPortHandler.contentRight();
                c.drawRect(new RectF(right - labelLineWidth - textPadding,
                                pts[1] - labelLineHeight - textPadding,
                                right,
                                pts[1] + textPadding),
                        mHighlightDatePaint);

                mHighlightDatePaint.setColor(Color.WHITE);
                c.drawText(zdf,
                        right - labelLineWidth - textPadding,
                        pts[1], mHighlightDatePaint);

            }
        }
    }

    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c
     * @param pts the transformed x- and y-position of the lines
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(Canvas c, float[] pts, ILineScatterCandleRadarDataSet set) {
        drawHighlightLines(c, pts, set, null);
    }


    /**
     * Draws vertical & horizontal highlight-lines if enabled.
     *
     * @param c
     * @param pts the transformed x- and y-position of the lines
     * @param set the currently drawn dataset
     */
    protected void drawHighlightLines(Canvas c, int index, float[] pts, ILineScatterCandleRadarDataSet set) {

        // set color and stroke-width
        mHighlightPaint.setColor(set.getHighLightColor());
        mHighlightPaint.setStrokeWidth(set.getHighlightLineWidth());
        // draw highlighted lines (if enabled)
        mHighlightPaint.setPathEffect(set.getDashPathEffectHighlight());

        // draw vertical highlight lines
        if (set.isVerticalHighlightIndicatorEnabled()) {
            // create vertical path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(pts[0], mViewPortHandler.contentTop());
            mHighlightLinePath.lineTo(pts[0], mViewPortHandler.contentBottom());

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }

        // draw horizontal highlight lines
        if (set.isHorizontalHighlightIndicatorEnabled()) {

            // create horizontal path
            mHighlightLinePath.reset();
            mHighlightLinePath.moveTo(mViewPortHandler.contentLeft(), pts[1]);
            mHighlightLinePath.lineTo(mViewPortHandler.contentRight(), pts[1]);

            c.drawPath(mHighlightLinePath, mHighlightPaint);
        }
    }
}
