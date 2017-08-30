package com.jyh.kxt.chat.presenter;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.chat.ChatRoomActivity;
import com.jyh.kxt.chat.adapter.ChatRoomAdapter;
import com.jyh.kxt.chat.json.BlockJson;
import com.jyh.kxt.chat.json.ChatRoomJson;
import com.jyh.kxt.index.presenter.PullListViewPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/30.
 */

public class ChatRoomPresenter extends BasePresenter {
    @BindObject ChatRoomActivity chatRoomActivity;

    public ChatRoomPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void initPullListView() {
        List<ChatRoomJson> chatRoomList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ChatRoomJson chatRoomJson = new ChatRoomJson();
            chatRoomJson.setViewType(i % 2 == 0 ? 0 : 1);
            chatRoomList.add(chatRoomJson);
        }

        ChatRoomAdapter chatRoomAdapter = new ChatRoomAdapter(mContext, chatRoomList);

        JSONObject parameterJson = new JSONObject();

        chatRoomActivity.ptrlChartRoomList.setDividerNull();

        chatRoomActivity.pullListViewPresenter.createView(chatRoomActivity.flListLayout);
        chatRoomActivity.pullListViewPresenter.setLoadMode(PullListViewPresenter.LoadMode.PAGE_LOAD);
        chatRoomActivity.pullListViewPresenter.setRequestInfo("", parameterJson, BlockJson.class);
        chatRoomActivity.pullListViewPresenter.setAdapter(chatRoomAdapter);
//        chatRoomActivity.pullListViewPresenter.startRequest();
    }
}
