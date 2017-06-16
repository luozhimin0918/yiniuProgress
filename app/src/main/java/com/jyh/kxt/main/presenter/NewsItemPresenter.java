package com.jyh.kxt.main.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RollDotViewPager;
import com.jyh.kxt.base.custom.RollViewPager;
import com.jyh.kxt.base.impl.OnSocketTextMessage;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.base.utils.JumpUtils;
import com.jyh.kxt.base.utils.MarketConnectUtil;
import com.jyh.kxt.base.utils.MarketUtil;
import com.jyh.kxt.base.widget.night.heple.SkinnableTextView;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.adapter.BtnAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;
import com.jyh.kxt.market.adapter.MarketGridAdapter;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.util.SystemUtil;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.viewpager.BannerLayout;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemPresenter extends BasePresenter implements OnSocketTextMessage {

    @BindObject
    NewsItemFragment newsItemFragment;

    private boolean isMain;//是否为首页

    private List<SlideJson> slides;//幻灯片
    private AdJson ads;//广告
    private List<SlideJson> shortcuts;//按钮
    private List<MarketItemBean> quotes;//行情
    private ArrayList<String> list;
    public NewsAdapter newsAdapter;
    private BannerLayout carouseView;
    private RequestQueue queue;

    private String lastId = "";
    private VolleyRequest request;
    private String code;
    private boolean isMore;
    private RollDotViewPager mRollDotViewPager;
    private BtnAdapter btnAdapter;
    private ImageView iv_ad;
    private TextView tvTitle;


    public NewsItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public void init(Bundle arguments) {

        newsItemFragment.plRootView.loadWait();
        queue = newsItemFragment.getQueue();
        code = arguments.getString(IntentConstant.CODE);
        if (request == null) {
            request = new VolleyRequest(mContext, queue);
            request.setTag(code);
        }
        if (isMain) {
            ininMain(arguments);
        } else {

            newsItemFragment.plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemClickEvent(position - 1, view, parent);
                }
            });

            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {

                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    List<NewsJson> news = checkNews(newsJsons);
                    if (news == null) {
                        newsItemFragment.plRootView.loadEmptyData();
                    } else {
                        newsAdapter = new NewsAdapter(mContext, news);
                        newsItemFragment.plvContent.setAdapter(newsAdapter);
                        newsItemFragment.plRootView.loadOver();
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    try {
                        newsItemFragment.plRootView.loadError();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    private void itemClickEvent(int position, View view, AdapterView<?> parent) {
        NewsJson newsJson = newsAdapter.getData().get(position);
        JumpUtils.jump((MainActivity) mContext, newsJson.getO_class(), newsJson.getO_action(), newsJson.getO_id(),
                newsJson.getHref());
        //保存浏览记录
        BrowerHistoryUtils.save(mContext, newsJson);

        //单条刷新,改变浏览状态
        newsAdapter.getView(position, view, parent);
    }

    /**
     * 初始化首页布局
     *
     * @param arguments
     */
    private void ininMain(Bundle arguments) {
        slides = arguments.getParcelableArrayList(IntentConstant.NEWS_SLIDE);
        shortcuts = arguments.getParcelableArrayList(IntentConstant.NEWS_SHORTCUTS);
        quotes = arguments.getParcelableArrayList(IntentConstant.NEWS_QUOTES);
        ads = arguments.getParcelable(IntentConstant.NEWS_ADS);

        ArrayList<NewsJson> parcelableArrayList = arguments.getParcelableArrayList(IntentConstant.NEWS_NEWS);
        list = arguments.getStringArrayList(IntentConstant.NEWS_LIST);

        initMain(parcelableArrayList);
    }

    /**
     * 初始化首页布局
     *
     * @param data
     */
    private void initMain(List<NewsJson> data) {

        List<NewsJson> newsJsons = checkNews(data);
        if (newsJsons == null) {
            newsItemFragment.plRootView.loadEmptyData();
        } else {
            if (newsAdapter == null) {
                newsAdapter = new NewsAdapter(mContext, newsJsons);
                newsItemFragment.plvContent.setAdapter(newsAdapter);
            } else {
                newsAdapter.setData(newsJsons);
            }
            newsItemFragment.plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    itemClickEvent(position - 2, view, parent);
                }
            });

            initHeadViewLayout();

            //头部排序
            if (list != null) {
                for (String type : list) {
                    switch (type) {
                        case VarConstant.NEWS_AD:
                            addAD();
                            break;
                        case VarConstant.NEWS_SLIDE:
                            addCarouselView(slides);
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            addBtn(shortcuts);
                            break;
                        case VarConstant.NEWS_QUOTES:
                            addQuotes();
                            break;
                    }
                }
            }
            if (homeHeadView != null) {
                newsItemFragment.plvContent.getRefreshableView().removeHeaderView(homeHeadView);
            }
            LinearLayout layout = new LinearLayout(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);
            tvTitle = new TextView(mContext);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                    .LayoutParams.WRAP_CONTENT);
            tvTitle.setText("财经要闻");
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.content_font_size));
            TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvTitle, R.mipmap.icon_video_line, 0, 0, 0);
            tvTitle.setGravity(Gravity.CENTER_VERTICAL);
            tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color60));
            tvTitle.setLayoutParams(tvParams);
            layout.addView(tvTitle);
            layout.setLayoutParams(params);
            homeHeadView.addView(layout);
            newsItemFragment.plvContent.getRefreshableView().addHeaderView(homeHeadView);
            newsItemFragment.plRootView.loadOver();
        }

    }

    private List<SkinnableTextView> mAdTextViewList;
    private LinearLayout homeHeadView;

    /**
     * 初始化头部布局
     */
    public void initHeadViewLayout() {
        if (homeHeadView == null) {
            AbsListView.LayoutParams headLayoutParams = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);

            homeHeadView = new LinearLayout(mContext);
            homeHeadView.setOrientation(LinearLayout.VERTICAL);
            homeHeadView.setLayoutParams(headLayoutParams);
        } else {
            homeHeadView.removeAllViews();
        }
    }

    /**
     * 添加轮播图
     *
     * @param carouselList
     */
    public void addCarouselView(final List<SlideJson> carouselList) {

        if (carouselList == null) return;

        int currentItem = 0;
        if (carouseView != null) {
            currentItem = carouseView.getViewPager().getCurrentItem();
        }

        final int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_slide);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        carouseView = (BannerLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_slide, null);
        carouseView.setLayoutParams(params);

        final List<String> carouseList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < carouselList.size(); i++) {
            SlideJson slideJson = carouselList.get(i);
            String carouselItem = slideJson.getPicture();
            carouseList.add(HttpConstant.IMG_URL + carouselItem);
            titles.add(slideJson.getName());
        }
        carouseView.setViewUrls(carouseList, titles, currentItem);
        homeHeadView.addView(carouseView);

        carouseView.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SlideJson slideJson = carouselList.get(position);
                JumpUtils.jump((BaseActivity) mContext, slideJson.getO_class(), slideJson.getO_action(), slideJson
                        .getO_id(), slideJson
                        .getHref());
            }
        });


    }

    /**
     * 添加按钮
     *
     * @param shortcuts
     */
    public void addBtn(List<SlideJson> shortcuts) {

        RecyclerView recyclerView = new RecyclerView(mContext);

        int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_btn_height) / 2;
        if (shortcuts.size() > 4) {
            carouselHeight = carouselHeight * 2;
        }

        GridLayoutManager manager = new GridLayoutManager(mContext, 4) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        manager.setOrientation(GridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        btnAdapter = new BtnAdapter(shortcuts, mContext);
        recyclerView.setAdapter(btnAdapter);

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        recyclerView.setLayoutParams(params);

        homeHeadView.addView(recyclerView);

        addLineView();
    }

    /**
     * 添加行情
     */
    private JSONArray marketCodeList = new JSONArray();
    private HashMap<String, MarketItemBean> marketMap = new HashMap<>();

    public void addQuotes() {

        try {
            List<MarketItemBean> marketEditOption = MarketUtil.getMarketEditOption(mContext);
            if (marketEditOption.size() > 6) {
                List<MarketItemBean> subMarketItem = marketEditOption.subList(0, 6);
                quotes.clear();
                quotes.addAll(subMarketItem);
            } else {
                int quotesSub = 6 - marketEditOption.size();
                List<MarketItemBean> subMarketItem = marketEditOption.subList(0, marketEditOption.size());
                List<MarketItemBean> subQuotesItem = new ArrayList<>(quotes.subList(0, quotesSub));

                quotes.clear();

                quotes.addAll(subMarketItem);
                quotes.addAll(subQuotesItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        marketCodeList.clear();
        marketMap.clear();

        for (MarketItemBean marketItemBean : quotes) {
            marketCodeList.add(marketItemBean.getCode());
            marketMap.put(marketItemBean.getCode(), marketItemBean);

            marketItemBean.setChange(MarketUtil.replacePositive(marketItemBean.getChange()));
            marketItemBean.setRange(MarketUtil.replacePositive(marketItemBean.getRange()));
        }


        mRollDotViewPager = new RollDotViewPager(mContext);
        mRollDotViewPager.setShowPaddingLine(false);
        RollViewPager recommendView = mRollDotViewPager.getRollViewPager();
        recommendView
                .setGridMaxCount(3)
                .setDataList(quotes)
                .setGridViewItemData(new RollViewPager.GridViewItemData() {
                    @Override
                    public void itemData(List dataSubList, GridView gridView) {
                        MarketGridAdapter adapter = new MarketGridAdapter(mContext, dataSubList);
                        gridView.setAdapter(adapter);
                    }
                });
        mRollDotViewPager.build();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                SystemUtil.dp2px(mContext, 115));

        mRollDotViewPager.setLayoutParams(lp);
        homeHeadView.addView(mRollDotViewPager);

        addLineView();

        sendSocketParams();
        MarketUtil.saveMarketEditOption(mContext, quotes, 0);
    }

    public void sendSocketParams() {
        MarketConnectUtil.getInstance().sendSocketParams(
                iBaseView,
                marketCodeList,
                NewsItemPresenter.this);
    }

    /**
     * 添加广告
     */
    public void addAD() {
        LinearLayout adView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_ad, null);
        iv_ad = (ImageView) adView.findViewById(R.id.iv_ad);

        try {
            final AdJson.AdItemJson mPicAd = ads.getPic_ad();

            String picture = mPicAd.getPicture();
            if (RegexValidateUtil.isEmpty(picture)) {
                iv_ad.setVisibility(View.GONE);
            } else {
                iv_ad.setVisibility(View.VISIBLE);
            }
            Glide.with(mContext).load(picture).error(R.mipmap.icon_def_news)
                    .placeholder(R.mipmap.icon_def_news).into(iv_ad);

            homeHeadView.addView(adView);

            adView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, WebActivity.class);
                    intent.putExtra(IntentConstant.NAME, "广告");
                    intent.putExtra(IntentConstant.WEBURL, mPicAd.getHref());
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mAdTextViewList = new ArrayList<>();
            List<AdJson.AdItemJson> mTextAd = ads.getText_ad();

            int itemHeight = SystemUtil.dp2px(mContext, 30);
            int itemLeftPadding = SystemUtil.dp2px(mContext, 5);

            int adTextColor = ContextCompat.getColor(mContext, R.color.font_color5);

            for (final AdJson.AdItemJson adItemJson : mTextAd) {

                SkinnableTextView mAdTextView = new SkinnableTextView(mContext);
                mAdTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        itemHeight));
                mAdTextView.setGravity(Gravity.CENTER_VERTICAL);

                mAdTextView.setTextColor(adTextColor);
                mAdTextView.setPadding(itemLeftPadding, 0, 0, 0);
                mAdTextView.setText(" • " + adItemJson.getTitle());
                homeHeadView.addView(mAdTextView);
                mAdTextViewList.add(mAdTextView);
                mAdTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, WebActivity.class);
                        intent.putExtra(IntentConstant.NAME, adItemJson.getTitle());
                        intent.putExtra(IntentConstant.WEBURL, adItemJson.getHref());
                        mContext.startActivity(intent);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addLineView();
    }

    /**
     * 添加分割线
     */
    public void addLineView() {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_line, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)
                mContext.getResources()
                        .getDimension(R.dimen.line_height));
        inflate.setLayoutParams(params);
        inflate.setTag("lineTag");
        homeHeadView.addView(inflate);
    }

    /**
     * 刷新
     *
     * @param refreshView
     */
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        lastId = "";
        if (isMain) {
            request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<TypeDataJson>>() {

                @Override
                protected void onResponse(List<TypeDataJson> newsTypeDataJsons) {

                    ArrayList<String> list = new ArrayList<>();
                    List<NewsJson> data = new ArrayList<NewsJson>();
                    for (TypeDataJson headerJson : newsTypeDataJsons) {
                        switch (headerJson.getType()) {
                            case VarConstant.NEWS_SLIDE:
                                JSONArray slideArray = (JSONArray) headerJson.getData();
                                if (slideArray == null) break;
                                slides = JSON.parseArray(slideArray.toString(), SlideJson.class);
                                if (slides.size() > 0) {
                                    list.add(VarConstant.NEWS_SLIDE);
                                }
                                break;
                            case VarConstant.NEWS_SHORTCUT:
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcuts = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcuts.size() > 0) {
                                    list.add(VarConstant.NEWS_SHORTCUT);
                                }
                                break;
                            case VarConstant.NEWS_LIST:
                                JSONArray newsArray = (JSONArray) headerJson.getData();
                                if (newsArray == null) break;
                                data = JSON.parseArray(newsArray.toString(), NewsJson.class);
                                break;
                            case VarConstant.NEWS_QUOTES:
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), MarketItemBean.class);
                                if (quotes.size() > 0) {
                                    list.add(VarConstant.NEWS_QUOTES);
                                }
                                break;
                            case VarConstant.NEWS_AD:
                                ads = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD);
                                break;
                        }
                    }
                    initMain(data);
                    refreshView.onRefreshComplete();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    refreshView.onRefreshComplete();
                }
            });
        } else {
            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    newsAdapter.setData(checkNews(newsJsons));
                    refreshView.onRefreshComplete();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    refreshView.onRefreshComplete();
                }
            });
        }
    }

    /**
     * 加载更多
     *
     * @param refreshView
     */
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {

        if (isMore) {
            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    newsAdapter.addData(checkNews(newsJsons));
                    refreshView.onRefreshComplete();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    refreshView.onRefreshComplete();
                }
            });
        } else {
            refreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshView.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 200);
        }
    }

    /**
     * 获取list请求接口
     */
    private String getUrl(String code) {
        String url = HttpConstant.NEWS_LIST;
        try {
            JSONObject object = request.getJsonParam();
            if (!TextUtils.isEmpty(code)) {
                object.put(VarConstant.HTTP_CODE, code);
            }
            if (!TextUtils.isEmpty(lastId)) {
                object.put(VarConstant.HTTP_LASTID, lastId);
            }
            url = url + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 处理要闻列表,更新lastid
     *
     * @param news
     */
    private List<NewsJson> checkNews(List<NewsJson> news) {
        if (news == null || news.size() == 0) {
            return null;
        }
        List<NewsJson> newsJsons;
        if (news.size() > VarConstant.LIST_MAX_SIZE) {
            newsJsons = new ArrayList<>(news.subList(0, VarConstant.LIST_MAX_SIZE));
            lastId = news.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
            isMore = true;
        } else {
            newsJsons = new ArrayList<>(news);
            lastId = "";
            isMore = false;
        }
        return newsJsons;
    }

    /**
     * 错误后 重新加载
     */
    public void reLoad() {
        lastId = "";
        if (isMain) {
            request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<TypeDataJson>>() {

                @Override
                protected void onResponse(List<TypeDataJson> newsTypeDataJsons) {

                    ArrayList<String> list = new ArrayList<>();
                    List<NewsJson> data = new ArrayList<NewsJson>();
                    for (TypeDataJson headerJson : newsTypeDataJsons) {
                        switch (headerJson.getType()) {
                            case VarConstant.NEWS_SLIDE:
                                JSONArray slideArray = (JSONArray) headerJson.getData();
                                if (slideArray == null) break;
                                slides = JSON.parseArray(slideArray.toString(), SlideJson.class);
                                if (slides.size() > 0) {
                                    list.add(VarConstant.NEWS_SLIDE);
                                }
                                break;
                            case VarConstant.NEWS_SHORTCUT:
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcuts = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcuts.size() > 0) {
                                    list.add(VarConstant.NEWS_SHORTCUT);
                                }
                                break;
                            case VarConstant.NEWS_LIST:
                                JSONArray newsArray = (JSONArray) headerJson.getData();
                                if (newsArray == null) break;
                                data = JSON.parseArray(newsArray.toString(), NewsJson.class);
                                break;
                            case VarConstant.NEWS_QUOTES:
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), MarketItemBean.class);
                                if (quotes.size() > 0) {
                                    list.add(VarConstant.NEWS_QUOTES);
                                }
                                break;
                            case VarConstant.NEWS_AD:
                                ads = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD);
                                break;
                        }
                    }
                    initMain(data);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    newsItemFragment.plRootView.loadError();
                }
            });
        } else {
            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    List<NewsJson> news = checkNews(newsJsons);
                    if (news == null) {
                        newsItemFragment.plRootView.loadEmptyData();
                        return;
                    }
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(mContext, news);
                        newsItemFragment.plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(news);
                    }
                    newsItemFragment.plRootView.loadOver();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    newsItemFragment.plRootView.loadError();
                }
            });
        }
    }

    public void clearBrowerHistory() {
        try {
            newsAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 行情  Socket
     *
     * @param text
     */
    @Override
    public void onTextMessage(String text) {
        try {
            MarketUtil.mapToMarketBean(newsItemFragment.plRootView, 0, marketMap, text);
        } catch (Exception e) {
            try {
                List<String> jsonList = JSONArray.parseArray(text, String.class);
                for (String itemJson : jsonList) {
                    MarketUtil.mapToMarketBean(newsItemFragment.plRootView, 0, marketMap, itemJson);
                }
            } catch (Exception e1) {
            }
        }
    }

    public void onChangeTheme() {
        try {
            if (mRollDotViewPager != null) {
                mRollDotViewPager.onChangeTheme();
            }
            if (btnAdapter != null) {
                btnAdapter.notifyDataSetChanged();
            }
            if (carouseView != null) {
                carouseView.onChangeTheme();
            }
            if (newsAdapter != null) {
                newsAdapter.notifyDataSetChanged();
            }
            if (mRollDotViewPager != null) {
                mRollDotViewPager.onChangeTheme();
            }

            if (homeHeadView == null) {
                return;
            }
            for (int i = 0; i < homeHeadView.getChildCount(); i++) {
                String tag = (String) homeHeadView.getChildAt(i).getTag();
                if ("lineTag".equals(tag)) {
                    int lineColor = ContextCompat.getColor(mContext, R.color.line_color2);
                    View lineView = homeHeadView.getChildAt(i);
                    lineView.setBackgroundColor(lineColor);
                }
            }
            if (mAdTextViewList != null) {
                for (SkinnableTextView skinnableTextView : mAdTextViewList) {
                    skinnableTextView.setTextColor(ContextCompat.getColor(mContext, R.color.font_color5));
                }
            }
            if (tvTitle != null) {
                TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(tvTitle, R.mipmap.icon_video_line, 0, 0, 0);
                tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.font_color60));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
