package com.jyh.kxt.base.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mr'Dai on 2017/7/31.
 */

public class NavigationTabLayout extends LinearLayout {
    private Context mContext;

    private ArrayList<String> mTitles;
    private LinearLayout mTabsContainer;


    private int mOldClickPosition = 0;
    private int mCurrentClickPosition = 0;
    private float mCurrentPositionOffset = 0;

    private int mTabCount;
    /**
     * 用于绘制显示器
     */
    private Rect mIndicatorRect = new Rect();
    /**
     * 用于实现滚动居中
     */
    private Rect mTabRect = new Rect();
    private GradientDrawable mIndicatorDrawable = new GradientDrawable();

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path mTrianglePath = new Path();
    private static final int STYLE_NORMAL = 0;
    private static final int STYLE_TRIANGLE = 1;
    private static final int STYLE_BLOCK = 2;
    private int mIndicatorStyle = STYLE_NORMAL;

    private int modeScrollable;
    private float mTabPadding;
    private float mTabWidth;

    /**
     * indicator
     */
    private int mIndicatorColor;
    private float mIndicatorHeight;
    private float mIndicatorWidth;
    private float mIndicatorCornerRadius;
    private float mIndicatorMarginLeft;
    private float mIndicatorMarginTop;
    private float mIndicatorMarginRight;
    private float mIndicatorMarginBottom;
    private int mIndicatorGravity;
    private boolean mIndicatorWidthEqualTitle;

    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;
    private int mUnderlineGravity;

    /**
     * divider
     */
    private int mDividerColor;
    private float mDividerWidth;
    private float mDividerPadding;

    /**
     * title
     */
    private static final int TEXT_BOLD_NONE = 0;
    private static final int TEXT_BOLD_WHEN_SELECT = 1;
    private static final int TEXT_BOLD_BOTH = 2;
    private float mTextsize;
    private int mTextSelectColor;
    private int mTextUnselectColor;
    private int mTextUnselectColorResource;

    private int mTextBold;
    private boolean mTextAllCaps;

    public NavigationTabLayout(Context context) {
        this(context, null);
    }

