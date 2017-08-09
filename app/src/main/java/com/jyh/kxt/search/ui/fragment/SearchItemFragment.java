package com.jyh.kxt.search.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.index.adapter.VideoSearchAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.search.adapter.QuoteAdapter;
import com.jyh.kxt.search.adapter.ViewpointAdapter;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.json.QuoteJson;
import com.jyh.kxt.search.presenter.SearchItemPresenter;
import com.jyh.kxt.trading.adapter.ColumnistAdapter;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class SearchItemFragment extends BaseFragment implements PullToRefreshListView.OnRefreshListener2, PageLoadLayout
        .OnAfreshLoadListener {

    public static final String SEARCH_KEY = "search_key";
    public static final String SEARCH_TYPE = "search_type";

    @BindView(R.id.pl_content) public PullToRefreshListView plContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    private LinearLayout homeHeadView;
    private View viewQuote;
    private TextView tvTitleQuote;
    private TextView tvTitleViewpoint;
    private ListView rvContentQuote;
    private TextView tvMoreQuote;
    private View vLine1;
    private View vLine2;
    private View vLine3;

    private SearchItemPresenter presenter;

    private String searchKey;
    private String searchType;
    private QuoteAdapter quoteAdapter;
    private ViewpointAdapter viewpointAdapter;
    private NewsAdapter newsAdapter;
    private VideoSearchAdapter videoAdapter;
    private ColumnistAdapter columnistAdapter;
    private QuoteAdapter quoteAdapter2;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_search_list);

        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setOnRefreshListener(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            searchKey = arguments.getString(SEARCH_KEY);
            searchType = arguments.getString(SEARCH_TYPE);
        }

        presenter = new SearchItemPresenter(this, searchType);

        plRootView.loadWait();
        initHeadViewLayout();
        presenter.init(searchKey);
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
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.init(searchKey);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getQueue().cancelAll(SearchItemPresenter.class.getName() + searchType);
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh(List data) {

        switch (searchType) {
            case "main":
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case "point":
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case "news":
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case "video":
                if (data == null || data.size() == 0) {
                } else {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(videoAdapter);
                    } else {
                        videoAdapter.setData(disposeData(data));
                    }
                    videoAdapter.setSearchKey(searchKey);
                }
                break;
            case "writer":
                if (data == null || data.size() == 0) {
                } else {
                    if (columnistAdapter == null) {
                        columnistAdapter = new ColumnistAdapter(disposeData(data), getContext());
                        plContent.setAdapter(columnistAdapter);
                    } else {
                        columnistAdapter.setData(disposeData(data));
                    }
                    columnistAdapter.setSearchKey(searchKey);
                }

                break;
            case "blog":
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case "quote":
                if (data == null || data.size() == 0) {
                } else {
                    if (quoteAdapter2 == null) {
                        quoteAdapter2 = new QuoteAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.setData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                }
                break;
        }

        plContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 200);
    }

    public void loadMore(List data) {

        switch (searchType) {
            case "main":
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.addData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case "point":
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.addData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case "news":
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.addData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case "video":
                if (data == null || data.size() == 0) {
                } else {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(videoAdapter);
                    } else {
                        videoAdapter.addData(disposeData(data));
                    }
                    videoAdapter.setSearchKey(searchKey);
                }
                break;
            case "writer":
                if (data == null || data.size() == 0) {
                } else {
                    if (columnistAdapter == null) {
                        columnistAdapter = new ColumnistAdapter(disposeData(data), getContext());
                        plContent.setAdapter(columnistAdapter);
                    } else {
                        columnistAdapter.addData(disposeData(data));
                    }
                    columnistAdapter.setSearchKey(searchKey);
                }

                break;
            case "blog":
                if (data == null || data.size() == 0) {
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.addData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                }
                break;
            case "quote":
                if (data == null || data.size() == 0) {
                } else {
                    if (quoteAdapter2 == null) {
                        quoteAdapter2 = new QuoteAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.addData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                }
                break;
        }

        plContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 200);
    }

    /**
     * 初始化头部布局
     */
    public void initHeadViewLayout() {
        if (homeHeadView == null) {
            AbsListView.LayoutParams headLayoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);

            homeHeadView = new LinearLayout(getContext());
            homeHeadView.setOrientation(LinearLayout.VERTICAL);
            homeHeadView.setLayoutParams(headLayoutParams);
        } else {
            homeHeadView.removeAllViews();
        }
    }

    /**
     * 添加行情布局
     *
     * @param data
     */
    public void addQuoteView(final List<QuoteItemJson> data) {

        if (data == null || data.size() == 0) return;
        viewQuote = LayoutInflater.from(getContext()).inflate(R.layout.head_search, null, false);
        tvTitleQuote = (TextView) viewQuote.findViewById(R.id.tv_title);
        tvTitleViewpoint = (TextView) viewQuote.findViewById(R.id.tv_title2);
        vLine1 = viewQuote.findViewById(R.id.v_line1);
        rvContentQuote = (ListView) viewQuote.findViewById(R.id.rv_content);
        tvMoreQuote = (TextView) viewQuote.findViewById(R.id.tv_more);
        vLine2 = viewQuote.findViewById(R.id.v_line2);
        vLine3 = viewQuote.findViewById(R.id.v_line3);

        quoteAdapter = new QuoteAdapter(getContext(), data);
        quoteAdapter.setSearchKey(searchKey);
        rvContentQuote.setAdapter(quoteAdapter);
        rvContentQuote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuoteItemJson quoteItemJson = data.get(position);

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        homeHeadView.addView(viewQuote, params);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (quoteAdapter != null) quoteAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        if (eventBus != null) {
            if (eventBus.fromCode == EventBusClass.EVENT_SEARCH) {
                searchKey = (String) eventBus.intentObj;
                plRootView.loadWait();
                presenter.init(searchKey);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void initQuote(QuoteJson quoteJson) {
        homeHeadView.removeAllViews();
        addQuoteView(quoteJson.getData());
        String is_more = quoteJson.getIs_more();
        if (is_more != null && is_more.equals("0")) {
            tvMoreQuote.setVisibility(View.VISIBLE);
        } else {
            tvMoreQuote.setVisibility(View.GONE);
        }
    }


    public void init(List data) {
        switch (searchType) {
            case "main":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    plContent.getRefreshableView().addHeaderView(homeHeadView);

                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case "point":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointAdapter(disposeData(data), getContext());
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case "news":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case "video":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (videoAdapter == null) {
                        videoAdapter = new VideoSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(videoAdapter);
                    } else {
                        videoAdapter.setData(disposeData(data));
                    }
                    videoAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case "writer":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (columnistAdapter == null) {
                        columnistAdapter = new ColumnistAdapter(disposeData(data), getContext());
                        plContent.setAdapter(columnistAdapter);
                    } else {
                        columnistAdapter.setData(disposeData(data));
                    }
                    columnistAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }

                break;
            case "blog":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(disposeData(data));
                    }
                    newsAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case "quote":
                if (data == null || data.size() == 0) {
                    plRootView.loadEmptyData();
                } else {
                    if (quoteAdapter2 == null) {
                        quoteAdapter2 = new QuoteAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.setData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
        }
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
            switch (searchType) {
                case "main":
                    ViewPointTradeBean viewpoint = (ViewPointTradeBean) data.get(lastPosition);
                    presenter.setLastId(viewpoint.o_id);
                    break;
                case "point":
                    ViewPointTradeBean viewpoint2 = (ViewPointTradeBean) data.get(lastPosition);
                    presenter.setLastId(viewpoint2.o_id);
                    break;
                case "news":
                    NewsJson newsJson = (NewsJson) data.get(lastPosition);
                    presenter.setLastId(newsJson.getO_id());
                    break;
                case "video":
                    VideoListJson video = (VideoListJson) data.get(lastPosition);
                    presenter.setLastId(video.getId());
                    break;
                case "writer":
                    ColumnistListJson authorNewsJson = (ColumnistListJson) data.get(lastPosition);
                    presenter.setLastId(authorNewsJson.getId());
                    break;
                case "blog":
                    NewsJson newsJson1 = (NewsJson) data.get(lastPosition);
                    presenter.setLastId(newsJson1.getO_id());
                    break;
                case "quote":
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
}
