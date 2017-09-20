package com.jyh.kxt.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.presenter.EmotionPresenter;
import com.jyh.kxt.base.util.SoftKeyBoardListener;
import com.jyh.kxt.base.util.emoje.EmoticonsEditText;
import com.jyh.kxt.chat.json.ChatPreviewJson;
import com.jyh.kxt.chat.presenter.ChatRoomPresenter;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatRoomActivity extends BaseActivity implements SoftKeyBoardListener.OnSoftKeyBoardChangeListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;

    @BindView(R.id.chat_room_reminder) public TextView tvRoomReminder;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;

    @BindView(R.id.fl_list_layout) public LinearLayout flListLayout;
    @BindView(R.id.ptrl_chat_room_list) public ListView lvContentList;

    @BindView(R.id.publish_content_et) public EmoticonsEditText publishContentEt;
    @BindView(R.id.iv_publish_emoji) ImageView ivPublishEmoji;
    @BindView(R.id.iv_publish_send)  public ImageView ivPublishSend;

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

        otherUid = getIntent().getStringExtra(IntentConstant.U_ID);
        otherName = getIntent().getStringExtra(IntentConstant.NAME);

        emotionPresenter = new EmotionPresenter(this);
        chatRoomPresenter = new ChatRoomPresenter(this);

        emotionPresenter.initEmotionView(this, flContent, publishContentEt, rlKeyboardAboveLayout, ivPublishEmoji);
        chatRoomPresenter.initPullListView();

        SoftKeyBoardListener.setListener(this, this);

        tvBarTitle.setText(otherName);
        ivBarFunction.setImageResource(R.mipmap.icon_msg_usercenter);

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEventUpdate(EventBusClass eventBusClass) {
        if (eventBusClass.fromCode == EventBusClass.EVENT_MSG_BAN) {
            boolean isShield = (boolean) eventBusClass.intentObj;
            if (isShield) {
                chatRoomPresenter.isBannedForReceiver = true;
                tvRoomReminder.setVisibility(View.VISIBLE);
                tvRoomReminder.setText("回复消息将自动解除屏蔽");
            } else {
                chatRoomPresenter.isBannedForReceiver = false;
                tvRoomReminder.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.iv_publish_emoji, R.id.iv_publish_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                Intent intent = new Intent(this, UserSettingActivity.class);
                intent.putExtra(IntentConstant.U_ID, otherUid);
                startActivity(intent);
                break;
            case R.id.iv_publish_emoji:
                emotionPresenter.clickEmoJeView();
                break;
            case R.id.iv_publish_send:
                chatRoomPresenter.prepareSendInfo();
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

    @Override
    protected void onDestroy() {
        //是否有数据保存草稿
        String sendContent = publishContentEt.getText().toString();

        String chatPreviewDraft = SPUtils.getString(this, SpConstant.CHAT_PREVIEW);
        List<ChatPreviewJson> chatDraftList;
        if (!TextUtils.isEmpty(chatPreviewDraft)) {
            chatDraftList = JSONArray.parseArray(chatPreviewDraft, ChatPreviewJson.class);
        } else {
            chatDraftList = new ArrayList<>();
        }

        ChatPreviewJson chatPreviewResetJson = null;
        for (ChatPreviewJson chatPreviewJson : chatDraftList) {
            if (otherUid.equals(chatPreviewJson.getReceiver())) {
                chatPreviewResetJson = chatPreviewJson;
            }
        }

        if (chatPreviewResetJson != null) {
            if (!TextUtils.isEmpty(sendContent)) {
                chatPreviewResetJson.setContent(sendContent);
            } else {
                chatDraftList.remove(chatPreviewResetJson);
            }
        } else {
            if (!TextUtils.isEmpty(sendContent)) {
                chatPreviewResetJson = new ChatPreviewJson();
                chatPreviewResetJson.setType(2);
                chatPreviewResetJson.setContent(sendContent);
                chatPreviewResetJson.setReceiver(otherUid);
                chatDraftList.add(chatPreviewResetJson);
            }
        }
        SPUtils.save(this, SpConstant.CHAT_PREVIEW, JSON.toJSONString(chatDraftList));

        EventBusClass eventBusClass = new EventBusClass(EventBusClass.EVENT_DRAFT, null);
        EventBus.getDefault().post(eventBusClass);
        //发送清空阅读网络请求
        if (chatRoomPresenter != null) {
            chatRoomPresenter.requestClearUnread();
            chatRoomPresenter.onDestroy();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
