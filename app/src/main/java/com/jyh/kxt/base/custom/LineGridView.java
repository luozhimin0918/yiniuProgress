package com.jyh.kxt.base.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/7/31.
 */

public class LineGridView extends GridView {

    private int gridRowsCount;
    private Paint linePaint;
    private int lineNumColumns = 0;

    public LineGridView(Context context) {
        this(context, null);
    }

    public LineGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.gray_btn_bg_color));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (linePaint != null) {
            if (getAdapter() == null) {
                return;
            }

            int gridHeight = getHeight();
            int gridWidth = getWidth();

            int itemHeight = gridHeight / gridRowsCount;
            int itemWidth = gridWidth / lineNumColumns;

            for (int i = 1; i < gridRowsCount; i++) {
                canvas.drawLine(0, i * itemHeight, gridWidth, i * itemHeight, linePaint);
            }

            for (int i = 1; i < lineNumColumns; i++) {
                canvas.drawLine(i * itemWidth, 0, i * itemWidth, gridHeight, linePaint);
            }
        }

    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        /*gridRowsCount = adapter.getCount() % lineNumColumns == 0 ?
                adapter.getCount() / lineNumColumns :
                adapter.getCount() / lineNumColumns + 1;*/
        gridRowsCount = adapter.getCount() % lineNumColumns == 0 ?
                adapter.getCount() / lineNumColumns :
                adapter.getCount() / lineNumColumns + 1;
    }

    public void setLineNumColumns(int lineNumColumns) {
        this.lineNumColumns = lineNumColumns;
    }
}
