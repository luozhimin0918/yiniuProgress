package com.jyh.kxt.chat.presenter;

import android.database.Cursor;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.dao.ChatRoomJsonDao;
import com.jyh.kxt.base.dao.DBManager;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.ChatRoomActivity;
import com.jyh.kxt.chat.adapter.ChatRoomAdapter;
import com.jyh.kxt.chat.json.ChatPreviewJson;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.chat.util.ChatSocketUtil;
import com.jyh.kxt.chat.util.OnChatMessage;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SPUtils;
import com.library.util.SystemUtil;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomPresenter extends BasePresenter {
    @BindObject ChatRoomActivity chatRoomActivity;

    private UserJson userInfo;
    private List<ChatRoomJson> baseChatRoomList;
    private ChatRoomAdapter chatRoomAdapter;
    private HashMap<String, ChatRoomJson> temporaryChatRoomMap;

    private boolean isAllowLoadMore = true;

    public boolean isBannedForReceiver = false;

    private boolean isInitialLoadHistory = true;

    private View listHeaderView;


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
        lvContentList.setAdapter(chatRoomAdapter);

        lvContentList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    //超过25条说明可以加载更多
                    if (listHeaderView == null && baseChatRoomList.size() > 25 && isAllowLoadMore) {
                        LayoutInflater mInflater = LayoutInflater.from(mContext);
                        listHeaderView = mInflater.inflate(R.layout.item_chat_more, chatRoomActivity.lvContentList, false);
                        chatRoomActivity.lvContentList.addHeaderView(listHeaderView);

                        requestPullMoreData();
                    }
                }
            }
        });
        /*
         * 初始化EditText
         */
        chatRoomActivity.publishContentEt.setImeOptions(EditorInfo.IME_ACTION_SEND);
        chatRoomActivity.publishContentEt.setInputType(InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE);

        chatRoomActivity.publishContentEt.setSingleLine(false);
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

        ChatSocketUtil.getInstance().sendSocketParams(mContext, chatRoomActivity.otherUid, onSocketTextMessage);


        String chatPreviewDraft = SPUtils.getString(mContext, SpConstant.CHAT_PREVIEW);
        if (!TextUtils.isEmpty(chatPreviewDraft)) {
            List<ChatPreviewJson> chatDraftList = JSONArray.parseArray(chatPreviewDraft, ChatPreviewJson.class);
            if (chatDraftList != null) {
                for (ChatPreviewJson chatPreviewJson : chatDraftList) {
                    String receiver = chatPreviewJson.getReceiver();
                    if (receiver.equals(chatRoomActivity.otherUid) && chatPreviewJson.getType() == 2) {
                        String draftContent = chatPreviewJson.getContent();
                        chatRoomActivity.publishContentEt.setText(draftContent);
                        chatRoomActivity.publishContentEt.setSelection(draftContent.length());
                    }
                }
            }
        }
    }

    /**
     * 退出的时候清空未读数量
     */
    public void requestClearUnread() {
        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);
        volleyRequest.setTag("clearUnread");

        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("sender", userInfo.getUid());
        jsonParam.put("receiver", chatRoomActivity.otherUid);
        volleyRequest.doPost(HttpConstant.MESSAGE_CLEAR_UNREAD, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String jsonData) {
            }
        });
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
            ChatRoomJson lastChatRoomJson = baseChatRoomList.get(0);
            jsonParam.put("last_id", lastChatRoomJson.getId());
        }
        volleyRequest.doPost(HttpConstant.MESSAGE_HISTORY, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String chatRoomString) {
                try {
                    if (listHeaderView != null) {
                        chatRoomActivity.lvContentList.removeHeaderView(listHeaderView);
                        listHeaderView = null;
                    }

                    JSONObject chatRoomObject = JSONObject.parseObject(chatRoomString);

                    String chatRoomJsonList = chatRoomObject.getString("list");
                    String bannedForReceiver = chatRoomObject.getString("is_banned_for_receiver");

                    isBannedForReceiver = "1".equals(bannedForReceiver);

                    if (chatRoomJsonList != null) {
                        List<ChatRoomJson> chatRoomList = JSONArray.parseArray(chatRoomJsonList, ChatRoomJson.class);

                        if (chatRoomList.size() == 0) {
                            isAllowLoadMore = false;
                        }

                        analyzeListData(chatRoomList);
                        baseChatRoomList.addAll(0, chatRoomList);
                    } else {
                        isAllowLoadMore = false;
                    }


                    //通知刷新
                    chatRoomAdapter.notifyDataSetChanged();
                    if (isInitialLoadHistory) {
                        chatRoomActivity.tvRoomReminder.setVisibility(View.GONE);
                        isInitialLoadHistory = false;
                        chatRoomActivity.lvContentList.setSelection(chatRoomAdapter.getCount());
                    }

                    if (isBannedForReceiver) {
                        chatRoomActivity.tvRoomReminder.setVisibility(View.VISIBLE);
                        chatRoomActivity.tvRoomReminder.setText("回复消息将自动解除屏蔽");
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (listHeaderView != null) {
                        chatRoomActivity.lvContentList.removeHeaderView(listHeaderView);
                        listHeaderView = null;
                    }

                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);

                if (listHeaderView != null) {
                    chatRoomActivity.lvContentList.removeHeaderView(listHeaderView);
                    listHeaderView = null;
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

                if (isBannedForReceiver) {
                    chatRoomActivity.tvRoomReminder.setVisibility(View.GONE);
                    isBannedForReceiver = false;
                }


                String uniqueIdentification = getUniqueIdentification();
                ChatRoomJson temporaryChatRoom = temporaryChatRoomMap.get(uniqueIdentification);

                if (mChatRoomJson.getIs_banned_for_sender() == 1) {
                    onErrorResponse(null);
                    return;
                }

                temporaryChatRoom.setMsgSendStatus(0);
                temporaryChatRoom.setId(mChatRoomJson.getId());

                if (baseChatRoomList.size() != 0) {
                    int temporaryPosition = baseChatRoomList.indexOf(temporaryChatRoom);
                    baseChatRoomList.remove(temporaryPosition);
                    baseChatRoomList.add(baseChatRoomList.size(), temporaryChatRoom);
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

        String foregoingChatId = null;
        if (baseChatRoomList.size() != 0) {
            for (int i = baseChatRoomList.size() - 1; i >= 0; i--) {
                ChatRoomJson lastChatRoom = baseChatRoomList.get(i);
                String lastChatRoomId = lastChatRoom.getId();
                if (lastChatRoomId != null && Double.parseDouble(lastChatRoomId) > 1) {//保证了是成功后面的数据
                    foregoingChatId = lastChatRoom.getId();
                    break;
                }
            }
        } else {
            foregoingChatId = null;
        }

        String memberAvatar = userInfo.getWriter_avatar() == null ? userInfo.getPicture() : userInfo.getWriter_avatar();

        ChatRoomJson fakeChatRoom = new ChatRoomJson();
        fakeChatRoom.setId(randomUnique);//设置默认假数据
        fakeChatRoom.setViewType(1);
        fakeChatRoom.setMsgSendStatus(1);
        fakeChatRoom.setContent(chatContent);
        fakeChatRoom.setDatetime(sendDateTime);
        fakeChatRoom.setSender(userInfo.getUid());
        fakeChatRoom.setReceiver(chatRoomActivity.otherUid);
        fakeChatRoom.setAvatar(memberAvatar);
        fakeChatRoom.setForegoingChatId(foregoingChatId);

        DBManager mDBManager = DBManager.getInstance(mContext);
        ChatRoomJsonDao chatRoomJsonDao = mDBManager.getDaoSessionWrit().getChatRoomJsonDao();
        //得到他们聊天记录里面的5条数据
        String daoCountSql = "SELECT COUNT(*) count FROM CHAT_ROOM_BEAN WHERE  SENDER = '" + userInfo.getUid() + "" +
                "' AND  RECEIVER = '" + chatRoomActivity.otherUid + "'";
        Cursor cursor = chatRoomJsonDao.getDatabase().rawQuery(daoCountSql, null);

        int daoCount = 0;
        while (cursor.moveToNext()) {
            daoCount = cursor.getInt(cursor.getColumnIndex("count"));
        }
        cursor.close();

        if (daoCount > 5) {
            String daoDeleteSql = "DELETE FROM CHAT_ROOM_BEAN WHERE ID IN " +
                    "(select id from CHAT_ROOM_BEAN WHERE  SENDER = '" + userInfo.getUid() + "' AND  " +
                    "RECEIVER = '" + chatRoomActivity.otherUid + "' " +
                    "limit 0," + (daoCount - 5) + "); ";
            chatRoomJsonDao.getDatabase().execSQL(daoDeleteSql);
        }

        //保存假数据到数据库
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

            HashMap<String, ChatRoomJson> netWorkChatMap = new HashMap<>();
            /**
             * 拿到网络上数据
             */
            for (int position = 0; position < chatRoomList.size(); position++) {
                ChatRoomJson listItemChatRoom = chatRoomList.get(position);
                netWorkChatMap.put(listItemChatRoom.getId(), listItemChatRoom);
            }
            for (int i = errorChatList.size() - 1; i >= 0; i--) {
                ChatRoomJson chatRoomJson = errorChatList.get(i);
                ChatRoomJson chatRoomIndex = netWorkChatMap.get(chatRoomJson.getForegoingChatId());
                if (chatRoomIndex != null) {
                    if (chatRoomJson.getForegoingChatId() == null) {
                        chatRoomList.add(0, chatRoomJson);
                    } else {
                        int indexOf = chatRoomList.indexOf(chatRoomIndex);
                        chatRoomList.add(indexOf + 1, chatRoomJson);
                    }
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

            for (int i = 0; i < chatRoomList.size(); i++) {

                ChatRoomJson chatRoomJson = chatRoomList.get(i);
                String senderUid = chatRoomJson.getSender();

                if (userInfo.getUid().equals(senderUid)) {//是我发送的
                    chatRoomJson.setViewType(1);
                } else {
                    chatRoomJson.setViewType(0);
                }

                //计算间隔事件
                long thisSendTime = Long.parseLong(chatRoomJson.getDatetime()) * 1000;

                if (lastSendTime == 0) {
                    lastSendTime = thisSendTime;
                    continue;
                }

                if (thisSendTime - lastSendTime >= 60 * 5 * 1000) {
                    lastSendTime = thisSendTime;
                    chatRoomJson.setPartitionTime(thisSendTime);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onDestroy() {
        ChatSocketUtil.getInstance().unOnChatMessage(onSocketTextMessage);
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

    private OnChatMessage onSocketTextMessage = new OnChatMessage() {
        @Override
        public void onChatMessage(ChatRoomJson chatRoomJson) {
            if (chatRoomActivity.otherUid.equals(chatRoomJson.getSender())) {
                chatRoomJson.setMsgSendStatus(0);
                chatRoomJson.setViewType(0);
                baseChatRoomList.add(chatRoomJson);
                chatRoomAdapter.notifyDataSetChanged();
            }
        }
    };

}
