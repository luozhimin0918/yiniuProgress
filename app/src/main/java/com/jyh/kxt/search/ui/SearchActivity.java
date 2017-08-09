package com.jyh.kxt.search.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.widget.SearchEditText;
import com.jyh.kxt.index.adapter.VideoSearchAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.search.adapter.QuoteAdapter;
import com.jyh.kxt.search.adapter.ViewpointAdapter;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.presenter.SearchPresenter;
import com.jyh.kxt.trading.adapter.ColumnistAdapter;
import com.jyh.kxt.trading.adapter.ViewpointSearchAdapter;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.library.base.http.VarConstant;
import com.library.widget.PageLoadLayout;
import com.library.widget.flowlayout.FlowLayout;
import com.library.widget.flowlayout.TagAdapter;
import com.library.widget.flowlayout.TagFlowLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:搜索单页
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/7.
 */

public class SearchActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener2 {

    public static final String TYPE = "type";
    public static final String SEARCHKEY = "searchKey";

    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.iv_break) ImageView ivBreak;//回退按钮
    @BindView(R.id.tv_break) TextView tvBreak;//取消按钮
    @BindView(R.id.edt_search) SearchEditText edtSearch;
    @BindView(R.id.layout_market_search_end) public PullToRefreshListView plvContent;//搜索结果
    @BindView(R.id.layout_market_search_start) View layoutSearchStart;
    @BindView(R.id.iv_clear_history) ImageView ivClearHistory;
    @BindView(R.id.ll_hot) LinearLayout llHot;//搜索历史根布局
    @BindView(R.id.fl_hot) TagFlowLayout historyView;//搜索历史流式布局

    private SearchPresenter presenter;

    private String type;//搜索类型
    private String searchKey;//搜索关键字
    private List<String> searchHistoryList;

    private TagAdapter<String> tagAdapter;
    private QuoteAdapter marketSearchAdapter;
    private VideoSearchAdapter videoAdapter;
    private NewsAdapter newsAdapter;
    private ColumnistAdapter columnistAdapter;
    private ViewpointSearchAdapter viewpointSearchAdapter;
    private ViewpointAdapter viewpointAdapter;
    private QuoteAdapter quoteAdapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_search, StatusBarColor.THEME1);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getStringExtra(TYPE);
            searchKey = intent.getStringExtra(SEARCHKEY);
        }

        presenter = new SearchPresenter(this, type);

        initView();

        if (searchKey != null) {
            plRootView.loadWait();
            presenter.search(searchKey);
            edtSearch.setText(searchKey);
        } else {
            presenter.initHistorySearch();
        }

    }

    private void initView() {
        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);
        plvContent.setDividerNull();
        plvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        plvContent.setOnItemClickListener(this);

        switch (type) {
            case VarConstant.SEARCH_TYPE_NEWS:
                edtSearch.setHint("搜索要闻");
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                edtSearch.setHint("搜索视听");
                break;
            case VarConstant.SEARCH_TYPE_ARTICLE:
                edtSearch.setHint("搜索专栏文章");
                break;
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                edtSearch.setHint("搜索专栏用户");
                break;
            case VarConstant.SEARCH_TYPE_QUOTE:
                edtSearch.setHint("搜索行情");
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                edtSearch.setHint("搜索名家观点");
                break;
        }

        historyView.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String searchKey = searchHistoryList.get(position);
                edtSearch.setText(searchKey);
                plRootView.loadWait();
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
                    searchKey = key;
                    plRootView.loadWait();
                    presenter.search(key);
                    return true;
                }
                return false;
            }
        });

    }

    public void init(String info) {
        if (info == null || info.equals("")) {
            plRootView.setNullText(getString(R.string.error_search_null));
            plRootView.loadEmptyData();
        } else {
            try {
                switch (type) {
                    case VarConstant.SEARCH_TYPE_NEWS:
                    case VarConstant.SEARCH_TYPE_BLOG:
                        List<NewsJson> newsJsons = JSON.parseArray(info, NewsJson.class);
                        if (newsJsons == null || newsJsons.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (newsAdapter == null) {
                                newsAdapter = new NewsAdapter(getContext(), disposeData(newsJsons));
                                plvContent.setAdapter(newsAdapter);
                            } else {
                                newsAdapter.setData(disposeData(newsJsons));
                            }
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIDEO:
                        List<VideoListJson> videos = JSON.parseArray(info, VideoListJson.class);
                        if (videos == null || videos.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (videoAdapter == null) {
                                videoAdapter = new VideoSearchAdapter(getContext(), disposeData(videos));
                                plvContent.setAdapter(videoAdapter);
                            } else {
                                videoAdapter.setData(disposeData(videos));
                            }
                            videoAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_ARTICLE:
                        List<NewsJson> newsJsonList = JSON.parseArray(info, NewsJson.class);
                        if (newsJsonList == null || newsJsonList.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (newsAdapter == null) {
                                newsAdapter = new NewsAdapter(getContext(), disposeData(newsJsonList));
                                plvContent.setAdapter(newsAdapter);
                            } else {
                                newsAdapter.setData(disposeData(newsJsonList));
                            }
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_COLUMNIST:
                        List<ColumnistListJson> columnists = JSON.parseArray(info, ColumnistListJson.class);
                        if (columnists == null || columnists.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (columnistAdapter == null) {
                                columnistAdapter = new ColumnistAdapter(disposeData(columnists), getContext());
                                plvContent.setAdapter(columnistAdapter);
                            } else {
                                columnistAdapter.setData(disposeData(columnists));
                            }
                            columnistAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_QUOTE:
                        List<QuoteItemJson> marketItemBeens = JSON.parseArray(info, QuoteItemJson.class);
                        if (marketItemBeens == null || marketItemBeens.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (marketSearchAdapter == null) {
                                marketSearchAdapter = new QuoteAdapter(this, disposeData(marketItemBeens));
                                plvContent.setAdapter(marketSearchAdapter);
                            } else {
                                marketSearchAdapter.setData(disposeData(marketItemBeens));
                            }
                            marketSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIEWPOINT:
                        List<ViewPointTradeBean> viewPoints = JSON.parseArray(info, ViewPointTradeBean.class);
                        if (viewPoints == null || viewPoints.size() == 0) {
                            plRootView.setNullText(getString(R.string.error_search_null));
                            plRootView.loadEmptyData();
                        } else {
                            if (viewpointSearchAdapter == null) {
                                viewpointSearchAdapter = new ViewpointSearchAdapter(this, disposeData(viewPoints));
                                plvContent.setAdapter(viewpointSearchAdapter);
                            } else {
                                viewpointSearchAdapter.setData(disposeData(viewPoints));
                            }
                            viewpointSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    default:
                        plRootView.setNullText(getString(R.string.error_search_null));
                        plRootView.loadEmptyData();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                plRootView.setNullText(getString(R.string.error_search_null));
                plRootView.loadEmptyData();
            }
        }
    }

    public void refresh(String info) {
        if (info != null && !info.equals("")) {
            try {
                switch (type) {
                    case VarConstant.SEARCH_TYPE_NEWS:
                    case VarConstant.SEARCH_TYPE_BLOG:
                        List<NewsJson> newsJsons = JSON.parseArray(info, NewsJson.class);
                        if (newsJsons != null && newsJsons.size() > 0) {
                            if (newsAdapter == null) {
                                newsAdapter = new NewsAdapter(getContext(), newsJsons);
                                plvContent.setAdapter(newsAdapter);
                            } else {
                                newsAdapter.setData(newsJsons);
                            }
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIDEO:
                        List<VideoListJson> videos = JSON.parseArray(info, VideoListJson.class);
                        if (videos != null && videos.size() > 0) {
                            if (videoAdapter == null) {
                                videoAdapter = new VideoSearchAdapter(getContext(), videos);
                                plvContent.setAdapter(videoAdapter);
                            } else {
                                videoAdapter.setData(videos);
                            }
                            videoAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_ARTICLE:
                        List<NewsJson> newsJsonList = JSON.parseArray(info, NewsJson.class);
                        if (newsJsonList != null && newsJsonList.size() > 0) {
                            if (newsAdapter == null) {
                                newsAdapter = new NewsAdapter(getContext(), newsJsonList);
                                plvContent.setAdapter(newsAdapter);
                            } else {
                                newsAdapter.setData(newsJsonList);
                            }
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_COLUMNIST:
                        List<ColumnistListJson> columnists = JSON.parseArray(info, ColumnistListJson.class);
                        if (columnists != null && columnists.size() > 0) {
                            if (columnistAdapter == null) {
                                columnistAdapter = new ColumnistAdapter(columnists, getContext());
                                plvContent.setAdapter(columnistAdapter);
                            } else {
                                columnistAdapter.setData(columnists);
                            }
                            columnistAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_QUOTE:
                        List<QuoteItemJson> marketItemBeens = JSON.parseArray(info, QuoteItemJson.class);
                        if (marketItemBeens != null && marketItemBeens.size() > 0) {
                            if (marketSearchAdapter == null) {
                                marketSearchAdapter = new QuoteAdapter(this, marketItemBeens);
                                plvContent.setAdapter(marketSearchAdapter);
                            } else {
                                marketSearchAdapter.setData(marketItemBeens);
                            }
                            marketSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIEWPOINT:
                        List<ViewPointTradeBean> viewPointTradeBeen = JSON.parseArray(info, ViewPointTradeBean.class);
                        if (viewPointTradeBeen != null && viewPointTradeBeen.size() > 0) {
                            if (viewpointSearchAdapter == null) {
                                viewpointSearchAdapter = new ViewpointSearchAdapter(this, viewPointTradeBeen);
                                plvContent.setAdapter(viewpointSearchAdapter);
                            } else {
                                viewpointSearchAdapter.setData(viewPointTradeBeen);
                            }
                            viewpointSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(String info) {
        if (info != null && !info.equals("")) {
            try {
                switch (type) {
                    case VarConstant.SEARCH_TYPE_NEWS:
                    case VarConstant.SEARCH_TYPE_BLOG:
                        List<NewsJson> newsJsons = JSON.parseArray(info, NewsJson.class);
                        if (newsJsons == null || newsJsons.size() == 0) {
                        } else {
                            newsAdapter.addData(newsJsons);
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIDEO:
                        List<VideoListJson> videoListJsons = JSON.parseArray(info, VideoListJson.class);
                        if (videoListJsons == null || videoListJsons.size() == 0) {
                        } else {
                            videoAdapter.addData(videoListJsons);
                            videoAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_ARTICLE:
                        List<NewsJson> newsJsonList = JSON.parseArray(info, NewsJson.class);
                        if (newsJsonList == null || newsJsonList.size() == 0) {
                        } else {
                            newsAdapter.addData(newsJsonList);
                            newsAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_COLUMNIST:
                        List<ColumnistListJson> columnists = JSON.parseArray(info, ColumnistListJson.class);
                        if (columnists == null || columnists.size() == 0) {
                        } else {
                            columnistAdapter.addData(columnists);
                            columnistAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_QUOTE:
                        List<MarketItemBean> marketItemBeen = JSON.parseArray(info, MarketItemBean.class);
                        if (marketItemBeen == null || marketItemBeen.size() == 0) {
                        } else {
                            marketSearchAdapter.setData(marketItemBeen);
                            marketSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIEWPOINT:
                        List<ViewPointTradeBean> viewPointTradeBeen = JSON.parseArray(info, ViewPointTradeBean.class);
                        if (viewPointTradeBeen == null || viewPointTradeBeen.size() == 0) {
                        } else {
                            viewpointSearchAdapter.addData(viewPointTradeBeen);
                            viewpointSearchAdapter.setSearchKey(searchKey);
                            hideSearchHistory();
                        }
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    private void hideSearchHistory() {
        layoutSearchStart.setVisibility(View.GONE);
        plvContent.setVisibility(View.VISIBLE);
        plRootView.loadOver();
    }

    @OnClick({R.id.iv_break, R.id.tv_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int dataPosition = position - 1;
        if (dataPosition >= 0) {
            switch (type) {
                case VarConstant.SEARCH_TYPE_QUOTE:
                    QuoteItemJson marketItemBean = marketSearchAdapter.dataList.get(dataPosition);
                    Intent intent = new Intent(this, MarketDetailActivity.class);
                    MarketItemBean market = new MarketItemBean();
                    market.setCode(marketItemBean.getCode());
                    market.setName(marketItemBean.getName());
                    intent.putExtra(IntentConstant.MARKET, market);
                    startActivity(intent);
                    break;
                case VarConstant.SEARCH_TYPE_NEWS:
                    NewsJson newsJson = newsAdapter.getData().get(dataPosition);
                    JumpUtils.jump(this, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(), newsJson
                            .getHref());
                    //保存浏览记录
                    BrowerHistoryUtils.save(getContext(), newsJson);
                    //单条刷新,改变浏览状态
                    newsAdapter.getView(dataPosition, view, parent);
                    break;
                case VarConstant.SEARCH_TYPE_VIDEO:
                    VideoListJson video = videoAdapter.getData().get(dataPosition);
                    JumpUtils.jump(this, VarConstant.OCLASS_VIDEO, VarConstant.OACTION_DETAIL, video.getUid(), null);
                    break;
                case VarConstant.SEARCH_TYPE_COLUMNIST:
                    List<ColumnistListJson> data = columnistAdapter.getData();
                    if (data == null) return;
                    int size = data.size();
                    if (size - 1 <= dataPosition) return;
                    Intent columnistIntent = new Intent(getContext(), AuthorActivity.class);
                    columnistIntent.putExtra(IntentConstant.O_ID, data.get(dataPosition).getId());
                    getContext().startActivity(columnistIntent);
                    break;
                case VarConstant.SEARCH_TYPE_ARTICLE:
                    NewsJson newsJson2 = newsAdapter.getData().get(dataPosition);
                    JumpUtils.jump(this, newsJson2.getO_class(), newsJson2.getO_action(), newsJson2.getO_id(), newsJson2
                            .getHref());
                    //保存浏览记录
                    BrowerHistoryUtils.save(getContext(), newsJson2);
                    //单条刷新,改变浏览状态
                    newsAdapter.getView(dataPosition, view, parent);
                    break;
                case VarConstant.SEARCH_TYPE_VIEWPOINT:
                    break;
            }
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

    /**
     * 初始化搜索历史布局
     *
     * @param historys
     */
    public void showHistorySearch(List<String> historys) {
        this.searchHistoryList = historys;
        if (searchHistoryList == null || searchHistoryList.size() == 0) {
            layoutSearchStart.setVisibility(View.GONE);
        } else {
            layoutSearchStart.setVisibility(View.VISIBLE);
            if (tagAdapter == null) {
                tagAdapter = new TagAdapter<String>(searchHistoryList) {
                    @Override
                    public View getView(FlowLayout parent, int position, String s) {
                        TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_flow_tv,
                                historyView, false);
                        tv.setText(s);
                        return tv;
                    }
                };

                historyView.setAdapter(tagAdapter);
            } else {
                tagAdapter.setTagDatas(searchHistoryList);
            }
        }
        plRootView.loadOver();
    }

    /**
     * 数据处理
     *
     * @param data
     * @return
     */
    private List disposeData(List data) {
        List newData;
        int size = data.size();
        if (size > VarConstant.LIST_MAX_SIZE) {
            newData = new ArrayList(data.subList(0, VarConstant.LIST_MAX_SIZE));
            int lastPosition = VarConstant.LIST_MAX_SIZE - 1;
            switch (type) {
                case VarConstant.SEARCH_TYPE_MAIN:
                    ViewPointTradeBean viewpoint = (ViewPointTradeBean) data.get(lastPosition);
                    presenter.setLastId(viewpoint.o_id);
                    break;
                case VarConstant.SEARCH_TYPE_VIEWPOINT:
                    ViewPointTradeBean viewpoint2 = (ViewPointTradeBean) data.get(lastPosition);
                    presenter.setLastId(viewpoint2.o_id);
                    break;
                case VarConstant.SEARCH_TYPE_NEWS:
                    NewsJson newsJson = (NewsJson) data.get(lastPosition);
                    presenter.setLastId(newsJson.getO_id());
                    break;
                case VarConstant.SEARCH_TYPE_VIDEO:
                    VideoListJson video = (VideoListJson) data.get(lastPosition);
                    presenter.setLastId(video.getId());
                    break;
                case VarConstant.SEARCH_TYPE_COLUMNIST:
                    ColumnistListJson authorNewsJson = (ColumnistListJson) data.get(lastPosition);
                    presenter.setLastId(authorNewsJson.getId());
                    break;
                case VarConstant.SEARCH_TYPE_BLOG:
                    NewsJson newsJson1 = (NewsJson) data.get(lastPosition);
                    presenter.setLastId(newsJson1.getO_id());
                    break;
                case VarConstant.SEARCH_TYPE_QUOTE:
                    QuoteItemJson quoteItemJson = (QuoteItemJson) data.get(lastPosition);
                    presenter.setLastId(quoteItemJson.getId());
                    break;
            }
            presenter.setMore(true);
        } else {
            newData = new ArrayList(data);
            presenter.setLastId("");
            presenter.setMore(false);
        }
        return newData;
    }


    public void refresh(List data) {

        switch (type) {
            case VarConstant.SEARCH_TYPE_MAIN:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_NEWS:
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                if (data == null || data.size() == 0) {
                } else {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoSearchAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(videoAdapter);
                    } else {
                        videoAdapter.setData(disposeData(data));
                    }
                    videoAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                if (data == null || data.size() == 0) {
                } else {
                    if (columnistAdapter == null) {
                        columnistAdapter = new ColumnistAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(columnistAdapter);
                    } else {
                        columnistAdapter.setData(disposeData(data));
                    }
                    columnistAdapter.setSearchKey(searchKey);
                }

                break;
            case VarConstant.SEARCH_TYPE_BLOG:
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_QUOTE:
                if (data == null || data.size() == 0) {
                } else {
                    if (quoteAdapter2 == null) {
                        quoteAdapter2 = new QuoteAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.setData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                }
                break;
        }

        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List data) {

        switch (type) {
            case VarConstant.SEARCH_TYPE_MAIN:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.addData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.addData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_NEWS:
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.addData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_VIDEO:
                if (data == null || data.size() == 0) {
                } else {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoSearchAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(videoAdapter);
                    } else {
                        videoAdapter.addData(disposeData(data));
                    }
                    videoAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                if (data == null || data.size() == 0) {
                } else {
                    if (columnistAdapter == null) {
                        columnistAdapter = new ColumnistAdapter(disposeData(data), getContext());
                        plvContent.setAdapter(columnistAdapter);
                    } else {
                        columnistAdapter.addData(disposeData(data));
                    }
                    columnistAdapter.setSearchKey(searchKey);
                }

                break;
            case VarConstant.SEARCH_TYPE_BLOG:
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.addData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_QUOTE:
                if (data == null || data.size() == 0) {
                } else {
                    if (quoteAdapter2 == null) {
                        quoteAdapter2 = new QuoteAdapter(getContext(), disposeData(data));
                        plvContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.addData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                }
                break;
        }

        plvContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plvContent.onRefreshComplete();
            }
        }, 200);
    }

}
