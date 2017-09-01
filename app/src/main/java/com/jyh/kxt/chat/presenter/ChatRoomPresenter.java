package com.jyh.kxt.chat.presenter;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
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
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, ChatRoomJson> temporaryChatRoomMap;

    private boolean isBannedForReceiver = false;
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
        temporaryChatRoomMap = new HashMap<>();

        chatRoomAdapter = new ChatRoomAdapter(mContext, baseChatRoomList, this);

        ListView lvContentList = chatRoomActivity.lvContentList;
        lvContentList.setTranscriptMode(TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lvContentList.setAdapter(chatRoomAdapter);

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
                        String chatContent = chatRoomActivity.publishContentEt.getText().toString();
                        if (TextUtils.isEmpty(chatContent)) {
                            return true;
                        }
                        String randomUnique = String.valueOf(Math.random());

                        ChatRoomJson fakeChatRoom = analyzeFakeData(randomUnique, chatContent);
                        requestSendChitChat(fakeChatRoom, randomUnique);
                        break;
                    default:
                        break;
                }
                return true;
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
            ChatRoomJson lastChatRoomJson = baseChatRoomList.get(baseChatRoomList.size() - 1);
            jsonParam.put("last_id", lastChatRoomJson.getId());
        }
        volleyRequest.doPost(HttpConstant.MESSAGE_HISTORY, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String chatRoomString) {
                try {
                    JSONObject chatRoomObject = JSONObject.parseObject(chatRoomString);

                    String chatRoomJsonList = chatRoomObject.getString("list");
                    String bannedForReceiver = chatRoomObject.getString("is_banned_for_receiver");

                    isBannedForReceiver = "1".equals(bannedForReceiver);

                    if (chatRoomJsonList != null) {
                        List<ChatRoomJson> chatRoomList = JSONArray.parseArray(chatRoomJsonList, ChatRoomJson.class);

                        analyzeListData(chatRoomList);
                        baseChatRoomList.addAll(chatRoomList);

                        if (isInitialLoadHistory) {
                            chatRoomActivity.tvRoomReminder.setVisibility(View.GONE);
                            isInitialLoadHistory = false;
                        }
                    }

                    if (isBannedForReceiver) {
                        chatRoomActivity.tvRoomReminder.setVisibility(View.VISIBLE);
                        chatRoomActivity.tvRoomReminder.setText("回复消息将自动解除屏蔽");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 发送内容的 网络请求
     */
    public void requestSendChitChat(ChatRoomJson fakeChatRoom, String randomUnique) {
        if (isBannedForReceiver) {
            chatRoomActivity.tvRoomReminder.setVisibility(View.GONE);
            chatRoomActivity.tvRoomReminder.setText("回复消息将自动解除屏蔽");
        }

        chatRoomActivity.publishContentEt.setText("");

        temporaryChatRoomMap.put(randomUnique, fakeChatRoom);

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        volleyRequest.setUniqueIdentification(randomUnique);

        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("sender", userInfo.getUid());
        jsonParam.put("receiver", chatRoomActivity.otherUid);
        jsonParam.put("content", fakeChatRoom.getContent());

        volleyRequest.doPost(HttpConstant.MESSAGE_SEND_MSG, jsonParam, new HttpListener<ChatRoomJson>() {
            /**
             * 数据库操作
             */
            DBManager mDBManager = DBManager.getInstance(mContext);
            Database database = mDBManager.getDaoSessionWrit().getDatabase();

            @Override
            protected void onResponse(ChatRoomJson mChatRoomJson) {
                String uniqueIdentification = getUniqueIdentification();
                ChatRoomJson temporaryChatRoom = temporaryChatRoomMap.get(uniqueIdentification);

                if (mChatRoomJson.getIs_banned_for_receiver() == 1) {
                    temporaryChatRoom.setIs_banned_for_receiver(1);
                    onErrorResponse(null);
                    return;
                }

                temporaryChatRoom.setMsgSendStatus(0);
                temporaryChatRoom.setId(mChatRoomJson.getId());

                if (baseChatRoomList.size() != 0) {
                    int temporaryPosition = baseChatRoomList.indexOf(temporaryChatRoom);
                    baseChatRoomList.remove(temporaryPosition);
                    baseChatRoomList.add(baseChatRoomList.size() - 1, temporaryChatRoom);
                }

                String updateSql = "UPDATE CHAT_ROOM_BEAN SET FOREGOING_CHAT_ID = " +
                        "(SELECT  FOREGOING_CHAT_ID FROM CHAT_ROOM_BEAN WHERE ID = '" + uniqueIdentification + "') " +
                        "WHERE FOREGOING_CHAT_ID = '" + uniqueIdentification + "'";
                database.execSQL(updateSql);

                String deleteSql = "DELETE FROM CHAT_ROOM_BEAN WHERE ID = '" + uniqueIdentification + "'";
                database.execSQL(deleteSql);

                //通知刷新
                chatRoomAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                String uniqueIdentification = getUniqueIdentification();
                /**
                 * 发送出现错误
                 */
                ChatRoomJson temporaryChatRoom = temporaryChatRoomMap.get(uniqueIdentification);
                temporaryChatRoom.setMsgSendStatus(2);

                String updateSql = "UPDATE CHAT_ROOM_BEAN SET  MSG_SEND_STATUS = 2 WHERE ID = '" + uniqueIdentification + "'";
                database.execSQL(updateSql);

                chatRoomAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 组织假数据
     */
    private ChatRoomJson analyzeFakeData(String randomUnique, String chatContent) {
        String sendDateTime = String.valueOf(System.currentTimeMillis() / 1000);

        String foregoingChatId;
        if (baseChatRoomList.size() != 0) {
            ChatRoomJson lastChatRoom = baseChatRoomList.get(baseChatRoomList.size() - 1);
            foregoingChatId = lastChatRoom.getId();
        } else {
            foregoingChatId = null;
        }

        ChatRoomJson fakeChatRoom = new ChatRoomJson();
        fakeChatRoom.setId(randomUnique);//设置默认假数据
        fakeChatRoom.setViewType(1);
        fakeChatRoom.setMsgSendStatus(1);
        fakeChatRoom.setContent(chatContent);
        fakeChatRoom.setDatetime(sendDateTime);
        fakeChatRoom.setSender(userInfo.getUid());
        fakeChatRoom.setReceiver(chatRoomActivity.otherUid);
        fakeChatRoom.setAvatar(userInfo.getPicture());
        fakeChatRoom.setForegoingChatId(foregoingChatId);

        //保存假数据到数据库
        DBManager mDBManager = DBManager.getInstance(mContext);
        ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionWrit().getChatRoomJsonDao();
        chatRoomJsonDao.insert(fakeChatRoom);

        baseChatRoomList.add(fakeChatRoom);
        chatRoomAdapter.notifyDataSetChanged();

        return fakeChatRoom;
    }

    /**
     * 分析列表中的数据
     *
     * @param chatRoomList
     */
    private void analyzeListData(List<ChatRoomJson> chatRoomList) {
        /**
         * 查询数据库中 所有错误的数据,进行还原
         */
        if (isInitialLoadHistory) {
            DBManager mDBManager = DBManager.getInstance(mContext);
            ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionRead().getChatRoomJsonDao();

            List<ChatRoomJson> errorChatList = chatRoomJsonDao.queryBuilder().where(ChatRoomJsonDao.Properties.Sender.eq(userInfo.getUid())
                    , ChatRoomJsonDao.Properties.Receiver.eq(chatRoomActivity.otherUid)).list();

            HashMap<String, ChatRoomJson> localChatMap = new HashMap<>();
            for (ChatRoomJson chatRoomJson : errorChatList) {

                if (chatRoomJson.getForegoingChatId() == null) {
                    chatRoomList.add(0, chatRoomJson);
                } else {
                    localChatMap.put(chatRoomJson.getForegoingChatId(), chatRoomJson);
                }
            }

            /**
             * 比对网络请求的数据
             */
            for (int index = chatRoomList.size() - 1; index >= 0; index--) {
                ChatRoomJson listItemChatRoom = chatRoomList.get(index);

                ChatRoomJson mapItemChatRoom = localChatMap.get(listItemChatRoom.getId());
                if (mapItemChatRoom != null) {
                    int indexOf = chatRoomList.indexOf(listItemChatRoom);
                    chatRoomList.add(indexOf + 1, mapItemChatRoom);
                }
            }
        }

        try {
            long lastSendTime = 0;

            //今天的Long值

            // String todayString = (String) DateFormat.format("yyyy-MM-dd", System.currentTimeMillis());
            // long todayLong = DateFormat.getDateFormat(mContext).parse(todayString).getTime();

            //int i = chatRoomList.size() - 1; i >= 0; i--
            //int i = 0; i < chatRoomList.size(); i++

            for (int i = chatRoomList.size() - 1; i >= 0; i--) {

                ChatRoomJson chatRoomJson = chatRoomList.get(i);
                String senderUid = chatRoomJson.getSender();

                if (userInfo.getUid().equals(senderUid)) {//是我发送的
                    chatRoomJson.setViewType(1);
                } else {
                    chatRoomJson.setViewType(0);
                }

                //计算间隔事件
                long thisSendTime = Long.parseLong(chatRoomJson.getDatetime());

                if (lastSendTime == 0) {
                    lastSendTime = thisSendTime;
                } else {
                    if (lastSendTime + 60 * 5 >= thisSendTime) {
                        lastSendTime = thisSendTime;
                        chatRoomJson.setPartitionTime(thisSendTime);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
