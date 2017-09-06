package com.jyh.kxt.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.dao.ChatRoomJsonDao;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.adapter.LetterListAdapter;
import com.jyh.kxt.chat.json.ChatPreviewJson;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.chat.json.LetterJson;
import com.jyh.kxt.chat.json.LetterListJson;
import com.jyh.kxt.chat.presenter.LetterPresenter;
import com.jyh.kxt.chat.util.ChatSocketUtil;
import com.jyh.kxt.chat.util.OnChatMessage;
import com.jyh.kxt.user.json.UserJson;
import com.library.bean.EventBusClass;
import com.library.manager.ActivityManager;
import com.library.util.SPUtils;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:私信列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener, OnChatMessage {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    /**
     * 未读消息,通过这个数量来控制主页面是否有红点产生
     */
    public int unreadMessageCount = 0;

    private LetterJson letterJson;

    public LetterPresenter presenter;
    private LetterListAdapter adapter;

    private Set<String> letterLastIdSet = new HashSet<>();
    private HashMap<String, LetterListJson> letterReceiverSet = new HashMap<>();

    /**
     * 回调消息
     *
     * @param chatRoomJson
     */
    @Override
    public void onChatMessage(ChatRoomJson chatRoomJson) {
        String sender = chatRoomJson.getSender();

        Activity stackPeekActivity = ActivityManager.getInstance().getStackPeekActivity();
        if (stackPeekActivity == this) {

            LetterListJson letterListJson = letterReceiverSet.get(sender);
            String currentTime = (System.currentTimeMillis() / 1000) + "";
            if (letterListJson != null) {
                int contentType = letterListJson.getContentType();
                if (contentType != 2) {
                    letterListJson.setLast_content(chatRoomJson.getContent());

                    String numUnreadStr = letterListJson.getNum_unread() == null ? "0" : letterListJson.getNum_unread();
                    int numUnread = Integer.parseInt(numUnreadStr) + 1;

                    letterListJson.setNum_unread(numUnread + "");

                    letterListJson.setDatetime(currentTime);
                }
            } else {
                letterListJson = new LetterListJson();
                letterListJson.setDatetime(currentTime);
                letterListJson.setLast_content(chatRoomJson.getContent());
                letterListJson.setNum_unread("1");
                letterListJson.setReceiver(chatRoomJson.getReceiver());
                letterListJson.setAvatar(chatRoomJson.getAvatar());
                letterListJson.setLast_id(chatRoomJson.getId());
                letterListJson.setNickname(chatRoomJson.getNickname());

                adapter.dataList.add(0, letterListJson);
                letterReceiverSet.put(letterListJson.getReceiver(), letterListJson);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter, StatusBarColor.THEME1);
        presenter = new LetterPresenter(this);
        initView();
        plRootView.loadWait();
        presenter.init();
        EventBus.getDefault().register(this);


        ChatSocketUtil.getInstance().sendSocketParams(this, "", this);
    }

    private void initView() {

        tvBarTitle.setText("我的私信");
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_msg_ban));

        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plContent.setOnItemClickListener(this);
        plContent.setOnRefreshListener(this);
        plRootView.setOnAfreshLoadListener(this);

    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                startActivity(new Intent(this, BlockActivity.class));
                break;
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position - 1;
        if (index == 0) {
            //系统消息是否存在红点
            if ("1".equals(letterJson.getShow_red_dot())) {
                adapter.setShowRed(false);
                adapter.notifyDataSetChanged();
                unreadMessageCount -= 1;
            }
            Intent intent = new Intent(this, SystemLetterActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, ChatRoomActivity.class);
            LetterListJson letterListJson = adapter.dataList.get(index - 1);
            intent.putExtra(IntentConstant.U_ID, letterListJson.getReceiver());
            intent.putExtra(IntentConstant.NAME, letterListJson.getNickname());
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }


    public void init(LetterJson letterJson) {
        if (letterJson == null) {
            plRootView.loadEmptyData();
            return;
        }

        this.letterJson = letterJson;

        List<LetterListJson> list = letterJson.getList();
        analysisListData(list);

        adapter = new LetterListAdapter(list, this, plContent.getRefreshableView());
        presenter.scrollListener(plContent, adapter);
        //是否存在红点
        String show_red_dot = letterJson.getShow_red_dot();
        boolean isShowRedDot = show_red_dot != null && "1".equals(show_red_dot);
        adapter.setShowRed(isShowRedDot);
        plContent.setAdapter(adapter);

        if (isShowRedDot) {
            unreadMessageCount += 1;
        }

        plRootView.loadOver();
    }

    public void refresh(LetterJson letterJson) {
        if (letterJson == null) {
            return;
        }

        this.letterJson = letterJson;

        List<LetterListJson> list = letterJson.getList();
        if (list == null || list.size() == 0) {
            return;
        }
        adapter.setData(list);

        analysisListData(list);
        //是否存在红点
        String show_red_dot = letterJson.getShow_red_dot();
        boolean isShowRedDot = show_red_dot != null && "1".equals(show_red_dot);
        if (isShowRedDot) {
            unreadMessageCount += 1;
        }
        adapter.setShowRed(isShowRedDot);

        adapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onEventUpdate(EventBusClass eventBusClass) {
        if (eventBusClass.fromCode == EventBusClass.EVENT_DRAFT) {
            presenter.refresh();
        }
    }


    private void analysisListData(List<LetterListJson> letterList) {
        try {
            /**
             * 默认网络数据       <接收列表中的最后一条消息ID>
             */
            letterLastIdSet.clear();
            letterReceiverSet.clear();
            unreadMessageCount = 0;//刷新重置

            for (LetterListJson letterListJson : letterList) {
                letterLastIdSet.add(letterListJson.getLast_id());
                letterReceiverSet.put(letterListJson.getReceiver(), letterListJson);

                letterListJson.setContentType(0);


                String numUnread = letterListJson.getNum_unread();
                if (numUnread != null) {
                    unreadMessageCount += Integer.parseInt(numUnread);
                }
            }

            /**
             * 本地发送失败的数据    <接收列表中的接收人ID>
             */
            HashMap<String, ChatPreviewJson> previewMap = new HashMap<>();

            DBManager mDBManager = DBManager.getInstance(this);
            ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionWrit().getChatRoomJsonDao();
            String groupSql = "SELECT FOREGOING_CHAT_ID,CONTENT,RECEIVER FROM CHAT_ROOM_BEAN GROUP BY RECEIVER";
            Cursor cursor = chatRoomJsonDao.getDatabase().rawQuery(groupSql, null);
            while (cursor.moveToNext()) {
                String foregoing_chat_id = cursor.getString(cursor.getColumnIndex("FOREGOING_CHAT_ID"));
                String content = cursor.getString(cursor.getColumnIndex("CONTENT"));
                String receiver = cursor.getString(cursor.getColumnIndex("RECEIVER"));

                LetterListJson containReceiver = letterReceiverSet.get(receiver);
                if (containReceiver != null) {
                    boolean containsChatId = letterLastIdSet.contains(foregoing_chat_id);
                    if (containsChatId) {
                        ChatPreviewJson chatPreviewJson = new ChatPreviewJson();
                        chatPreviewJson.setType(1);
                        chatPreviewJson.setReceiver(receiver);
                        chatPreviewJson.setContent(content);
                        previewMap.put(receiver, chatPreviewJson);
                    }
                }
            }

            String chatPreviewDraft = SPUtils.getString(this, SpConstant.CHAT_PREVIEW);
            if (!TextUtils.isEmpty(chatPreviewDraft)) {
                List<ChatPreviewJson> chatDraftList = JSONArray.parseArray(chatPreviewDraft, ChatPreviewJson.class);
                if (chatDraftList != null) {
                    for (ChatPreviewJson chatPreviewJson : chatDraftList) {
                        String receiver = chatPreviewJson.getReceiver();
                        LetterListJson containReceiver = letterReceiverSet.get(receiver);
                        if (containReceiver != null) {
                            previewMap.put(receiver, chatPreviewJson);
                        }
                    }
                }
            }


            for (LetterListJson letterListJson : letterList) {
                String receiver = letterListJson.getReceiver();
                ChatPreviewJson chatPreviewJson = previewMap.get(receiver);
                if (chatPreviewJson != null) {
                    letterListJson.setLocal_content(chatPreviewJson.getContent());
                    letterListJson.setContentType(chatPreviewJson.getType());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_msg_ban));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e(TAG, "unreadMessageCount: "+unreadMessageCount );

        UserJson userInfo = LoginUtils.getUserInfo(this);
        if (userInfo != null) {
            userInfo.setIs_unread_msg(unreadMessageCount == 0 ? 0 : 1);
            EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN, userInfo));
        }

        EventBus.getDefault().unregister(this);
        getQueue().cancelAll(LetterPresenter.class.getName());

        ChatSocketUtil.getInstance().unOnChatMessage(this);
    }

    public void deleteMessage(LetterListJson bean) {
    }
}
