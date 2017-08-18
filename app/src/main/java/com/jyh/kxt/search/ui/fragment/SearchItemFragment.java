package com.jyh.kxt.search.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.VideoDetailActivity;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.index.adapter.VideoSearchAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.jyh.kxt.market.ui.MarketDetailActivity;
import com.jyh.kxt.search.adapter.QuoteAdapter;
import com.jyh.kxt.search.json.PointJson;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.json.QuoteJson;
import com.jyh.kxt.search.presenter.SearchItemPresenter;
import com.jyh.kxt.search.util.AutoCompleteUtils;
import com.jyh.kxt.trading.adapter.ColumnistAdapter;
import com.jyh.kxt.trading.adapter.ViewpointSearchAdapter;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.jyh.kxt.trading.ui.AuthorActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.util.SystemUtil;
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
        .OnAfreshLoadListener, AdapterView.OnItemClickListener {

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
    private ViewpointSearchAdapter viewpointAdapter;
    private NewsAdapter newsAdapter;
    private VideoSearchAdapter videoAdapter;
    private ColumnistAdapter columnistAdapter;
    private QuoteAdapter quoteAdapter2;
    private TextView footView;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_search_list);

        plRootView.setOnAfreshLoadListener(this);
        plContent.setDividerNull();
        plContent.setOnRefreshListener(this);
        plContent.setOnItemClickListener(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            searchKey = arguments.getString(SEARCH_KEY);
            searchType = arguments.getString(SEARCH_TYPE);
        }

        if (searchType.equals(VarConstant.SEARCH_TYPE_MAIN)) {
            plContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        } else {
            plContent.setMode(PullToRefreshBase.Mode.BOTH);
        }

        presenter = new SearchItemPresenter(this, searchType);

        plRootView.loadWait();
        initHeadViewLayout();
        initFootView();
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
            case VarConstant.SEARCH_TYPE_MAIN:
                plContent.getRefreshableView().addHeaderView(homeHeadView);
                if (data == null || data.size() == 0) {
                } else {

                    List dataCopy = new ArrayList();
                    if (data != null && data.size() > 3) {
                        dataCopy.addAll(data.subList(0, 3));
                    } else {
                        dataCopy.add(data);
                    }

                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), dataCopy);
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(dataCopy);
                    }
                    AutoCompleteUtils.saveData(getContext(), dataCopy);
                    viewpointAdapter.setSearchKey(searchKey);
                }
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(viewpointAdapter);
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
                        plContent.setAdapter(newsAdapter);
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
                        plContent.setAdapter(videoAdapter);
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
                        plContent.setAdapter(columnistAdapter);
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
                        plContent.setAdapter(newsAdapter);
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
                        plContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.setData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                    AutoCompleteUtils.saveData(getContext(), data);
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
            case VarConstant.SEARCH_TYPE_MAIN:
