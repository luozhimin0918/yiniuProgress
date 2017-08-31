package com.jyh.kxt.chat.presenter;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.dao.ChatRoomJsonDao;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.ChatRoomActivity;
import com.jyh.kxt.chat.adapter.ChatRoomAdapter;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import static android.widget.AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomPresenter extends BasePresenter {
    @BindObject ChatRoomActivity chatRoomActivity;

    private UserJson userInfo;
    private List<ChatRoomJson> baseChatRoomList;
    private ChatRoomAdapter chatRoomAdapter;

    private ChatRoomJson fakeChatRoom;

    private boolean isInitialLoadHistory = true;

    public ChatRoomPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initPullListView() {
        userInfo = LoginUtils.getUserInfo(mContext);

        /*
         * 初始化ListView
         */
        baseChatRoomList = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(mContext, baseChatRoomList);

        PullToRefreshListView pullContentView = chatRoomActivity.pullContentView;
        pullContentView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        pullContentView.setDividerNull();
        pullContentView.getRefreshableView().setTranscriptMode(TRANSCRIPT_MODE_ALWAYS_SCROLL);
        pullContentView.setAdapter(chatRoomAdapter);

        pullContentView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                requestPullMoreData();
            }
        });

        /*
         * 初始化EditText
         */
        chatRoomActivity.publishContentEt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        chatRoomActivity.publishContentEt.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        chatRoomActivity.publishContentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEND:
                        requestSendChitChat();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        /**
         * 默认加载更多数据
         */
        chatRoomActivity.tvRoomReminder.setVisibility(View.VISIBLE);
        chatRoomActivity.tvRoomReminder.setText("正在获取历史数据...");

        requestPullMoreData();
    }

    /**
     * 请求加载更多数据
     */
    private void requestPullMoreData() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("sender", userInfo.getUid());
        jsonParam.put("receiver", chatRoomActivity.otherUid);
        if (baseChatRoomList.size() != 0) {
            jsonParam.put("last_id", "");
        }
        volleyRequest.doPost(HttpConstant.MESSAGE_HISTORY, jsonParam, new HttpListener<List<ChatRoomJson>>() {
            @Override
            protected void onResponse(List<ChatRoomJson> chatRoomList) {
                chatRoomActivity.pullContentView.onRefreshComplete();

                analyzeListData(chatRoomList);
                baseChatRoomList.addAll(chatRoomList);

                if (isInitialLoadHistory) {
                    chatRoomActivity.tvRoomReminder.setVisibility(View.GONE);
                    isInitialLoadHistory = false;
                    //判断是否屏蔽过?

                }
            }
        });
    }

    /**
     * 发送内容的 网络请求
     */
    private void requestSendChitChat() {
        String chatContent = chatRoomActivity.publishContentEt.getText().toString();
        if (TextUtils.isEmpty(chatContent)) {
            return;
        }

        boolean isConnectedNetWork = SystemUtil.isConnected(mContext);
        if (!isConnectedNetWork) {
            showPromptContent("网络好像出现了故障,发送失败");
            return;
        }

        analyzeFakeData(chatContent);

//        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
//        JSONObject jsonParam = volleyRequest.getJsonParam();
//        jsonParam.put("sender", userInfo.getUid());
//        jsonParam.put("receiver", chatRoomActivity.otherUid);
//        jsonParam.put("content", chatContent);
//        volleyRequest.doPost(HttpConstant.MESSAGE_SEND_MSG, jsonParam, new HttpListener<String>() {
//            @Override
//            protected void onResponse(String sendResponse) {
//
//                Log.e("to", "onResponse: " + sendResponse);
//            }
//        });
    }

    /**
     * 组织假数据
     */
    private void analyzeFakeData(String chatContent) {
        String sendDateTime = String.valueOf(System.currentTimeMillis() / 1000);
        fakeChatRoom = new ChatRoomJson();
        fakeChatRoom.setId("");
        fakeChatRoom.setViewType(1);
        fakeChatRoom.setMsgSendStatus(1);
        fakeChatRoom.setContent(chatContent);
        fakeChatRoom.setDatetime(sendDateTime);
        fakeChatRoom.setSender(userInfo.getUid());
        fakeChatRoom.setReceiver(chatRoomActivity.otherUid);
        fakeChatRoom.setAvatar(userInfo.getPicture());

        //保存假数据到数据库
        DBManager mDBManager = DBManager.getInstance(mContext);
        ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionWrit().getChatRoomJsonDao();
        chatRoomJsonDao.insert(fakeChatRoom);

        baseChatRoomList.add(fakeChatRoom);
        chatRoomAdapter.notifyDataSetChanged();
    }

    /**
     * 分析列表中的数据
     *
     * @param chatRoomList
     */
    private void analyzeListData(List<ChatRoomJson> chatRoomList) {
        long lastSendTime = 0;

        for (ChatRoomJson chatRoomJson : chatRoomList) {
            String senderUid = chatRoomJson.getSender();
            if (!userInfo.getUid().equals(senderUid)) {//是我发送的
                chatRoomJson.setViewType(1);
            } else {
                chatRoomJson.setViewType(0);
            }

            //计算间隔事件
            long thisSendTime = Long.parseLong(chatRoomJson.getDatetime());

            if (lastSendTime - 1000 * 60 * 5 <= thisSendTime) {
                lastSendTime = thisSendTime;
                chatRoomJson.setPartitionTime(thisSendTime);
            }

            if (lastSendTime == 0) {
                lastSendTime = thisSendTime;
            }
        }
    }


    /**
     * 显示提示内容
     *
     * @param reminder
     */
    private void showPromptContent(String reminder) {
        int statusBarHeight = SystemUtil.getStatuBarHeight(mContext);
        int actionBarHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.actionbar_height);

        TSnackbar.make(chatRoomActivity.flListLayout,
                reminder,
                TSnackbar.LENGTH_SHORT,
                TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                .setPromptThemBackground(Prompt.WARNING)
                .setMinHeight(statusBarHeight, actionBarHeight)
                .show();
    }
}
