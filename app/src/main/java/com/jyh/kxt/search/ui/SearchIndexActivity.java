package com.jyh.kxt.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.adapter.SearchTypeAdapter;
import com.jyh.kxt.base.annotation.OnItemClickListener;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.search.adapter.QuoteAdapter;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.json.SearchType;
import com.jyh.kxt.search.presenter.SearchIndexPresenter;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.flowlayout.TagAdapter;
import com.library.widget.flowlayout.TagFlowLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:搜索主页
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchIndexActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshListView
        .OnRefreshListener2 {
    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.tv_break) TextView tvBreak;
    @BindView(R.id.rv_type) RecyclerView rvType;
    @BindView(R.id.iv_del) ImageView ivDel;
    @BindView(R.id.fl_history) TagFlowLayout flHistory;
    @BindView(R.id.ll_history) LinearLayout llHistory;
    @BindView(R.id.pl_content) public PullToRefreshListView rvContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.layout_start) View startView;

    private SearchIndexPresenter presenter;
    private SearchTypeAdapter searchTypeAdapter;
    private List<String> searchHistory;
    private TagAdapter<String> tagAdapter;
    private QuoteAdapter adapter;
    private String searchKey;
    private TextView headView;
    private View footView;
    public boolean isCanBack;

    private RelativeLayout rlView;
    private ImageView ivIcon;
    private TextView tvFoot1;
    private TextView tvFoot2;
    private ImageView ivFootMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_search, StatusBarColor.THEME1);
        presenter = new SearchIndexPresenter(this);
        initView();
        presenter.init();
    }

    private void initView() {

        rvContent.setDividerNull();
        rvContent.setOnRefreshListener(this);
        plRootView.setOnAfreshLoadListener(this);
        rvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<QuoteItemJson> dataList = adapter.dataList;
                int dataPosition=position-2;
                if (dataPosition < dataList.size()) {
                    Intent intent = new Intent(getContext(), MarketDetailActivity.class);
                    MarketItemBean marketBean = new MarketItemBean();
                    marketBean.setCode(dataList.get(dataPosition).getCode());
                    intent.putExtra(IntentConstant.MARKET, marketBean);
                    startActivity(intent);
                }
            }
        });

        edtSearch.setHint("搜索");

        final List<SearchType> searchTypes = new ArrayList<>();
        searchTypes.add(new SearchType("行情", VarConstant.SEARCH_TYPE_QUOTE));
        searchTypes.add(new SearchType("要闻", VarConstant.SEARCH_TYPE_NEWS));
        searchTypes.add(new SearchType("视听", VarConstant.SEARCH_TYPE_VIDEO));
        searchTypes.add(new SearchType("专栏", VarConstant.SEARCH_TYPE_COLUMNIST));
        searchTypes.add(new SearchType("观点", VarConstant.SEARCH_TYPE_VIEWPOINT));
        searchTypes.add(new SearchType("文章", VarConstant.SEARCH_TYPE_ARTICLE));
        GridLayoutManager manager = new GridLayoutManager(this, 3);
        searchTypeAdapter = new SearchTypeAdapter(this, searchTypes);
        searchTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra(SearchActivity.TYPE, searchTypes.get(position).getCode());
                startActivity(intent);
            }
        });
        rvType.setLayoutManager(manager);
        rvType.setAdapter(searchTypeAdapter);

        flHistory.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String searchKey = searchHistory.get(position);
                edtSearch.setText(searchKey);
                SearchIndexActivity.this.searchKey = searchKey;
                presenter.search(searchKey);
                return false;
            }
        });
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //监听软键盘搜索按钮
                    String key = edtSearch.getText();
                    SearchIndexActivity.this.searchKey = key;
                    presenter.search(key);
                    return true;
                }
                return false;
            }
        });
    }

    @OnClick({R.id.tv_break, R.id.iv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_break:
                onBackPressed();
                break;
            case R.id.iv_del:
                presenter.delSearchHistory();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isCanBack) {
            isCanBack = false;
            presenter.init();
            plRootView.loadOver();
            startView.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (searchTypeAdapter != null)
            searchTypeAdapter.notifyDataSetChanged();
        if (adapter != null)
            adapter.notifyDataSetChanged();
        if (tagAdapter != null)
            tagAdapter.notifyDataChanged();
        if (headView != null)
            headView.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color2));

        if (footView != null) {
            footView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
            if (rlView != null)
                rlView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme1));
            if (ivIcon != null)
                ivIcon.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_search_logo));
            if (tvFoot1 != null)
                tvFoot1.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color5));
            if (tvFoot2 != null)
                tvFoot2.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color17));
            if (ivFootMore != null)
                ivFootMore.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_comment_more));
        }

    }

    /**
     * 显示搜索记录
     *
     * @param historys
     */
    public void showHistorySearch(List<String> historys) {
        llHistory.setVisibility(View.VISIBLE);
        if (historys == null || historys.size() == 0) {
            hideHistory();
        } else {
            searchHistory = historys;
            if (tagAdapter == null) {
                tagAdapter = new TagAdapter<String>(searchHistory) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_tv,
                                flHistory, false);
                        tv.setText(s);
                        return tv;
                    }
                };

                flHistory.setAdapter(tagAdapter);
            } else {
                tagAdapter.setTagDatas(searchHistory);
            }
        }
    }

    /**
     * 隐藏搜索记录
     */
    private void hideHistory() {
        llHistory.setVisibility(View.GONE);
    }

    /**
     * 刷新搜索记录
     *
     * @param list
     */
    public void refreshSearchHistory(List<String> list) {
        if (list == null || list.size() == 0) {
            hideHistory();
        } else {
            showHistorySearch(list);
        }
    }

    public void init(List<QuoteItemJson> quotes) {

        startView.setVisibility(View.GONE);
        rvContent.setVisibility(View.VISIBLE);

        if (quotes == null || quotes.size() == 0) {
            plRootView.setNullText(getString(R.string.error_search_null));
            plRootView.loadEmptyData();
        } else {
            if (adapter == null) {
                adapter = new QuoteAdapter(this, quotes);
                rvContent.setAdapter(adapter);
            } else {
                adapter.setData(quotes);
            }
            ListView refreshableView = rvContent.getRefreshableView();
            if (headView != null) {
                refreshableView.removeHeaderView(headView);
            }
            initHeadView();
            refreshableView.addHeaderView(headView);
            if (footView != null) {
                refreshableView.removeFooterView(footView);
            }
            initFootView();
            refreshableView.addFooterView(footView);

            plRootView.loadOver();
        }
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.search(searchKey);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        presenter.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        presenter.loadMore();
    }

    public void refresh(List<QuoteItemJson> quotes) {
        if (quotes == null || quotes.size() == 0) {
            rvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvContent.onRefreshComplete();
                }
            }, 200);
        } else {
            adapter.setData(quotes);
        }
    }

    public void loadMore(List<QuoteItemJson> quotes) {
        if (quotes == null || quotes.size() == 0) {
            rvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rvContent.onRefreshComplete();
                }
            }, 200);
        } else {
            adapter.addData(quotes);
        }
    }

    public void initHeadView() {
        headView = new TextView(getContext());
        headView.setText("行情");
        headView.setGravity(Gravity.CENTER_VERTICAL);
        headView.setPadding(SystemUtil.dp2px(getContext(), 15), 0, 0, 0);
        headView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        headView.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color2));
        headView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.theme1));
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SystemUtil.dp2px(getContext(), 40));
        headView.setLayoutParams(params);
    }

    public void initFootView() {
        footView = LayoutInflater.from(getContext()).inflate(R.layout.layout_end_search, null, false);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchMainActivity.class);
                intent.putExtra(SearchMainActivity.SEARCH_KEY, searchKey);
                startActivity(intent);
            }
        });
        rlView = (RelativeLayout) footView.findViewById(R.id.rl_more);
        ivIcon = (ImageView) footView.findViewById(R.id.iv_icon);
        tvFoot1 = (TextView) footView.findViewById(R.id.tv1);
        tvFoot2 = (TextView) footView.findViewById(R.id.tv2);
        ivFootMore = (ImageView) footView.findViewById(R.id.iv_more);
    }
}
