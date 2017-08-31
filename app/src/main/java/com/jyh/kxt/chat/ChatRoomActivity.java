package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.presenter.EmotionPresenter;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.jyh.kxt.chat.presenter.ChatRoomPresenter;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatRoomActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;

    @BindView(R.id.chat_room_reminder) public TextView tvRoomReminder;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;

    @BindView(R.id.fl_list_layout) public FrameLayout flListLayout;
    @BindView(R.id.ptrl_chat_room_list) public PullToRefreshListView pullContentView;

    @BindView(R.id.publish_content_et) public EmoticonsEditText publishContentEt;
    @BindView(R.id.iv_publish_emoji) ImageView ivPublishEmoji;

    @BindView(R.id.rl_keyboard_above) RelativeLayout rlKeyboardAboveLayout;
    @BindView(R.id.fl_emotion_layout) FrameLayout flContent;

    public String otherUid;
    public String otherName;

    private EmotionPresenter emotionPresenter;
    private ChatRoomPresenter chatRoomPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room, StatusBarColor.THEME1);

        otherUid = /*getIntent().getStringExtra(IntentConstant.U_ID)*/ "58703";
        otherName = getIntent().getStringExtra(IntentConstant.NAME);

        emotionPresenter = new EmotionPresenter(this);
        chatRoomPresenter = new ChatRoomPresenter(this);

        emotionPresenter.initEmotionView(this, flContent, publishContentEt, rlKeyboardAboveLayout, ivPublishEmoji);
        chatRoomPresenter.initPullListView();

        SoftKeyBoardListener.setListener(this, this);

        tvBarTitle.setText(otherName);
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
                emotionPresenter.clickEmoJeView();
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
        if (emotionPresenter != null) {
            emotionPresenter.keyBoardShow(height);
        }
    }

    @Override
    public void keyBoardHide(int height) {
        if (emotionPresenter != null) {
            emotionPresenter.keyBoardHide(height);
        }
    }
}