    public NavigationTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);

        this.mContext = context;
        mTabsContainer = this;

        obtainAttributes(context, attrs);
    }


    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, com.library.R.styleable.SlidingTabLayout);

        mIndicatorStyle = ta.getInt(com.library.R.styleable.SlidingTabLayout_tl_indicator_style, STYLE_NORMAL);
        mIndicatorColor = ta.getColor(com.library.R.styleable.SlidingTabLayout_tl_indicator_color, Color.parseColor
                (mIndicatorStyle == STYLE_BLOCK ?
                        "#4B6A87" : "#ffffff"));
        mIndicatorHeight = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_height,
                dp2px(mIndicatorStyle == STYLE_TRIANGLE ? 4 : (mIndicatorStyle == STYLE_BLOCK ? -1 : 2)));
        mIndicatorWidth = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_width, dp2px
                (mIndicatorStyle ==
                        STYLE_TRIANGLE ? 10 :
                        -1));
        mIndicatorCornerRadius = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_corner_radius,
                dp2px
                        (mIndicatorStyle ==
                                STYLE_BLOCK ? -1 : 0));
        mIndicatorMarginLeft = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_margin_left,
                dp2px(0));
        mIndicatorMarginTop = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_margin_top, dp2px
                (mIndicatorStyle == STYLE_BLOCK
                        ? 7 : 0));
        mIndicatorMarginRight = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_margin_right,
                dp2px(0));
        mIndicatorMarginBottom = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_indicator_margin_bottom,
                dp2px
                        (mIndicatorStyle ==
                                STYLE_BLOCK ? 7 : 0));
        mIndicatorGravity = ta.getInt(com.library.R.styleable.SlidingTabLayout_tl_indicator_gravity, Gravity.BOTTOM);
        mIndicatorWidthEqualTitle = ta.getBoolean(com.library.R.styleable
                .SlidingTabLayout_tl_indicator_width_equal_title, false);

        mUnderlineColor = ta.getColor(com.library.R.styleable.SlidingTabLayout_tl_underline_color, Color.parseColor
                ("#ffffff"));
        mUnderlineHeight = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_underline_height, dp2px(0));
        mUnderlineGravity = ta.getInt(com.library.R.styleable.SlidingTabLayout_tl_underline_gravity, Gravity.BOTTOM);

        mDividerColor = ta.getColor(com.library.R.styleable.SlidingTabLayout_tl_divider_color, Color.parseColor
                ("#ffffff"));
        mDividerWidth = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_divider_width, dp2px(0));
        mDividerPadding = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_divider_padding, dp2px(12));

        mTextsize = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_textsize, sp2px(14));
        mTextSelectColor = ta.getColor(com.library.R.styleable.SlidingTabLayout_tl_textSelectColor, Color.parseColor
                ("#ffffff"));
        mTextUnselectColor = ta.getColor(com.library.R.styleable.SlidingTabLayout_tl_textUnselectColor, Color.parseColor
                ("#AAffffff"));
        mTextUnselectColorResource = ta.getResourceId(com.library.R.styleable.SlidingTabLayout_tl_textUnselectColor,
                com.library.R.color
                        .gray_btn_bg_color);

        mTextBold = ta.getInt(com.library.R.styleable.SlidingTabLayout_tl_textBold, TEXT_BOLD_NONE);
        mTextAllCaps = ta.getBoolean(com.library.R.styleable.SlidingTabLayout_tl_textAllCaps, false);

        mTabWidth = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_tab_width, dp2px(-1));
        mTabPadding = ta.getDimension(com.library.R.styleable.SlidingTabLayout_tl_tab_padding, 0);

        modeScrollable = ta.getInt(com.library.R.styleable.SlidingTabLayout_tl_mode_scrollable, 0);

        ta.recycle();
    }


    /**
     * 填充Title
     */
    public void setData(int titlesRes) {
        String[] stringArray = getResources().getStringArray(titlesRes);
        setData(stringArray);
    }

    /**
     * 填充Title
     */
    public void setData(String[] titles) {

        if (titles == null || titles.length == 0) {
            throw new IllegalStateException("Titles can not be EMPTY !");
        }
        mTitles = new ArrayList<>();
        Collections.addAll(mTitles, titles);

        notifyDataSetChanged();
    }


    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        try {
            mTabsContainer.removeAllViews();
            this.mTabCount = mTitles.size();
            View tabView;
            for (int i = 0; i < mTabCount; i++) {
                tabView = View.inflate(mContext, com.library.R.layout.layout_tab, null);
                CharSequence pageTitle = mTitles.get(i);
                addTab(i, pageTitle, tabView);
            }
            updateTabStyles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        ArrayList<View> touchViewList = getTouchables();
        for (int i = 0; i < touchViewList.size(); i++) {
            View itemView = touchViewList.get(i);
            if (isPinnedViewTouched(itemView, ev.getX(), ev.getY())) {

                mCurrentClickPosition = mTabsContainer.indexOfChild(itemView);
                if (mCurrentClickPosition == -1) {
                    continue;
                }
                if (mCurrentClickPosition != mOldClickPosition) {

                    //这里由于是属于虚拟画出来的子视图
                    if (mListener != null) {
                        mListener.onTabSelect(mCurrentClickPosition,0);
                    }
                    mCurrentPositionOffset = 1f;
                    animatorState = 0;
                    updateTabSelection(mCurrentClickPosition);
                    postInvalidate();

                    mOldClickPosition = mCurrentClickPosition;
                }
                break;
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private boolean isPinnedViewTouched(View view, float x, float y) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return rect.contains((int) x, (int) y);
    }

    /**
     * 创建并添加tab
     */
    private void addTab(int position, CharSequence title, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(com.library.R.id.tv_tab_title);
        if (tv_tab_title != null) {
            if (title != null) tv_tab_title.setText(title);
        }

        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isClickable && animatorState == 1) {
                    mCurrentClickPosition = mTabsContainer.indexOfChild(v);
                    if (mCurrentClickPosition == -1) {
                        return;
                    }
                    if (mCurrentClickPosition != mOldClickPosition) {
                        if (mListener != null) {
                            mListener.onTabSelect(mCurrentClickPosition,1);
                        }
                        clickTabAnimator();
                    }
                }
            }
        });

        /** 每一个Tab的布局参数 */
        LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1);

        if (mTabWidth > 0) {
            lp_tab = new LinearLayout.LayoutParams((int) mTabWidth, FrameLayout.LayoutParams.MATCH_PARENT);
        }

        mTabsContainer.addView(tabView, position, lp_tab);
    }

    private int animatorState = 0;

    private void clickTabAnimator() {
        float[] animatorFloats = new float[2];

        animatorFloats[0] = 0.0f;
        animatorFloats[1] = 1.0f;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(animatorFloats);
        valueAnimator.setDuration(150);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorState = 0;

                float animatedUpdateValue = (float) animation.getAnimatedValue();
                mCurrentPositionOffset = animatedUpdateValue;
                postInvalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animatorState = 1;
                mOldClickPosition = mCurrentClickPosition;
                updateTabSelection(mCurrentClickPosition);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                animatorState = 1;
                mOldClickPosition = mCurrentClickPosition;
                updateTabSelection(mCurrentClickPosition);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }


    private float margin;

    private void calcIndicatorRect() {
        if (animatorState != 0) {
            return;
        }
        animatorState = 1;

        View oldTabView = mTabsContainer.getChildAt(mOldClickPosition);
        if (oldTabView == null) {
            return;
        }
        float left = oldTabView.getLeft();
        float right = oldTabView.getRight();

        //for mIndicatorWidthEqualTitle
        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
            TextView tab_title = (TextView) oldTabView.findViewById(com.library.R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextsize);
            float textWidth = mTextPaint.measureText(tab_title.getText().toString());
            margin = (right - left - textWidth) / 2;

        }
        if (this.mCurrentClickPosition < mTabCount) {
            View nextTabView = mTabsContainer.getChildAt(this.mCurrentClickPosition);
            float nextTabLeft = nextTabView.getLeft();
            float nextTabRight = nextTabView.getRight();

            float offsetLeft = mCurrentPositionOffset * (nextTabLeft - left);
            left = left + offsetLeft;

            float offsetRight = mCurrentPositionOffset * (nextTabRight - right);
            right = right + offsetRight;

            //for mIndicatorWidthEqualTitle
            if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
                TextView next_tab_title = (TextView) nextTabView.findViewById(com.library.R.id.tv_tab_title);
                mTextPaint.setTextSize(mTextsize);
                float nextTextWidth = mTextPaint.measureText(next_tab_title.getText().toString());
                float nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2;
                margin = margin + mCurrentPositionOffset * (nextMargin - margin);
            }
        }

        int padding = (oldTabView.getWidth() / 2 - (int) mIndicatorWidth / 2);
        mIndicatorRect.left = (int) left + padding;
        mIndicatorRect.right = mIndicatorRect.left + (int) mIndicatorWidth;

        mTabRect.left = (int) left;
        mTabRect.right = (int) right;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || mTabCount <= 0) {
            return;
        }

        int height = getHeight();
        int paddingLeft = getPaddingLeft();

        //draw indicator line
        calcIndicatorRect();

        if (mIndicatorStyle == STYLE_TRIANGLE) {
            if (mIndicatorHeight > 0) {
                mTrianglePaint.setColor(mIndicatorColor);
                mTrianglePath.reset();
                mTrianglePath.moveTo(paddingLeft + mIndicatorRect.left, height);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.left / 2 + mIndicatorRect.right / 2, height -
                        mIndicatorHeight);
                mTrianglePath.lineTo(paddingLeft + mIndicatorRect.right, height);
                mTrianglePath.close();
                canvas.drawPath(mTrianglePath, mTrianglePaint);
            }
        } else if (mIndicatorStyle == STYLE_BLOCK) {
            if (mIndicatorHeight < 0) {
                mIndicatorHeight = height - mIndicatorMarginTop - mIndicatorMarginBottom;
            } else {

            }

            if (mIndicatorHeight > 0) {
                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
                    mIndicatorCornerRadius = mIndicatorHeight / 2;
                }

                mIndicatorDrawable.setColor(mIndicatorColor);
                mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                        (int) mIndicatorMarginTop, (int) (paddingLeft + mIndicatorRect.right - mIndicatorMarginRight),
                        (int) (mIndicatorMarginTop + mIndicatorHeight));
                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        } else {
            if (mIndicatorHeight > 0) {
                mIndicatorDrawable.setColor(mIndicatorColor);

                if (mIndicatorGravity == Gravity.BOTTOM) {
                    int left = mIndicatorRect.left;
                    int top = height - (int) mIndicatorHeight - (int) mIndicatorMarginBottom;
                    int right = mIndicatorRect.right;
                    int bottom = height - (int) mIndicatorMarginBottom;
                    mIndicatorDrawable.setBounds(left,
                            top,
                            right,
                            bottom);

                    Log.e("mIndicatorDrawable", "mIndicatorRect.left " + mIndicatorRect.left +
                            " mIndicatorRect.right " + mIndicatorRect.right);
                } else {
                    mIndicatorDrawable.setBounds(paddingLeft + (int) mIndicatorMarginLeft + mIndicatorRect.left,
                            (int) mIndicatorMarginTop,
                            paddingLeft + mIndicatorRect.right - (int) mIndicatorMarginRight,
                            (int) mIndicatorHeight + (int) mIndicatorMarginTop);
                }

                mIndicatorDrawable.setCornerRadius(mIndicatorCornerRadius);
                mIndicatorDrawable.draw(canvas);
            }
        }
    }


    private boolean isClickable = true;

    @Override
    public boolean isClickable() {
        return isClickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    protected void updateTabStyles() {
        mTextUnselectColor = ContextCompat.getColor(mContext, mTextUnselectColorResource);
        for (int i = 0; i < mTabCount; i++) {
            View v = mTabsContainer.getChildAt(i);
//            v.setPadding((int) mTabPadding, v.getPaddingTop(), (int) mTabPadding, v.getPaddingBottom());
            TextView tv_tab_title = (TextView) v.findViewById(com.library.R.id.tv_tab_title);
            if (tv_tab_title != null) {
                tv_tab_title.setTextColor(i == mCurrentClickPosition ? mTextSelectColor : mTextUnselectColor);
                tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextsize);
                tv_tab_title.setPadding((int) mTabPadding, 0, (int) mTabPadding, 0);
                if (mTextAllCaps) {
                    tv_tab_title.setText(tv_tab_title.getText().toString().toUpperCase());
                }

                if (mTextBold == TEXT_BOLD_BOTH) {
                    tv_tab_title.getPaint().setFakeBoldText(true);
                } else if (mTextBold == TEXT_BOLD_NONE) {
                    tv_tab_title.getPaint().setFakeBoldText(false);
                }
            }
        }
    }

    private void updateTabSelection(int position) {
        mTextUnselectColor = ContextCompat.getColor(mContext, mTextUnselectColorResource);
        for (int i = 0; i < mTabCount; ++i) {
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = (TextView) tabView.findViewById(com.library.R.id.tv_tab_title);

            if (tab_title != null) {
                tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
                if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
                    tab_title.getPaint().setFakeBoldText(isSelect);
                }
            }
        }
    }


    public void setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        invalidate();
    }

    public void setTabPadding(float tabPadding) {
        this.mTabPadding = dp2px(tabPadding);
        updateTabStyles();
    }

    public void setTabWidth(float tabWidth) {
        this.mTabWidth = dp2px(tabWidth);

        for (int i = 0; i < mTabCount; i++) {
            View childAt = mTabsContainer.getChildAt(i);

            ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
            layoutParams.width = (int) mTabWidth;

            childAt.setLayoutParams(layoutParams);
        }

        updateTabStyles();
    }

    public void setIndicatorColor(int indicatorColor) {
        this.mIndicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        invalidate();
    }

    public void setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        invalidate();
    }

    public void setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        invalidate();
    }

    public void setIndicatorGravity(int indicatorGravity) {
        this.mIndicatorGravity = indicatorGravity;
        invalidate();
    }

    public void setIndicatorMargin(float indicatorMarginLeft, float indicatorMarginTop,
                                   float indicatorMarginRight, float indicatorMarginBottom) {
        this.mIndicatorMarginLeft = dp2px(indicatorMarginLeft);
        this.mIndicatorMarginTop = dp2px(indicatorMarginTop);
        this.mIndicatorMarginRight = dp2px(indicatorMarginRight);
        this.mIndicatorMarginBottom = dp2px(indicatorMarginBottom);
        invalidate();
    }

    public void setIndicatorWidthEqualTitle(boolean indicatorWidthEqualTitle) {
        this.mIndicatorWidthEqualTitle = indicatorWidthEqualTitle;
        invalidate();
    }

    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = dp2px(underlineHeight);
        invalidate();
    }

    public void setUnderlineGravity(int underlineGravity) {
        this.mUnderlineGravity = underlineGravity;
        invalidate();
    }

    public void setDividerColor(int dividerColor) {
        this.mDividerColor = dividerColor;
        invalidate();
    }

    public void setDividerWidth(float dividerWidth) {
        this.mDividerWidth = dp2px(dividerWidth);
        invalidate();
    }

    public void setDividerPadding(float dividerPadding) {
        this.mDividerPadding = dp2px(dividerPadding);
        invalidate();
    }

    public void setTextsize(float textsize) {
        this.mTextsize = sp2px(textsize);
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnselectColor) {
        this.mTextUnselectColor = textUnselectColor;
        updateTabStyles();
    }

    public void setTextBold(int textBold) {
        this.mTextBold = textBold;
        updateTabStyles();
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.mTextAllCaps = textAllCaps;
        updateTabStyles();
    }

    public void setCurrentTab(int tabPosition) {
        mCurrentClickPosition = tabPosition;
        mCurrentPositionOffset = 1;
        animatorState = 0;
        updateTabSelection(mCurrentClickPosition);
        postInvalidate();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mOldClickPosition = mCurrentClickPosition;
                animatorState = 1;
            }
        },100);
    }


    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentClickPosition;
    }

    public int getIndicatorStyle() {
        return mIndicatorStyle;
    }

    public float getTabPadding() {
        return mTabPadding;
    }

    public float getTabWidth() {
        return mTabWidth;
    }

    public int getIndicatorColor() {
        return mIndicatorColor;
    }

    public float getIndicatorHeight() {
        return mIndicatorHeight;
    }

    public float getIndicatorWidth() {
        return mIndicatorWidth;
    }

    public float getIndicatorCornerRadius() {
        return mIndicatorCornerRadius;
    }

    public float getIndicatorMarginLeft() {
        return mIndicatorMarginLeft;
    }

    public float getIndicatorMarginTop() {
        return mIndicatorMarginTop;
    }

    public float getIndicatorMarginRight() {
        return mIndicatorMarginRight;
    }

    public float getIndicatorMarginBottom() {
        return mIndicatorMarginBottom;
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public float getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public float getDividerWidth() {
        return mDividerWidth;
    }

    public float getTextsize() {
        return mTextsize;
    }

    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return mTextUnselectColor;
    }

    public int getTextBold() {
        return mTextBold;
    }

    public boolean isTextAllCaps() {
        return mTextAllCaps;
    }

    public TextView getTitleView(int tab) {
        View tabView = mTabsContainer.getChildAt(tab);
        TextView tv_tab_title = (TextView) tabView.findViewById(com.library.R.id.tv_tab_title);
        return tv_tab_title;
    }

    //setter and getter

    // show MsgTipView
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();


    public interface OnTabSelectListener {
        void onTabSelect(int position,int clickId);
    }

    private OnTabSelectListener mListener;

    public void setOnTabSelectListener(OnTabSelectListener listener) {
        this.mListener = listener;
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentClickPosition", mCurrentClickPosition);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentClickPosition = bundle.getInt("mCurrentClickPosition");
            state = bundle.getParcelable("instanceState");
            if (mCurrentClickPosition != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentClickPosition);
            }
        }
        super.onRestoreInstanceState(state);
    }

    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
