package com.jyh.kxt.av.presenter;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonLinearLayout;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Mr'Dai on 2017/4/5.
 */

public class ReplyMessagePresenter extends BasePresenter {

    @BindObject VideoDetailActivity videoDetailActivity;

    @BindView(R.id.tv_publish) TextView tvPublish;
    @BindView(R.id.fl_emoje) FrameLayout flEmoJe;
    @BindView(R.id.eet_content) EmoticonsEditText eetContent;

    public boolean isShowEmoJiView = false;

    private View replyMessageView;
    private EmoticonLinearLayout emoJeContentView;

    private PopupUtil replyMessagePopup;

    public ReplyMessagePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initView(View replyMessageView, PopupUtil replyMessagePopup) {
        ButterKnife.bind(this, replyMessageView);
        this.replyMessageView = replyMessageView;
        this.replyMessagePopup = replyMessagePopup;
        this.eetContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isShowEmoJiView) {
                    isShowEmoJiView = false;

                    flEmoJe.removeView(emoJeContentView);
                    hideSoftInputFromWindow();

                    return false;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.iv_emoji, R.id.tv_publish})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_emoji:
                showOrHideEmoJiView();
                break;
            case R.id.tv_publish:
                replyMessagePopup.dismiss();
                break;
        }
    }


    private void showOrHideEmoJiView() {
        isShowEmoJiView = !isShowEmoJiView;

        if (emoJeContentView == null) {

            ViewGroup.LayoutParams emoJeParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            emoJeContentView = (EmoticonLinearLayout) inflater.inflate(R.layout.view_emoje_content, null);
            emoJeContentView.setLayoutParams(emoJeParams);


            emoJeContentView.setOnItemClick(new EmoticonLinearLayout.OnItemClick() {
                @Override
                public void itemEmoJeClick(EmojeBean emojeBean) {
                    eetContent.itemEmoJeClick(emojeBean);
                }

                @Override
                public void deleteEmoJeClick() {
                    eetContent.deleteEmoJeClick( );
                }
            });
        }

        if (isShowEmoJiView) {
            flEmoJe.addView(emoJeContentView);
        } else {
            flEmoJe.removeView(emoJeContentView);
        }
        hideSoftInputFromWindow();
    }

    private void hideSoftInputFromWindow() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public void goneEmoJeView() {
        if (emoJeContentView != null) {
            flEmoJe.removeView(emoJeContentView);
            isShowEmoJiView = false;
        }
    }


    public void adjustEmoJeView(int height) {
        if (flEmoJe != null) {
            int emoJeWidth = replyMessageView.getWidth();

            ViewGroup.LayoutParams layoutParams = flEmoJe.getLayoutParams();
            layoutParams.width = emoJeWidth;
            layoutParams.height = height;

            flEmoJe.setLayoutParams(layoutParams);
        }

    }
}
