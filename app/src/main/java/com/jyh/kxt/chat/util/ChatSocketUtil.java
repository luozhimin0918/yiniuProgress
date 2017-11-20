//package com.jyh.kxt.chat.util;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.alibaba.fastjson.JSONObject;
//import com.jyh.kxt.base.constant.IntentConstant;
//import com.jyh.kxt.base.constant.SpConstant;
//import com.jyh.kxt.base.utils.LoginUtils;
//import com.jyh.kxt.chat.json.ChatRoomJson;
//import com.jyh.kxt.index.json.MainInitJson;
//import com.jyh.kxt.user.json.UserJson;
//import com.library.base.http.VarConstant;
//import com.library.util.SPUtils;
//
//import org.apache.http.message.BasicNameValuePair;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.tavendo.autobahn.WebSocketConnection;
//import de.tavendo.autobahn.WebSocketConnectionHandler;
//import de.tavendo.autobahn.WebSocketOptions;
//
///**
// * Created by Mr'Dai on 2017/9/4.
// */
//
//public class ChatSocketUtil {
//    private static ChatSocketUtil chatSocketUtil;
//    private boolean isChatRoomIn;
//
//    public static ChatSocketUtil getInstance() {
//        if (chatSocketUtil == null) {
//            chatSocketUtil = new ChatSocketUtil();
//        }
//        return chatSocketUtil;
//    }
//
//
//    private WebSocketConnection mConnection;
//    private List<OnChatMessage> onSocketTextMessageList = new ArrayList<>();
//
//    public void sendSocketParams(Context mContext, String otherUid, OnChatMessage onChatMessage) {
//        try {
//            if (mContext == null) {//这里传入的有Fragment 可能被销毁
//                return;
//            }
//
//            if (mConnection == null) {
//                mConnection = new WebSocketConnection();
//            }
//
//            onSocketTextMessageList.add(onChatMessage);
//
//            if (!mConnection.isConnected()) {
//                requestConnectSocket(mContext, otherUid);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void requestConnectSocket(final Context mContext, final String otherUid) {
//        final UserJson userInfo = LoginUtils.getUserInfo(mContext);
//
//        if (userInfo == null) {
//            return;
//        }
//        String indexConfig = SPUtils.getString(mContext, SpConstant.INIT_LOAD_APP_CONFIG);
//        if (!"".equals(indexConfig)) {
//            try {
//                MainInitJson initConfig = JSONObject.parseObject(indexConfig, MainInitJson.class);
//
//
//                WebSocketOptions options = new WebSocketOptions();
//                options.setReceiveTextMessagesRaw(false);
//                options.setSocketConnectTimeout(30000);
//                options.setSocketReceiveTimeout(10000);
//
//                List<BasicNameValuePair> headers = new ArrayList<>();
//                headers.add(new BasicNameValuePair(IntentConstant.SOCKET_ORIGIN, VarConstant.SOCKET_DOMAIN));
//                mConnection.connect(initConfig.getMessage_socket_addr(), null, new WebSocketConnectionHandler() {
//                    @Override
//                    public void onOpen() {
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("cmd", "login");
//                        jsonObject.put("r", "kxt");
//                        jsonObject.put("uid", userInfo.getUid());
//                        jsonObject.put("rid", "1");
//
//                        jsonObject.put("name", userInfo.getNickname());
//                        jsonObject.put("mark", "login");
//
//                        mConnection.sendTextMessage(jsonObject.toString());
//                    }
//
//                    @Override
//                    public void onTextMessage(String payload) {
//                        try {
//                            if (onSocketTextMessageList.size() != 0 && payload != null && !"".equals(payload)) {
//                                for (OnChatMessage onSocketTextMessage : onSocketTextMessageList) {
//
//                                    JSONObject payloadBean = JSONObject.parseObject(payload);
//                                    String cmd = payloadBean.getString("cmd");
//
//                                    if ("message".equals(cmd)) {
//                                        ChatRoomJson chatRoomJson = JSONObject.parseObject(payload, ChatRoomJson.class);
//                                        onSocketTextMessage.onChatMessage(chatRoomJson);
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onClose(int code, String reason) {
//
//                    }
//                }, options, headers);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
//
//    public void unOnChatMessage(OnChatMessage onSocketTextMessage) {
//        try {
//            onSocketTextMessageList.remove(onSocketTextMessage);
//            if (onSocketTextMessageList.size() == 0) {
//                Log.e("unOnChatMessage", "unOnChatMessage: 关闭Socket,无回调监听");
//                mConnection.disconnect();
//                mConnection = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean isChatRoomIn() {
//        return isChatRoomIn;
//    }
//
//    public void setChatRoomIn(boolean chatRoomIn) {
//        isChatRoomIn = chatRoomIn;
//    }
//}
