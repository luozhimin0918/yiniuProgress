package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.presenter.EmotionPresenter;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.jyh.kxt.chat.presenter.ChatRoomPresenter;
import com.jyh.kxt.index.presenter.PullListViewPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatRoomActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @BindView(R.id.fl_list_layout) public FrameLayout flListLayout;
    @BindView(R.id.ptrl_chart_room_list) public PullToRefreshListView ptrlChartRoomList;
    @BindView(R.id.publish_content_et) EmoticonsEditText publishContentEt;
    @BindView(R.id.iv_publish_emoji) ImageView ivPublishEmoji;

    @BindView(R.id.rl_keyboard_above) RelativeLayout rlKeyboardAboveLayout;
    @BindView(R.id.fl_emotion_layout) FrameLayout flContent;

    private EmotionPresenter emotionPresenter;
    private ChatRoomPresenter chatRoomPresenter;
    public PullListViewPresenter pullListViewPresenter;

    private LinearLayout.LayoutParams keyboardAboveParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room, StatusBarColor.THEME1);

        emotionPresenter = new EmotionPresenter(this);
        chatRoomPresenter = new ChatRoomPresenter(this);
        pullListViewPresenter = new PullListViewPresenter(this);

        keyboardAboveParams = (LinearLayout.LayoutParams) rlKeyboardAboveLayout.getLayoutParams();
        emotionPresenter.initEmotionView(this, flContent, publishContentEt, rlKeyboardAboveLayout, ivPublishEmoji);
        chatRoomPresenter.initPullListView();
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.iv_publish_emoji})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                break;
            case R.id.iv_publish_emoji:
                emotionPresenter.showOrHideEmoJiView();
                break;
        }
    }


    /**
     * 软键盘监听事件
     *
     * @param height
     */
    @Override
    public void keyBoardShow(int height) {
        keyboardAboveParams.setMargins(0, 0, 0, height);
        rlKeyboardAboveLayout.setLayoutParams(keyboardAboveParams);
    }

    @Override
    public void keyBoardHide(int height) {
        keyboardAboveParams.setMargins(0, 0, 0, 0);
        rlKeyboardAboveLayout.setLayoutParams(keyboardAboveParams);

        SoftKeyBoardListener.OnSoftKeyBoardChangeListener softKeyBoardChangeListener =
                emotionPresenter.getSoftKeyBoardChangeListener();

        if (softKeyBoardChangeListener != null) {
            softKeyBoardChangeListener.keyBoardHide(height);
        }
    }
}
