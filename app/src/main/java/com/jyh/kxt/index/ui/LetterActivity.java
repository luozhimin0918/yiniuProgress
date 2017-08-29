package com.jyh.kxt.index.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.index.adapter.LetterListAdapter;
import com.jyh.kxt.index.json.LetterListJson;
import com.jyh.kxt.index.presenter.LetterPresenter;
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
        PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_content) PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    private LetterPresenter presenter;
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
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));

        plContent.setDividerNull();
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
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

        }
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
    protected void onChangeTheme() {
        super.onChangeTheme();
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(LetterPresenter.class.getName());
    }

    public void init(List<LetterListJson> list) {
        adapter = new LetterListAdapter(list, this, plContent.getRefreshableView());
        presenter.scrollListener(plContent.getRefreshableView(),adapter);
        plContent.setAdapter(adapter);
        plRootView.loadOver();
    }
}
