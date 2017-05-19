package com.jyh.kxt.market.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.market.adapter.MarketHotSearchAdapter;
import com.jyh.kxt.market.adapter.MarketSearchAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.presenter.SearchPresenter;
import com.library.base.http.VarConstant;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.recycler.DividerGridItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:行情搜索
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener {

    private static final String SEARCH_HISTORY = "1";
    private static final String SEARCH_RESULT = "2";

    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    @BindView(R.id.layout_market_search_start) View layoutSearchStart;
    @BindView(R.id.layout_market_search_end) public PullToRefreshListView plvContent;
    @BindView(R.id.rv_content) RecyclerView rvContent;

    private SearchPresenter presenter;
    private String searchType;
    private String searchKey;

    private MarketHotSearchAdapter hotAdapter;
    private MarketSearchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_search, StatusBarColor.THEME1);

        presenter = new SearchPresenter(this);

        initView();

        searchKey = getIntent().getStringExtra(IntentConstant.SEARCH_KEY);
        plRootView.loadWait();
        if (searchKey != null) {
            searchType = SEARCH_RESULT;
            presenter.search(searchKey);
        } else {
            searchType = SEARCH_HISTORY;
            presenter.initHotSearch();
        }


    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        rvContent.setLayoutManager(manager);
        DividerGridItemDecoration decor = new DividerGridItemDecoration(this);
        decor.setSpanCount(3);
        decor.setShowVerticalLine(false);
        rvContent.addItemDecoration(decor);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchType = SEARCH_RESULT;
                    String key = edtSearch.getText().toString();
                    searchKey = key;
                    plRootView.loadWait();
                    presenter.search(key);
                    return true;
                }
                return false;
            }
        });

        edtSearch.addTextChangedListener(edtSearch.new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                if (RegexValidateUtil.isEmpty(s.toString())) {
                }
            }
        });
    }

    @OnClick({R.id.tv_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_break:
                onBackPressed();
                break;
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        switch (searchType) {
            case SEARCH_RESULT:
                presenter.search(searchKey);
                break;
            case SEARCH_HISTORY:
                presenter.initHotSearch();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(presenter.getClass().getName());
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    public void refresh(List<MarketItemBean> markets) {
        if (markets != null && markets.size() > 0)
            adapter.setData(markets);
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void init(List<MarketItemBean> markets) {
        if (markets == null || markets.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            hideHotSearch();
            plvContent.setVisibility(View.VISIBLE);
            if (adapter == null) {
                adapter = new MarketSearchAdapter(markets, this);
                plvContent.setAdapter(adapter);
            } else {
                adapter.setData(markets);
            }
            adapter.setSearchKey(searchKey);
            plRootView.loadOver();
        }
    }

    public void hideHotSearch() {
        layoutSearchStart.setVisibility(View.GONE);
        plRootView.loadOver();
    }

    public void showHotSearch(List<MarketItemBean> markets) {
        if (markets == null || markets.size() == 0) {
            layoutSearchStart.setVisibility(View.GONE);
        } else {
            layoutSearchStart.setVisibility(View.VISIBLE);
            if (hotAdapter == null) {
                hotAdapter = new MarketHotSearchAdapter(this, markets);
                hotAdapter.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position, View view) {
                        plRootView.loadWait();
                        MarketItemBean itemBean = hotAdapter.getData().get(position);
                        presenter.search(itemBean.getName());
                    }
                });
                rvContent.setAdapter(hotAdapter);
            } else {
                hotAdapter.setData(markets);
            }
        }
        plRootView.loadOver();
    }
}
