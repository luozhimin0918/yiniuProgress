package com.jyh.kxt.base.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;
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
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.NativeStore;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

/**
 * Created by Mr'Dai on 2017/5/4.
 * 同意管理点赞动画
 */
public class ThumbView extends RelativeLayout {

    /**
     * 是否赞过
     */
    private boolean isThumb = false;
    //点赞的ID值
    private int thumbId = 0;

    private ImageView ivThumb;
    private TextView tvThumbCount;
    private TextView tvThumbAddCount;
    private CommentBean commentBean;

    private Animation mAnimation;
    private ObserverData observerData;

    public ThumbView(Context context) {
        this(context, null);
    }

    public ThumbView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
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

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isThumb) {
                    requestClickThumb();
                } else {
                    TSnackbar tSnackbar = TSnackbar.make(v, "已经赞过了喔", Snackbar.LENGTH_LONG, TSnackbar
                            .APPEAR_FROM_TOP_TO_DOWN)
                            .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height));

                    int color = ContextCompat.getColor(getContext(), R.color.red_btn_bg_color);
                    tSnackbar.setBackgroundColor(color);
                    tSnackbar.setPromptThemBackground(Prompt.WARNING);
                    tSnackbar.show();
                }
            }
        });
    }

    /**
     * 请求点赞
     */
    private void requestClickThumb() {

        String type = "";
        switch (commentBean.getType()) {
            case "2":
            case VarConstant.VIDEO:
                type = VarConstant.GOOD_TYPE_COMMENT_VIDEO;
                break;
            case "1":
            case VarConstant.ARTICLE:
                type = VarConstant.GOOD_TYPE_COMMENT_NEWS;
                break;
            case "3":
            case VarConstant.BLOG_ARTICLE:
                type = VarConstant.GOOD_TYPE_COMMENT_BLOG;
                break;
        }
        NativeStore.addThumbID(getContext(), type, thumbId + "", observerData, new ObserverData() {
            @Override
            public void callback(Object o) {
                changerCount(1);
                ivThumb.setImageResource(R.mipmap.icon_comment_like);

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
            public void onError(Exception e) {

            }
        });

//        IBaseView iBaseView = (IBaseView) getContext();
//        VolleyRequest volleyRequest = new VolleyRequest(getContext(), iBaseView.getQueue());
//        JSONObject jsonParam = volleyRequest.getJsonParam();
//        jsonParam.put("id", thumbId);
//        jsonParam.put("uid", userInfo.getUid());
//        jsonParam.put("token", userInfo.getToken());
//
//        volleyRequest.doPost(HttpConstant.GOOD_NEWS, jsonParam, new HttpListener<String>() {
//            @Override
//            protected void onResponse(String data) {
//            }
//
//            @Override
//            protected void onErrorResponse(VolleyError error) {
//                super.onErrorResponse(error);
//            }
//        });

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (tvThumbAddCount != null && tvThumbAddCount.isShown()) {
            tvThumbAddCount.setVisibility(View.GONE);
        }
    }

    public void setThumbCount(CommentBean commentBean, int thumbId, ObserverData observerData) {
        this.thumbId = thumbId;
        this.commentBean = commentBean;
        this.observerData=observerData;
        int count = commentBean.getNum_good();

        String type = null;

        switch (commentBean.getType()) {
            case VarConstant.ARTICLE:
            case "1":
                type = VarConstant.GOOD_TYPE_COMMENT_NEWS;
                break;
            case VarConstant.VIDEO:
            case "2":
                type = VarConstant.GOOD_TYPE_COMMENT_VIDEO;
                break;
            case VarConstant.BLOG_ARTICLE:
            case "3":
                type = VarConstant.GOOD_TYPE_COMMENT_BLOG;
                break;
        }

        isThumb = NativeStore.isThumbSucceed(getContext(), type, thumbId + "");

        if (isThumb) {
            ivThumb.setImageResource(R.mipmap.icon_comment_like);
            count = count + (commentBean.isTemporaryClickFavour() ? 1 : 0);
        } else {
            ivThumb.setImageResource(R.mipmap.icon_comment_unlike);
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
}
