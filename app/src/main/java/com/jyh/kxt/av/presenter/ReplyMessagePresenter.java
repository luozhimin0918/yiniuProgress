package com.jyh.kxt.av.presenter;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonsUtils;
import com.jyh.kxt.base.util.emoje.bean.EmoticonBean;
import com.jyh.kxt.base.util.emoje.utils.EmoticonsKeyboardBuilder;
import com.jyh.kxt.base.util.emoje.utils.Utils;
import com.jyh.kxt.base.util.emoje.view.EmoticonsEditText;
import com.jyh.kxt.base.util.emoje.view.EmoticonsIndicatorView;
import com.jyh.kxt.base.util.emoje.view.EmoticonsPageView;
import com.jyh.kxt.base.util.emoje.view.EmoticonsToolBarView;
import com.jyh.kxt.base.util.emoje.view.I.IView;

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
    private View emoJeContentView;

    private EmoticonsPageView mEmoticonsPageView;
    private EmoticonsIndicatorView mEmoticonsIndicatorView;
    private EmoticonsToolBarView mEmoticonsToolBarView;

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

                    emoJeContentView.setVisibility(View.GONE);
                    hideSoftInputFromWindow();

                    return true;
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
            emoJeContentView = inflater.inflate(R.layout.view_emoje_content, null);

            addViewListener(emoJeContentView);

            flEmoJe.addView(emoJeContentView, emoJeParams);
        }

        hideSoftInputFromWindow();

        if (isShowEmoJiView) {
            emoJeContentView.setVisibility(View.VISIBLE);
        } else {
            emoJeContentView.setVisibility(View.GONE);
        }
    }

    private void hideSoftInputFromWindow() {
        videoDetailActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void addViewListener(View view) {
        mEmoticonsPageView = (EmoticonsPageView) view.findViewById(R.id.view_epv);
        mEmoticonsIndicatorView = (EmoticonsIndicatorView) view.findViewById(R.id.view_eiv);
        mEmoticonsToolBarView = (EmoticonsToolBarView) view.findViewById(R.id.view_etv);

        setBuilder(EmoticonsUtils.getSimpleBuilder(mContext));

        mEmoticonsPageView.setOnIndicatorListener(new EmoticonsPageView.OnEmoticonsPageViewListener() {
            @Override
            public void emoticonsPageViewInitFinish(int count) {
                mEmoticonsIndicatorView.init(count);
            }

            @Override
            public void emoticonsPageViewCountChanged(int count) {
                mEmoticonsIndicatorView.setIndicatorCount(count);
            }

            @Override
            public void playTo(int position) {
                mEmoticonsIndicatorView.playTo(position);
            }

            @Override
            public void playBy(int oldPosition, int newPosition) {
                mEmoticonsIndicatorView.playBy(oldPosition, newPosition);
            }
        });

        mEmoticonsPageView.setIViewListener(new IView() {
            @Override
            public void onItemClick(EmoticonBean bean) {
                if (eetContent != null) {
                    eetContent.setFocusable(true);
                    eetContent.setFocusableInTouchMode(true);
                    eetContent.requestFocus();

                    // 删除
                    if (bean.getEventType() == EmoticonBean.FACE_TYPE_DEL) {
                        int action = KeyEvent.ACTION_DOWN;
                        int code = KeyEvent.KEYCODE_DEL;
                        KeyEvent event = new KeyEvent(action, code);
                        eetContent.onKeyDown(KeyEvent.KEYCODE_DEL, event);
                        return;
                    }
                    // 用户自定义
                    else if (bean.getEventType() == EmoticonBean.FACE_TYPE_USERDEF) {
                        return;
                    }

                    int index = eetContent.getSelectionStart();
                    Editable editable = eetContent.getEditableText();
                    if (index < 0) {
                        editable.append(bean.getContent());
                    } else {
                        editable.insert(index, bean.getContent());
                    }
                }
            }

            @Override
            public void onItemDisplay(EmoticonBean bean) {
            }

            @Override
            public void onPageChangeTo(int position) {
                mEmoticonsToolBarView.setToolBtnSelect(position);
            }
        });

        mEmoticonsToolBarView.setOnToolBarItemClickListener(new EmoticonsToolBarView.OnToolBarItemClickListener() {
            @Override
            public void onToolBarItemClick(int position) {
                mEmoticonsPageView.setPageSelect(position);
            }
        });
    }

    public void setBuilder(EmoticonsKeyboardBuilder builder) {
        if (mEmoticonsPageView != null) {
            mEmoticonsPageView.setBuilder(builder);
        }
        if (mEmoticonsToolBarView != null) {
            mEmoticonsToolBarView.setBuilder(builder);
        }
    }

    public void goneEmoJeView() {
        if (emoJeContentView != null) {
            emoJeContentView.setVisibility(View.GONE);
            isShowEmoJiView = false;
        }
    }

    private int lastKeyboardHeight = 0;

    public void adjustEmoJeView(int height) {
        if (flEmoJe != null && height != lastKeyboardHeight) {
            int emoJeWidth = replyMessageView.getWidth();
            lastKeyboardHeight = Utils.getDefKeyboardHeight(mContext);

            ViewGroup.LayoutParams layoutParams = flEmoJe.getLayoutParams();
            layoutParams.width = emoJeWidth;
            layoutParams.height = lastKeyboardHeight;

            flEmoJe.setLayoutParams(layoutParams);
        }

    }
}
