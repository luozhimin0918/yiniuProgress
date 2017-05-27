package com.jyh.kxt.base.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.jyh.kxt.R;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/5/18.
 */

public class RollDotView extends View {

    private int circleSize;
    private int circleCount = 4;
    private int circlePadding;
    private int selectedPosition = 0;
    private int defaultCircleColor;
    private int selectedCircleColor;

    private Paint circlePaint;

    public RollDotView(Context context) {
        this(context, null);
    }

    public RollDotView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollDotView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);

        circleSize = SystemUtil.dp2px(getContext(), 3);
        circlePadding = SystemUtil.dp2px(getContext(), 3);

    }


    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        defaultCircleColor = ContextCompat.getColor(getContext(), R.color.unaltered_color);
        selectedCircleColor = ContextCompat.getColor(getContext(), R.color.decline_color);

        for (int i = 0; i < circleCount; i++) {
            if (selectedPosition == i) {
                circlePaint.setColor(selectedCircleColor);
                canvas.drawCircle(circleSize * 2 * i + circleSize + circlePadding * i, circleSize, circleSize,
                        circlePaint);
            } else {
                circlePaint.setColor(defaultCircleColor);
                canvas.drawCircle(circleSize * 2 * i + circleSize + circlePadding * i, circleSize, circleSize,
                        circlePaint);
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int measureWidth = SystemUtil.dp2px(getContext(), circleSize * circleCount) + circlePadding * circleCount;
        int measureHeight = SystemUtil.dp2px(getContext(), circleSize + circlePadding);
        setMeasuredDimension(measureWidth, measureHeight);

    }

    public void onChangeTheme() {
        postInvalidate();
    }
}
