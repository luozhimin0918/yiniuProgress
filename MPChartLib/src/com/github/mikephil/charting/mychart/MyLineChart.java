package com.github.mikephil.charting.mychart;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.highlight.Highlight;

/**
 * 作者：ajiang
 * 邮箱：1025065158
 * 博客：http://blog.csdn.net/qqyanjiang
 */
public class MyLineChart extends LineChart {

    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface OnDoubleTapListener {
        void onDoubleTap();
    }

    private CombinedChart.OnDoubleTapListener onDoubleTapListener;

    public void setOnDoubleTapListener(CombinedChart.OnDoubleTapListener onDoubleTapListener) {
        this.onDoubleTapListener = onDoubleTapListener;
    }

    public void onDoubleTap() {
        if (onDoubleTapListener != null) {
            onDoubleTapListener.onDoubleTap();
        }
    }

    @Override
    protected void init() {
        super.init();
        /*此两处不能重新示例*/
        mXAxis = new MyXAxis();
        mAxisLeft = new MyYAxis(YAxis.AxisDependency.LEFT);
        mXAxisRenderer = new MyXAxisRenderer(mViewPortHandler, (MyXAxis) mXAxis, mLeftAxisTransformer, this);
        mAxisRendererLeft = new MyYAxisRenderer(mViewPortHandler, (MyYAxis) mAxisLeft, mLeftAxisTransformer);

        mAxisRight = new MyYAxis(YAxis.AxisDependency.RIGHT);
        mAxisRendererRight = new MyYAxisRenderer(mViewPortHandler, (MyYAxis) mAxisRight, mRightAxisTransformer);

    }

    /*返回转型后的左右轴*/
    @Override
    public MyYAxis getAxisLeft() {
        return (MyYAxis) super.getAxisLeft();
    }

    @Override
    public MyXAxis getXAxis() {
        return (MyXAxis) super.getXAxis();
    }


    @Override
    public MyYAxis getAxisRight() {
        return (MyYAxis) super.getAxisRight();
    }

    public void setMarker(MyLeftMarkerView markerLeft, MyRightMarkerView markerRight, MyBottomMarkerView
            markerBottom) {
    }

    public void setHighlightValue(Highlight h) {
        if (mData == null) {
            mIndicesToHighlight = null;
        } else {
            mIndicesToHighlight = new Highlight[]{h};
        }
        invalidate();
    }

    @Override
    protected void drawMarkers(Canvas canvas) {
    }
}
