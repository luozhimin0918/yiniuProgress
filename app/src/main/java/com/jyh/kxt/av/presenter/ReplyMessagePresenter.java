package com.jyh.kxt.av.presenter;

import android.app.Service;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.dao.EmojeBean;
import com.jyh.kxt.base.presenter.CommentPresenter;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.util.emoje.EmoticonLinearLayout;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.jyh.kxt.R.id.iv_emoji;

/**
 * Created by Mr'Dai on 2017/4/5.
 */

public class ReplyMessagePresenter extends BasePresenter {

    @BindView(R.id.tv_publish) TextView tvPublish;
    @BindView(R.id.fl_emoje) public FrameLayout flEmoJe;
    @BindView(R.id.iv_emoji) ImageView ivEmoJeState;
    @BindView(R.id.tv_max_length) TextView tvMaxLength;

    @BindView(R.id.eet_content) EmoticonsEditText eetContent;

    public boolean isShowEmoJiView = false;
    private boolean isAdjustEmoJeView = true;

    private View replyMessageView;
    private EmoticonLinearLayout emoJeContentView;

    private PopupUtil replyMessagePopup;
    private CommentPresenter.OnCommentPublishListener onCommentPublishListener;
    private CommentBean commentBean;
    private int commentWho;

    public ReplyMessagePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initView(View replyMessageView,
                         PopupUtil replyMessagePopup,
                         CommentPresenter.OnCommentPublishListener onCommentPublishListener) {
        ButterKnife.bind(this, replyMessageView);
        this.replyMessageView = replyMessageView;
        this.replyMessagePopup = replyMessagePopup;
        this.onCommentPublishListener = onCommentPublishListener;

        this.eetContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isShowEmoJiView) {
                    isShowEmoJiView = false;
                    ivEmoJeState.setImageResource(R.mipmap.icon_emoje);
                    flEmoJe.removeView(emoJeContentView);
                    hideSoftInputFromWindow();

                    return false;
                }
                return false;
            }
        });

        eetContent.setOnTextChangedListener(new EmoticonsEditText.OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentCount = start + count;
                tvMaxLength.setText(currentCount + "/140");
            }
        });
    }

    @OnClick({iv_emoji, R.id.tv_publish})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case iv_emoji:
                showOrHideEmoJiView();
                break;
            case R.id.tv_publish:
                if (onCommentPublishListener != null) {
                    onCommentPublishListener.onPublish(replyMessagePopup, eetContent, commentBean, commentWho);
                }
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
                    eetContent.deleteEmoJeClick();
                }
            });
        }

        if (isShowEmoJiView) {
            flEmoJe.addView(emoJeContentView);
            ivEmoJeState.setImageResource(R.mipmap.ico_keybor);
        } else {
            ivEmoJeState.setImageResource(R.mipmap.icon_emoje);
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
            ivEmoJeState.setImageResource(R.mipmap.icon_emoje);
            isShowEmoJiView = false;
        }
    }


    public void adjustEmoJeView(int height) {
        if (flEmoJe != null && isAdjustEmoJeView) {
            isAdjustEmoJeView = false;
            int emoJeWidth = replyMessageView.getWidth();

            ViewGroup.LayoutParams layoutParams = flEmoJe.getLayoutParams();
            layoutParams.width = emoJeWidth;
            layoutParams.height = height;

            flEmoJe.setLayoutParams(layoutParams);
        }
    }


    public void setCommentBean(CommentBean commentBean) {
        this.commentBean = commentBean;
    }

    public void setCommentWho(int commentWho) {
        this.commentWho = commentWho;
    }

    public void onChangeTheme() {
        try {
            int color = ContextCompat.getColor(mContext, R.color.theme1);
            emoJeContentView.setBackgroundColor(color);

            int color1 = ContextCompat.getColor(mContext, R.color.line_color3);
            replyMessageView.setBackgroundColor(color1);

            int color2 = ContextCompat.getColor(mContext, R.color.white);
            eetContent.setBackgroundColor(color2);

            int color3 = ContextCompat.getColor(mContext, R.color.theme1);
            flEmoJe.setBackgroundColor(color3);

            emoJeContentView.onChangeTheme();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