//                if (data == null || data.size() == 0) {
//                } else {
//                    if (viewpointAdapter == null) {
//                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), disposeData(data));
//                        plContent.setAdapter(viewpointAdapter);
//                    } else {
//                        viewpointAdapter.addData(disposeData(data));
//                    }
//                    viewpointAdapter.setSearchKey(searchKey);
//                }
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                if (data == null || data.size() == 0) {
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(viewpointAdapter);
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
                        plContent.setAdapter(newsAdapter);
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
                        plContent.setAdapter(videoAdapter);
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
                        plContent.setAdapter(columnistAdapter);
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
                        plContent.setAdapter(newsAdapter);
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
                        plContent.setAdapter(quoteAdapter2);
                    } else {
                        quoteAdapter2.addData(disposeData(data));
                    }
                    quoteAdapter2.setSearchKey(searchKey);
                    AutoCompleteUtils.saveData(getContext(), data);
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

    public void initFootView() {
        footView = new TextView(getContext());
        footView.setText("查看更多 >");
        footView.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color8));
        footView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        footView.setGravity(Gravity.CENTER);
        footView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_SEARCH_TYPE, VarConstant.SEARCH_TYPE_VIEWPOINT));
            }
        });
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SystemUtil.dp2px(getContext()
                , 35));
        footView.setLayoutParams(params);
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
        AutoCompleteUtils.saveData(getContext(), data);
        quoteAdapter.setSearchKey(searchKey);
        rvContentQuote.setAdapter(quoteAdapter);
        fixListViewHeight(rvContentQuote);
        rvContentQuote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuoteItemJson quoteItemJson = data.get(position);
                MarketItemBean marketItemBean = new MarketItemBean();
                marketItemBean.setName(quoteItemJson.getName());
                marketItemBean.setCode(quoteItemJson.getCode());
                Intent intent = new Intent(getContext(), MarketDetailActivity.class);
                intent.putExtra(IntentConstant.MARKET, marketItemBean);
                startActivity(intent);
            }
        });
        tvMoreQuote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_SEARCH_TYPE, VarConstant.SEARCH_TYPE_QUOTE));
            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        if (homeHeadView == null) initHeadViewLayout();
        homeHeadView.addView(viewQuote, params);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (quoteAdapter != null) quoteAdapter.notifyDataSetChanged();
        if (viewpointAdapter != null) viewpointAdapter.notifyDataSetChanged();
        if (newsAdapter != null) newsAdapter.notifyDataSetChanged();
        if (videoAdapter != null) videoAdapter.notifyDataSetChanged();
        if (columnistAdapter != null) columnistAdapter.notifyDataSetChanged();
        if (quoteAdapter2 != null) quoteAdapter2.notifyDataSetChanged();

        if (tvTitleQuote != null) tvTitleQuote.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color6));
        if (tvTitleViewpoint != null) tvTitleViewpoint.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color6));
        if (vLine1 != null) vLine1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
        if (vLine2 != null) vLine2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
        if (vLine3 != null) vLine3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.line_color2));
        if (tvMoreQuote != null) tvMoreQuote.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color8));

        if (footView != null) footView.setTextColor(ContextCompat.getColor(getContext(), R.color.font_color8));
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
        if (homeHeadView != null) {
            homeHeadView.removeAllViews();
            plContent.getRefreshableView().removeHeaderView(homeHeadView);
        }
        addQuoteView(quoteJson.getData());
        String is_more = quoteJson.getIs_more();
        if (tvMoreQuote != null) {
            if (is_more != null && is_more.equals("1")) {
                tvMoreQuote.setVisibility(View.VISIBLE);
            } else {
                tvMoreQuote.setVisibility(View.GONE);
            }
        }
        AutoCompleteUtils.saveData(getContext(), quoteJson.getData());
    }


    public void initMain(PointJson viewpoints) {
        String is_more = viewpoints.getIs_more();
        if (is_more != null && is_more.equals("1")) {
            footView.setVisibility(View.VISIBLE);
        } else {
            footView.setVisibility(View.GONE);
        }
        init(viewpoints.getData());
        plContent.postDelayed(new Runnable() {
            @Override
            public void run() {
                plContent.onRefreshComplete();
            }
        }, 200);
    }

    public void init(List data) {
        switch (searchType) {
            case VarConstant.SEARCH_TYPE_MAIN:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
                    plRootView.loadEmptyData();
                } else {
                    plContent.getRefreshableView().addHeaderView(homeHeadView);

                    if (footView == null) initFootView();
                    plContent.getRefreshableView().addFooterView(footView);

                    List dataCopy = new ArrayList();
                    if (data.size() > 3) {
                        dataCopy.addAll(data.subList(0, 3));
                    } else {
                        dataCopy.addAll(data);
                    }

                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), dataCopy);
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(dataCopy);
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case VarConstant.SEARCH_TYPE_VIEWPOINT:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
                    plRootView.loadEmptyData();
                } else {
                    if (viewpointAdapter == null) {
                        viewpointAdapter = new ViewpointSearchAdapter(getContext(), disposeData(data));
                        plContent.setAdapter(viewpointAdapter);
                    } else {
                        viewpointAdapter.setData(disposeData(data));
                    }
                    viewpointAdapter.setSearchKey(searchKey);
                    plRootView.loadOver();
                }
                break;
            case VarConstant.SEARCH_TYPE_NEWS:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
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
            case VarConstant.SEARCH_TYPE_VIDEO:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
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
            case VarConstant.SEARCH_TYPE_COLUMNIST:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
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
            case VarConstant.SEARCH_TYPE_BLOG:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
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
            case VarConstant.SEARCH_TYPE_QUOTE:
                if (data == null || data.size() == 0) {
                    plRootView.setNullText(getString(R.string.error_search_null));
                    plRootView.setNullImgId(R.mipmap.icon_search_null);
                    plRootView.setNullTextColor(R.color.font_color8);
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
                    AutoCompleteUtils.saveData(getContext(), data);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!VarConstant.SEARCH_TYPE_MAIN.equals(searchType)) {
            int clickPosition = position - 1;
            switch (searchType) {
                case VarConstant.SEARCH_TYPE_QUOTE:
                    QuoteItemJson quote = quoteAdapter2.dataList.get(clickPosition);
                    Intent quoteAdapter = new Intent(getContext(), MarketDetailActivity.class);
                    MarketItemBean marketBean = new MarketItemBean();
                    marketBean.setCode(quote.getCode());
                    marketBean.setName(quote.getName());
                    quoteAdapter.putExtra(IntentConstant.MARKET, marketBean);
                    startActivity(quoteAdapter);
                    break;
                case VarConstant.SEARCH_TYPE_NEWS:
                    NewsJson newsJson = newsAdapter.dataList.get(clickPosition);
                    JumpUtils.jump((BaseActivity) getActivity(), newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(),
                            newsJson.getHref());
                    break;
                case VarConstant.SEARCH_TYPE_VIDEO:
                    VideoListJson video = videoAdapter.dataList.get(clickPosition);
                    Intent detailIntent = new Intent(getContext(), VideoDetailActivity.class);
                    detailIntent.putExtra(IntentConstant.O_ID, video.getId());
                    startActivity(detailIntent);
                    break;
                case VarConstant.SEARCH_TYPE_COLUMNIST:
                    ColumnistListJson columnist = columnistAdapter.dataList.get(clickPosition);
                    Intent columnistIntent = new Intent(getContext(), AuthorActivity.class);
                    columnistIntent.putExtra(IntentConstant.O_ID, columnist.getId());
                    startActivity(columnistIntent);
                    break;
                case VarConstant.SEARCH_TYPE_BLOG:
                    NewsJson news = newsAdapter.dataList.get(clickPosition);
                    JumpUtils.jump((BaseActivity) getActivity(), news.getO_class(), news.getO_action(), news.getO_id(),
                            news.getHref());
                    break;
            }
        }
    }

    public void fixListViewHeight(ListView listView) {

        // 如果没有设置数据适配器，则ListView没有子项，返回。

        ListAdapter listAdapter = listView.getAdapter();

        int totalHeight = 0;

        if (listAdapter == null) {

            return;

        }

        for (int index = 0, len = listAdapter.getCount(); index < len; index++) {

            View listViewItem = listAdapter.getView(index, null, listView);

            // 计算子项View 的宽高

            listViewItem.measure(0, 0);

            // 计算所有子项的高度和

            totalHeight += listViewItem.getMeasuredHeight();

        }


        ViewGroup.LayoutParams params = listView.getLayoutParams();

        // listView.getDividerHeight()获取子项间分隔符的高度

        // params.height设置ListView完全显示需要的高度

        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));

        listView.setLayoutParams(params);

    }

}
