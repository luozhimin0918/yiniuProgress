package com.jyh.kxt.chat.util;

import com.jyh.kxt.chat.json.ChatRoomJson;

/**
 * Created by Mr'Dai on 2017/9/5.
 */

public interface OnChatMessage {
    void onChatMessage(ChatRoomJson chatRoomJson);
}
