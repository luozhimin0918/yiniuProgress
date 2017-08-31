package com.jyh.kxt.base.presenter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.emoje.EmoticonLinearLayout;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class EmotionPresenter extends BasePresenter {

    private Activity mActivity;
    /**
     * 空区的显示状态  0 不显示  1 软键盘 2 EmoJe表情
     */
    private int mGapDisplayState = 0;

    /**
     * 视图部分
     */
    private ImageView ivEmoJeView;
    private FrameLayout flParentLayout;
    private EmoticonsEditText eetContentView;
    private ViewGroup vgKeyboardAboveLayout;
    private EmoticonLinearLayout ellEmoJeLayout;

    /**
     * 空白区域的高度
     */
    private int mEmoJeHeight;
    private int mKeyBoardHeight;

    public EmotionPresenter(IBaseView iBaseView) {
        super(iBaseView);
        mEmoJeHeight = SystemUtil.dp2px(mContext, 215);
    }

    public void initEmotionView(Activity mActivity,
                                FrameLayout flParentLayout,
                                EmoticonsEditText eetContentView,
                                ViewGroup vgKeyboardAboveLayout,
                                ImageView ivEmoJeView) {

        this.mActivity = mActivity;
        this.eetContentView = eetContentView;
        this.ivEmoJeView = ivEmoJeView;
        this.flParentLayout = flParentLayout;
        this.vgKeyboardAboveLayout = vgKeyboardAboveLayout;

        if (ellEmoJeLayout == null) {

            ViewGroup.LayoutParams emoJeParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ellEmoJeLayout = (EmoticonLinearLayout) inflater.inflate(R.layout.view_emoje_content, flParentLayout, false);
            ellEmoJeLayout.setLayoutParams(emoJeParams);
            ellEmoJeLayout.setOnlyAllowSmallEmoJe(true);
            ellEmoJeLayout.initData();

            ellEmoJeLayout.setOnItemClick(new EmoticonLinearLayout.OnItemClick() {
                @Override
                public void itemEmoJeClick(EmojeBean emojeBean) {
                    EmotionPresenter.this.eetContentView.itemEmoJeClick(emojeBean);
                }

                @Override
                public void deleteEmoJeClick() {
                    EmotionPresenter.this.eetContentView.deleteEmoJeClick();
                }
            });

            eetContentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (mGapDisplayState == 2 || mGapDisplayState == 0) {
                        mGapDisplayState = 1;

                        EmotionPresenter.this.ivEmoJeView.setImageResource(R.mipmap.icon_emoje);
                        if (EmotionPresenter.this.ellEmoJeLayout != null) {
                            EmotionPresenter.this.flParentLayout.removeView(ellEmoJeLayout);
                        }
                        resetGapDisplayHeight();

                        hideSoftInputFromWindow();
                        return false;
                    }
                    return false;
                }
            });

        }
    }

    /**
     * 来回切换键盘和EmoJe
     */
    public void clickEmoJeView() {
        if (mGapDisplayState == 0 || mGapDisplayState == 1) {//隐藏键盘
            if (mGapDisplayState == 1) {
                hideSoftInputFromWindow();
            }
            mGapDisplayState = 2;

            resetGapDisplayHeight();

            ivEmoJeView.setImageResource(R.mipmap.ico_keybor);
            flParentLayout.addView(ellEmoJeLayout);

        } else if (mGapDisplayState == 2) {//弹出键盘
            mGapDisplayState = 1;

            hideSoftInputFromWindow();

            resetGapDisplayHeight();

            ivEmoJeView.setImageResource(R.mipmap.icon_emoje);
            if (ellEmoJeLayout != null) {
                flParentLayout.removeView(ellEmoJeLayout);
            }
        }
    }

    private void resetGapDisplayHeight() {
        if (mGapDisplayState == 0) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = flParentLayout.getLayoutParams();
        layoutParams.height = mGapDisplayState == 1 ? mKeyBoardHeight : mEmoJeHeight;
        flParentLayout.setLayoutParams(layoutParams);
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void keyBoardShow(int height) {
        if (this.mKeyBoardHeight == 0) {
            this.mKeyBoardHeight = height;
            resetGapDisplayHeight();
        }
        Log.e("keyBoardShow", "keyBoardShow: " + height);
    }

    public void keyBoardHide(int height) {
        if (mGapDisplayState == 1) {//只有键盘弹出时则
            mGapDisplayState = 0;

            ViewGroup.LayoutParams layoutParams = flParentLayout.getLayoutParams();
            layoutParams.height = 0;
            flParentLayout.setLayoutParams(layoutParams);
        }
        Log.e("keyBoardHide", "keyBoardHide: " + height);
    }
}
