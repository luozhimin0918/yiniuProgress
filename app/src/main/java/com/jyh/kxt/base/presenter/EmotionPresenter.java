package com.jyh.kxt.base.presenter;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonLinearLayout;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.library.util.SystemUtil;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class EmotionPresenter extends BasePresenter {

    private boolean isShowEmoJiView = false;
    private Activity mActivity;

    /**
     * 视图部分
     */
    private ImageView ivEmoJeView;
    private FrameLayout flParentLayout;
    private EmoticonsEditText eetContentView;
    private ViewGroup vgKeyboardAboveLayout;
    private EmoticonLinearLayout ellEmoJeLayout;

    private SoftKeyBoardListener.OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

    public EmotionPresenter(IBaseView iBaseView) {
        super(iBaseView);
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
        }
    }

    public void showOrHideEmoJiView() {
        isShowEmoJiView = !isShowEmoJiView;

        if (isShowEmoJiView) {
            SystemUtil.closeSoftInputWindow(mActivity);

            ViewGroup.LayoutParams layoutParams = flParentLayout.getLayoutParams();
            layoutParams.height = 0;
            flParentLayout.setLayoutParams(layoutParams);


            flParentLayout.addView(ellEmoJeLayout);
            ivEmoJeView.setImageResource(R.mipmap.ico_keybor);
            int bottomMargin = 0;
            ViewGroup.LayoutParams keyBoardParams = vgKeyboardAboveLayout.getLayoutParams();
            if (keyBoardParams instanceof RelativeLayout.LayoutParams) {
                bottomMargin = ((RelativeLayout.LayoutParams) keyBoardParams).bottomMargin;
            } else if (keyBoardParams instanceof LinearLayout.LayoutParams) {
                bottomMargin = ((LinearLayout.LayoutParams) keyBoardParams).bottomMargin;
            } else if (keyBoardParams instanceof FrameLayout.LayoutParams) {
                bottomMargin = ((FrameLayout.LayoutParams) keyBoardParams).bottomMargin;
            }

            if (bottomMargin == 0) {
                showEmoJeContent();
            } else {
                onSoftKeyBoardChangeListener = new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
                    @Override
                    public void keyBoardShow(int height) {

                    }

                    @Override
                    public void keyBoardHide(int height) {
                        showEmoJeContent();
                    }
                };
            }
        } else {
            hideEmoJeContent();
            hideSoftInputFromWindow();
        }
    }

    private void hideEmoJeContent() {
        isShowEmoJiView = false;
        ivEmoJeView.setImageResource(R.mipmap.icon_emoje);
        if (ellEmoJeLayout != null) {
            flParentLayout.removeView(ellEmoJeLayout);
        }

        ViewGroup.LayoutParams layoutParams = flParentLayout.getLayoutParams();
        layoutParams.height = 0;
        flParentLayout.setLayoutParams(layoutParams);
    }

    private void showEmoJeContent() {
        int emoJeHeight = SystemUtil.dp2px(mContext, 215);

        ViewGroup.LayoutParams layoutParams = flParentLayout.getLayoutParams();
        layoutParams.height = emoJeHeight;
        flParentLayout.setLayoutParams(layoutParams);

        onSoftKeyBoardChangeListener = null;
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public SoftKeyBoardListener.OnSoftKeyBoardChangeListener getSoftKeyBoardChangeListener() {
        return onSoftKeyBoardChangeListener;
    }
}
