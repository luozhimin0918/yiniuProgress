package com.jyh.kxt.base.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/3/3.
 */

public class SlidingBackLayout extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    /**
     * 屏幕的宽度
     */
    private int screenWidth;
    /**
     * 阴影宽度
     */
    private int shadowWidth;
    /**
     * 默认的第一个子视图
     */
    private View defChildAt;

    public SlidingBackLayout(Context context) {
        super(context);
        initSlidingBack();
    }

    public SlidingBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSlidingBack();
    }

    public SlidingBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSlidingBack();
    }

    Paint mShadowPaint;
    Drawable mLeftShadow;
    int mShadowWidth;

    private void initSlidingBack() {
        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(0xff000000);

        mShadowWidth = SystemUtil.dp2px(getContext(), 10);

        mLeftShadow = getResources().getDrawable(R.drawable.shadow_left_edge);
        mLeftShadow.setBounds(0, 0, mShadowWidth, getHeight());
        setBackgroundDrawable(mLeftShadow);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawShadow(canvas);
    }

    /**
     * 绘制shadow阴影
     *
     * @param canvas
     */
    private void drawShadow(Canvas canvas) {
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initDragHelper();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper != null && viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private void initDragHelper() {
        if (viewDragHelper != null) {
            return;
        }

        defChildAt = getChildAt(0);

        screenWidth = getWidth();
        shadowWidth = SystemUtil.dp2px(getContext(), 10);

        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
                    @Override
                    public boolean tryCaptureView(View child, int pointerId) {
                        return child != defChildAt;
                    }

                    @Override
                    public int clampViewPositionHorizontal(View child, int left, int dx) {
                        if (child == defChildAt) {
                            mLeftShadow.setBounds(left - shadowWidth, 0, left, getHeight());
                        }
                        return left;
                    }

                    @Override
                    public int clampViewPositionVertical(View child, int top, int dy) {
                        return 0;
                    }

                    @Override
                    public void onViewReleased(View releasedChild, float xvel, float yvel) {
                        if (releasedChild == defChildAt) {
                            if (defChildAt.getLeft() > getWidth() / 2) {
                                startTranslationAnimator(getWidth(), 0);
                            } else {
                                startTranslationAnimator(0, 0);
                            }
                        }
                    }

                    @Override
                    public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                        viewDragHelper.captureChildView(defChildAt, pointerId);
                    }

                    @Override
                    public void onViewDragStateChanged(int state) {
                        super.onViewDragStateChanged(state);
                    }
                }

        );
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
    }

    private void startTranslationAnimator(int x, int y) {
        float currentX = defChildAt.getX();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(currentX, x);
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationX = (float) animation.getAnimatedValue();
                int offsetX = (int) translationX;
                defChildAt.setLeft(offsetX);
                mLeftShadow.setBounds(offsetX - mShadowWidth, 0, offsetX, getHeight());
            }
        });

        valueAnimator.addListener(
                new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (defChildAt.getLeft() == 0) {

                        } else {
                            Context context = getContext();
                            if (context instanceof Activity) {
                                ((BaseActivity) context).finish(true);
                            }
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        defChildAt.setLeft(0);
                        mLeftShadow.setBounds(0, 0, 0, getHeight());
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                }

        );
        valueAnimator.start();
    }
}
