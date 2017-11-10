package com.jyh.kxt.base.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;

public class MyImageSpan extends ImageSpan {

    public static enum AlignPosition {
        ALIGN_BOTTOM, ALIGN_CENTER, ALIGN_TOP
    }

    private AlignPosition mAlignPosition = AlignPosition.ALIGN_TOP;

    public MyImageSpan(Drawable d) {
        super(d);
    }

    public MyImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public MyImageSpan(Drawable d, AlignPosition mAlignPosition) {
        super(d);
        this.mAlignPosition = mAlignPosition;
    }

    public int getSize(Paint paint, CharSequence text, int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getDrawable();
        Rect rect = d.getBounds();
        if (fm != null) {
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.bottom - fmPaint.top;
            int drHeight = rect.bottom - rect.top;

            int top = drHeight / 2 - fontHeight / 4;
            int bottom = drHeight / 2 + fontHeight / 4;

            fm.ascent = -bottom;
            fm.top = -bottom;
            fm.bottom = top;
            fm.descent = top;
        }
        return rect.right;
    }

    @Override
    public void draw(Canvas canvas,
                     CharSequence text,
                     int start, int end,
                     float x,
                     int top,
                     int y,
                     int bottom,
                     Paint paint) {

        Drawable b = getDrawable();
        canvas.save();

        if (mAlignPosition == AlignPosition.ALIGN_CENTER) {
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int transY = (y + fontMetricsInt.descent + y + fontMetricsInt.ascent) / 2 - b.getBounds().bottom / 2;
            canvas.translate(x, transY);

        }else if (mAlignPosition == AlignPosition.ALIGN_TOP){
            canvas.translate(x, top);
        }
        b.draw(canvas);
        canvas.restore();
    }
}