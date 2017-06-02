package com.jyh.kxt.market.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.market.adapter.MarketHotSearchAdapter;
import com.jyh.kxt.market.adapter.MarketSearchAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.presenter.SearchPresenter;
import com.library.util.RegexValidateUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.flowlayout.TagAdapter;
import com.library.widget.flowlayout.TagFlowLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:行情搜索
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener,
        AdapterView.OnItemClickListener {

    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    @BindView(R.id.layout_market_search_start) View layoutSearchStart;
    @BindView(R.id.layout_market_search_end) public PullToRefreshListView plvContent;

    @BindView(R.id.iv_clear_history) ImageView ivClear;
    @BindView(R.id.fl_hot) TagFlowLayout hotView;

    private SearchPresenter presenter;
    private String searchType;
    private String searchKey;

    private MarketHotSearchAdapter hotAdapter;
    private MarketSearchAdapter adapter;
    private TagAdapter<String> tagAdapter;
    private List<String> markets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_search, StatusBarColor.THEME1);

        presenter = new SearchPresenter(this);

        initView();

        searchKey = getIntent().getStringExtra(IntentConstant.SEARCH_KEY);
        plRootView.loadWait();
        if (searchKey != null) {
            presenter.search(searchKey);
        } else {
            presenter.initHotSearch();
        }


    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plvContent.setOnItemClickListener(this);

        hotView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String searchKey = markets.get(position);
                edtSearch.setText(searchKey);
                presenter.search(searchKey);
                return false;
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
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
        presenter.search(searchKey);
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
            plRootView.setNullText(getString(R.string.error_search_null));
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

    public void showHotSearch(List<String> markets) {
        this.markets = markets;
        if (markets == null || markets.size() == 0) {
            layoutSearchStart.setVisibility(View.GONE);
        } else {
            layoutSearchStart.setVisibility(View.VISIBLE);
            if (tagAdapter == null) {
                tagAdapter = new TagAdapter<String>(markets) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_tv,
                                hotView, false);
                        tv.setText(s);
                        return tv;
                    }
                };

                hotView.setAdapter(tagAdapter);
            } else {
                tagAdapter.setTagDatas(markets);
            }
        }
        plRootView.loadOver();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int dataPosition = position - 1;
        if (dataPosition >= 0) {
            MarketItemBean marketItemBean = adapter.getData().get(dataPosition);
            Intent intent = new Intent(this, MarketDetailActivity.class);
            intent.putExtra(IntentConstant.MARKET, marketItemBean);
            startActivity(intent);
        }
    }
}
