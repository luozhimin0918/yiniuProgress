package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.adapter.BlockAdapter;
import com.jyh.kxt.chat.json.BlockJson;
import com.jyh.kxt.index.presenter.PullListViewPresenter;
import com.library.base.http.VarConstant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 屏蔽列表
 */
public class BlockActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.fl_block_content) FrameLayout flBlockContent;

    private PullListViewPresenter pullListViewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block, StatusBarColor.THEME1);

        tvBarTitle.setText("已屏蔽用户");
        initPullListView();

    }

    private void initPullListView() {
        pullListViewPresenter = new PullListViewPresenter(this);

        List<BlockJson> blockList = new ArrayList<>();
        BlockAdapter blockAdapter = new BlockAdapter(this, blockList);

        JSONObject parameterJson = new JSONObject();

        pullListViewPresenter.createView(flBlockContent);
        pullListViewPresenter.setLoadMode(PullListViewPresenter.LoadMode.PAGE_LOAD);
        parameterJson.put(VarConstant.HTTP_SENDER, LoginUtils.getUserInfo(this).getUid());
        pullListViewPresenter.setRequestInfo(HttpConstant.MSG_BANNED_LIST, parameterJson, BlockJson.class);
        pullListViewPresenter.setAdapter(blockAdapter);
        pullListViewPresenter.startRequest();
    }

    @OnClick({R.id.iv_bar_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(getClass().getName());
    }
}
