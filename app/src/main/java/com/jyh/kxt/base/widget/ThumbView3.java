package com.jyh.kxt.base.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;

/**
 * Created by Mr'Dai on 2017/5/4.
 * 同意管理点赞动画
 */
public class ThumbView3 extends RelativeLayout {

    private TextView giveView;

    public ThumbView3(Context context) {
        this(context, null);
    }

    public ThumbView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
    }

    private void initGiveView() {
        giveView = new TextView(getContext());
        giveView.setText("+1");
        giveView.setTextSize(12);
        giveView.setTextColor(ContextCompat.getColor(getContext(), R.color.red2));
        giveView.setVisibility(View.GONE);
        LayoutParams mGiveParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mGiveParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(giveView, mGiveParams);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        initGiveView();
    }

    public void startGiveAnimation() {
        giveView.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.glive_anim);
        giveView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giveView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (giveView != null) {
            giveView.setVisibility(View.GONE);
        }
    }
}
