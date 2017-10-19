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
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.utils.NativeStore;
import com.jyh.kxt.trading.util.TradeHandlerUtil;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/5/4.
 * 同意管理点赞动画
 */
public class ThumbView3 extends RelativeLayout {

    public ThumbView3(Context context) {
        this(context, null);
    }

    public ThumbView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThumbView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
    }

    /**
     * 请求点赞
     */
    private void requestClickThumb() {

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
