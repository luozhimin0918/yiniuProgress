package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

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
    public void drawHighlightLines(Canvas c, float[] pts, ILineScatterCandleRadarDataSet set, String label) {
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

            if (label != null) {
                String[] splitLabelDate = label.split(" ");

                label = splitLabelDate[1];//读取后面一部分
                boolean isUseful = Pattern.matches(".*[1-9].*", label);
                if (!isUseful) {
                    label = splitLabelDate[0];//读取前面一部分
                }

                //增加日期
                float right = pts[0];
                float bottom = mViewPortHandler.contentBottom();

                int textPadding = 3;
                float labelLineHeight = Utils.calcTextHeight(mHighlightDatePaint, label);
                float labelLineWidth = Utils.calcTextWidth(mHighlightDatePaint, label);


                //如果在最左边或者最右边,则不要遮挡
                if (right < labelLineWidth) { //最左边了

                } else if (right - labelLineWidth < 0) {

                }
                mHighlightDatePaint.setColor(set.getHighLightColor());
                c.drawRect(new RectF(right - labelLineWidth / 2 - textPadding,
                                bottom - labelLineHeight - textPadding,
                                right + labelLineWidth / 2 + textPadding,
                                bottom + textPadding),
                        mHighlightDatePaint);

                mHighlightDatePaint.setColor(Color.WHITE);
                c.drawText(label,
                        right - labelLineWidth / 2,
                        bottom, mHighlightDatePaint);
            }
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
