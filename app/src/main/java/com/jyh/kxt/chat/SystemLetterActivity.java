package com.jyh.kxt.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.chat.adapter.LetterSysAdapter;
import com.jyh.kxt.chat.json.LetterSysJson;
import com.jyh.kxt.chat.presenter.SystemLetterPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:系统消息列表页
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/28.
 */

public class SystemLetterActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase
        .OnRefreshListener2 {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private SystemLetterPresenter presenter;
    private LetterSysAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letter);

        presenter = new SystemLetterPresenter(this);

        initView();

        plRootView.loadWait();
        presenter.init();
    }

    private void initView() {
        ivBarFunction.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("消息");

        plRootView.setOnAfreshLoadListener(this);

        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnRefreshListener(this);
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick() {
        onBackPressed();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(SystemLetterPresenter.class.getName());
    }

    public void initData(List<LetterSysJson> letterSysJsons) {
        if (letterSysJsons == null || letterSysJsons.size() == 0) {
            plRootView.loadEmptyData();
            return;
        }
        List<LetterSysJson> data = presenter.disposeData(letterSysJsons);
        if (adapter == null) {
            adapter = new LetterSysAdapter(data, this);
            plContent.setAdapter(adapter);
        } else {
            adapter.setData(data);
        }
        plRootView.loadOver();
    }

    public void loadMore(List<LetterSysJson> letterSysJsons) {
        if (letterSysJsons == null || letterSysJsons.size() == 0) {
            presenter.isMore = false;
            return;
        }
        List<LetterSysJson> data = presenter.disposeData(letterSysJsons);
        adapter.addData(data);
    }

    public void refresh(List<LetterSysJson> letterSysJsons) {
        if (letterSysJsons == null || letterSysJsons.size() == 0) {
            return;
        }
        List<LetterSysJson> data = presenter.disposeData(letterSysJsons);
        adapter.setData(data);
    }
}
