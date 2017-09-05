package com.jyh.kxt.chat.util;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.VarConstant;
import com.library.util.SPUtils;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketConnectionHandler;
import de.tavendo.autobahn.WebSocketOptions;

/**
 * Created by Mr'Dai on 2017/9/4.
 */

public class ChatSocketUtil {
    private static ChatSocketUtil chatSocketUtil;

    public static ChatSocketUtil getInstance() {
        if (chatSocketUtil == null) {
            chatSocketUtil = new ChatSocketUtil();
        }
        return chatSocketUtil;
    }


    private WebSocketConnection mConnection = new WebSocketConnection();
    private List<OnChatMessage> onSocketTextMessageList = new ArrayList<>();

    public void sendSocketParams(Context mContext, String otherUid, OnChatMessage onChatMessage) {
        try {
            if (mContext == null) {//这里传入的有Fragment 可能被销毁
                return;
            }

            onSocketTextMessageList.add(onChatMessage);

            if (!mConnection.isConnected()) {
                requestConnectSocket(mContext, otherUid);
            } else {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("cmd", "login");
//                mConnection.sendTextMessage(jsonObject.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestConnectSocket(final Context mContext, final String otherUid) {
        final UserJson userInfo = LoginUtils.getUserInfo(mContext);

        if (userInfo == null) {
            return;
        }
        String indexConfig = SPUtils.getString(mContext, SpConstant.INIT_LOAD_APP_CONFIG);
        if (!"".equals(indexConfig)) {
            try {
                MainInitJson initConfig = JSONObject.parseObject(indexConfig, MainInitJson.class);


                WebSocketOptions options = new WebSocketOptions();
                options.setReceiveTextMessagesRaw(false);
                options.setSocketConnectTimeout(30000);
                options.setSocketReceiveTimeout(10000);

                List<BasicNameValuePair> headers = new ArrayList<>();
                headers.add(new BasicNameValuePair(IntentConstant.SOCKET_ORIGIN, VarConstant.SOCKET_DOMAIN));
                mConnection.connect(initConfig.getMessage_socket_addr(), null, new WebSocketConnectionHandler() {
                    @Override
                    public void onOpen() {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("cmd", "login");
                        jsonObject.put("r", "kxt");
                        jsonObject.put("uid", userInfo.getUid());
                        jsonObject.put("rid", otherUid);

                        jsonObject.put("name", userInfo.getNickname());
                        jsonObject.put("mark", "login");

                        mConnection.sendTextMessage(jsonObject.toString());
                    }

                    @Override
                    public void onTextMessage(String payload) {
                        if (onSocketTextMessageList.size() != 0 && payload != null && !"".equals(payload)) {
                            for (OnChatMessage onSocketTextMessage : onSocketTextMessageList) {

                                if (payload != null && !"".equals(payload)) {

                                    JSONObject payloadBean = JSONObject.parseObject(payload);
                                    String cmd = payloadBean.getString("cmd");

                                    if ("message".equals(cmd)) {
                                        ChatRoomJson chatRoomJson = JSONObject.parseObject(payload, ChatRoomJson.class);

                                        String receiver = chatRoomJson.getReceiver();
                                        String sender = chatRoomJson.getSender();

//                                        String uid = userInfo.getUid();
//                                        if(uid.equals(receiver)){ //如果接收人是本人  则颠倒数据
//                                            chatRoomJson.setReceiver(sender);
//                                            chatRoomJson.setSender(receiver);
//                                        }

                                        onSocketTextMessage.onChatMessage(chatRoomJson);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onClose(int code, String reason) {

                    }
                }, options, headers);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void unOnChatMessage(OnChatMessage onSocketTextMessage) {
        try {
            onSocketTextMessageList.remove(onSocketTextMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
