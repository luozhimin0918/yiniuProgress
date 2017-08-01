package com.jyh.kxt.trading.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.jyh.kxt.trading.adapter.ColumnistAdapter;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.presenter.AuthorItemPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:名家专栏item
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/31.
 */

public class AuthorFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2, PageLoadLayout.OnAfreshLoadListener,
        AdapterView.OnItemClickListener {

    public static final String CODE = "code";
    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private AuthorItemPresenter presenter;
    private String code;
    private ColumnistAdapter adapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {

        setContentView(R.layout.fragment_trading_list);

        plRootView.setOnAfreshLoadListener(this);
        plContent.setOnRefreshListener(this);
        plContent.setMode(PullToRefreshBase.Mode.BOTH);
        plContent.setOnItemClickListener(this);
        plContent.setDividerNull();

        code = getArguments().getString(CODE);
        presenter = new AuthorItemPresenter(this, code);
        presenter.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(code);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refreshView();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init();
    }

    public void init(List<ColumnistListJson> jsons) {
        if (adapter == null) {
            adapter = new ColumnistAdapter(jsons, getContext());
            plContent.setAdapter(adapter);
        } else {
            adapter.setData(jsons);
        }
        plRootView.loadOver();
    }

    public void loadMore(List<ColumnistListJson> jsons) {
        if (adapter == null) {
            adapter = new ColumnistAdapter(jsons, getContext());
            plContent.setAdapter(adapter);
        } else {
            adapter.addData(jsons);
        }
    }

    public void refresh(List<ColumnistListJson> jsons) {
        if (adapter == null) {
            adapter = new ColumnistAdapter(jsons, getContext());
            plContent.setAdapter(adapter);
        } else {
            adapter.setData(jsons);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<ColumnistListJson> data = adapter.getData();
        if (data == null) return;
        int clickPosition = position - 1;
        int size = data.size();
        if (size - 1 <= clickPosition) return;
        Intent intent = new Intent(getContext(), AuthorActivity.class);
        intent.putExtra(IntentConstant.O_ID, data.get(clickPosition).getId());
        getContext().startActivity(intent);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        plContent.setDividerNull();
    }
}
