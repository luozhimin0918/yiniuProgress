package com.jyh.kxt.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewDebug;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.chat.adapter.LetterListAdapter;
import com.jyh.kxt.chat.json.LetterJson;
import com.jyh.kxt.chat.json.LetterListJson;
import com.jyh.kxt.chat.presenter.LetterPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:私信列表
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class LetterActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    public LetterPresenter presenter;
    private LetterListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);
        presenter = new LetterPresenter(this);
        initView();
        plRootView.loadWait();
        presenter.init();
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
            //系统消息
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

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_msg_ban));
        if (adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(LetterPresenter.class.getName());
    }

    public void init(LetterJson letterJson) {
        if (letterJson == null) {
            plRootView.loadEmptyData();
            return;
        }
        adapter = new LetterListAdapter(letterJson.getList(), this, plContent.getRefreshableView());
        String show_red_dot = letterJson.getShow_red_dot();
        presenter.scrollListener(plContent, adapter);
        plContent.setAdapter(adapter);
        adapter.setShowRed(show_red_dot != null && "1".equals(show_red_dot));
        plRootView.loadOver();
    }

    public void refresh(LetterJson letterJson) {
        if(letterJson==null) return;
        String show_red_dot = letterJson.getShow_red_dot();
        List<LetterListJson> list = letterJson.getList();
        if (list == null || list.size() == 0) return;
        adapter.setData(list);
        adapter.setShowRed(show_red_dot != null && "1".equals(show_red_dot));
    }

}
