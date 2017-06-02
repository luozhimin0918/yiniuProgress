package com.jyh.kxt.base.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.utils.NativeStore;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/5/4.
 * 同意管理点赞动画
 */
public class ThumbView2 extends RelativeLayout {

    /**
     * 是否赞过
     */
    private boolean isThumb = false;

    private ImageView ivThumb;
    private TextView tvThumbCount;
    private TextView tvThumbAddCount;

    private Animation mAnimation;
    private String type;
    private String id;

    public ThumbView2(Context context) {
        this(context, null);
    }

    public ThumbView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {

        int thumbCountColor = ContextCompat.getColor(getContext(), R.color.font_color9);
        int thumbAddColor = ContextCompat.getColor(getContext(), R.color.font_color11);
        int marginsRight = SystemUtil.dp2px(getContext(), 5);

        //点赞的图片
        ivThumb = new ImageView(getContext());
        ivThumb.setId(R.id.iv_thumb);

        LayoutParams thumbImageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        thumbImageParams.setMargins(0, 0, marginsRight, 0);

        ivThumb.setLayoutParams(thumbImageParams);
        addView(ivThumb);


        //点赞数量
        tvThumbCount = new TextView(getContext());
        tvThumbCount.setId(R.id.tv_thumb_count);
        tvThumbCount.setTextColor(thumbCountColor);

        LayoutParams thumbCountParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        thumbCountParams.addRule(RelativeLayout.RIGHT_OF, R.id.iv_thumb);

        tvThumbCount.setLayoutParams(thumbCountParams);
        addView(tvThumbCount);

        //点赞累加
        tvThumbAddCount = new TextView(getContext());
        tvThumbAddCount.setText("+1");
        tvThumbAddCount.setTextColor(thumbAddColor);
        tvThumbAddCount.setVisibility(View.GONE);

        TextPaint tp = tvThumbAddCount.getPaint();
        tp.setFakeBoldText(true);

        tvThumbAddCount.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(tvThumbAddCount);

        setClipToPadding(false);
        setClipChildren(false);

    }

    /**
     * 请求点赞
     */
    private void requestClickThumb() {
        NativeStore.addThumbID(getContext(), type, id, null, null);
        changerCount(1);
        ivThumb.setImageResource(R.mipmap.icon_share_ding_sel);
        tvThumbAddCount.setVisibility(View.VISIBLE);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.thumb_anim);
        tvThumbAddCount.startAnimation(mAnimation);
        tvThumbAddCount.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvThumbAddCount.setVisibility(View.GONE);
            }
        }, mAnimation.getDuration());
        isThumb = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (tvThumbAddCount != null && tvThumbAddCount.isShown()) {
            tvThumbAddCount.setVisibility(View.GONE);
        }
    }

    public void setThumbCount(int count, String id, String type, boolean isThumb) {
        this.id=id;
        this.type=type;
        this.isThumb = isThumb;
        if (isThumb) {
            ivThumb.setImageResource(R.mipmap.icon_share_ding_sel);
        } else {
            ivThumb.setImageResource(R.mipmap.icon_share_ding_unsel);
        }

        if (tvThumbCount != null) {
            tvThumbCount.setText(String.valueOf(count));
        }
    }

    private void changerCount(int count) {
        CharSequence currentCount = tvThumbCount.getText();
        String updateCountValue = String.valueOf(Integer.parseInt(currentCount.toString()) + count);
        tvThumbCount.setText(updateCountValue);
    }

    public void attention() {
        requestClickThumb();
    }

    public void onChangeTheme() {
        if (isThumb) {
            ivThumb.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_share_ding_sel));
        } else {
            ivThumb.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_share_ding_unsel));
        }
        tvThumbCount.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color9));
        tvThumbAddCount.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color11));
    }
}
