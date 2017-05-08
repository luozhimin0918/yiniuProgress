package com.jyh.kxt.main.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.utils.BrowerHistoryUtils;
import com.jyh.kxt.index.ui.WebActivity;
import com.jyh.kxt.main.adapter.BtnAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.index.json.HomeHeaderJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.activity.NewsContentActivity;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.viewpager.BannerLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻item
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsItemPresenter extends BasePresenter {

    @BindObject
    NewsItemFragment newsItemFragment;

    private boolean isMain;//是否为首页

    private List<SlideJson> slides;//幻灯片
    private AdJson ads;//广告
    private List<SlideJson> shortcuts;//按钮
    private List<QuotesJson> quotes;//行情
    private List<NewsJson> news;
    private ArrayList<String> list;
    private NewsAdapter newsAdapter;
    private BannerLayout carouseView;
    private RequestQueue queue;

    private String lastId = "";
    private VolleyRequest request;
    private String code;
    private boolean isMore;


    public NewsItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public void init(Bundle arguments) {

        newsItemFragment.plRootView.loadWait();
        queue = newsItemFragment.getQueue();
        request = new VolleyRequest(mContext, queue);
        code = arguments.getString(IntentConstant.CODE);
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

                    checkNews(newsJsons);

                    news = newsJsons;

                    newsAdapter = new NewsAdapter(mContext, newsJsons);
                    newsItemFragment.plvContent.setAdapter(newsAdapter);
                    newsItemFragment.plRootView.loadOver();
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
        NewsJson newsJson = news.get(position);

        Intent intent = null;
        if (TextUtils.isEmpty(newsJson.getHref())) {
            intent = new Intent(mContext, NewsContentActivity.class);
            intent.putExtra(IntentConstant.O_ID, newsJson.getO_id());
        } else {
            intent = new Intent(mContext, WebActivity.class);
            intent.putExtra(IntentConstant.WEBURL, newsJson.getHref());
        }

        mContext.startActivity(intent);

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
        news = arguments.getParcelableArrayList(IntentConstant.NEWS_NEWS);
        list = arguments.getStringArrayList(IntentConstant.NEWS_LIST);

        initMain();
    }

    /**
     * 初始化首页布局
     */
    private void initMain() {
        checkNews(news);

        newsAdapter = new NewsAdapter(mContext, news);
        newsItemFragment.plvContent.setAdapter(newsAdapter);

        newsItemFragment.plvContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClickEvent(position - 2, view, parent);
            }
        });

        initHeadViewLayout();

        //头部排序
        if (list != null)
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
                        break;
                }
            }
        if (homeHeadView != null)
            newsItemFragment.plvContent.getRefreshableView().removeHeaderView(homeHeadView);
        newsItemFragment.plvContent.getRefreshableView().addHeaderView(homeHeadView);
        newsItemFragment.plRootView.loadOver();
    }


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

        int currentItem = 0;
        if (carouseView != null) {
            currentItem = carouseView.getViewPager().getCurrentItem();
        }

        int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_slide);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        carouseView = (BannerLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_slide, null);
        carouseView.setLayoutParams(params);

        List<String> carouseList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < carouselList.size(); i++) {
            SlideJson slideJson = carouselList.get(i);
            String carouselItem = slideJson.getPicture();
            carouseList.add(HttpConstant.IMG_URL + carouselItem);
            titles.add(slideJson.getTitile());
        }
        carouseView.setViewUrls(carouseList, titles, currentItem);
        homeHeadView.addView(carouseView);

        carouseView.setOnBannerItemClickListener(new BannerLayout.OnBannerItemClickListener() {
            @Override
            public void onItemClick(int position) {

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
        int carouselHeight = (int) mContext.getResources().getDimension(R.dimen.index_btn_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                carouselHeight);
        recyclerView.setLayoutParams(params);

        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        manager.setOrientation(GridLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(new BtnAdapter(shortcuts, mContext));

        homeHeadView.addView(recyclerView);

        addLineView();
    }

    /**
     * 添加行情
     */
    public void addQuotes() {

    }

    /**
     * 添加广告
     */
    public void addAD() {
        LinearLayout adView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.news_header_ad, null);

        ImageView iv_ad = (ImageView) adView.findViewById(R.id.iv_ad);

        LinearLayout ll_ad = (LinearLayout) adView.findViewById(R.id.ll_ad);

        SlideJson[] textAds = ads.getText_ad();
        int size = textAds.length;

        boolean isShowTextAd = false;

        for (int i = 0; i < size; i++) {
            TextView tvAd = new TextView(mContext);

            String title = textAds[i].getTitile();

            isShowTextAd = isShowTextAd || !TextUtils.isEmpty(title);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                    .WRAP_CONTENT);

            tvAd.setText(title);
            tvAd.setTextSize(16);
            tvAd.setTextColor(ContextCompat.getColor(mContext, R.color.font_color2));

            tvAd.setLayoutParams(params);

            ll_ad.addView(tvAd);
        }

        if (isShowTextAd)
            ll_ad.setVisibility(View.VISIBLE);
        else
            ll_ad.setVisibility(View.GONE);

        Glide.with(mContext).load(ads.getPic_ad().getPicture()).error(R.mipmap.ico_def_load).placeholder(R.mipmap.ico_def_load).into(iv_ad);

        homeHeadView.addView(adView);

        addLineView();
    }

    /**
     * 添加分割线
     */
    public void addLineView() {
        View view = new View(mContext);
        int lineHeight = (int) mContext.getResources().getDimension(R.dimen.line_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                lineHeight);
        view.setBackgroundResource(R.color.line_color2);

        view.setLayoutParams(params);

        homeHeadView.addView(view);
    }

    /**
     * 刷新
     *
     * @param refreshView
     */
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        lastId = "";
        if (isMain) {
            request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<HomeHeaderJson>>() {

                @Override
                protected void onResponse(List<HomeHeaderJson> newsHomeHeaderJsons) {

                    ArrayList<String> list = new ArrayList<>();

                    for (HomeHeaderJson headerJson : newsHomeHeaderJsons) {
                        switch (headerJson.getType()) {
                            case VarConstant.NEWS_SLIDE:
                                JSONArray slideArray = (JSONArray) headerJson.getData();
                                if (slideArray == null) break;
                                slides = JSON.parseArray(slideArray.toString(), SlideJson.class);
                                if (slides.size() > 0)
                                    list.add(VarConstant.NEWS_SLIDE);
                                break;
                            case VarConstant.NEWS_SHORTCUT:
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcuts = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcuts.size() > 0)
                                    list.add(VarConstant.NEWS_SHORTCUT);
                                break;
                            case VarConstant.NEWS_LIST:
                                JSONArray newsArray = (JSONArray) headerJson.getData();
                                if (newsArray == null) break;
                                news = JSON.parseArray(newsArray.toString(), NewsJson.class);
                                break;
                            case VarConstant.NEWS_QUOTES:
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), QuotesJson.class);
                                if (quotes.size() > 0)
                                    list.add(VarConstant.NEWS_QUOTES);
                                break;
                            case VarConstant.NEWS_AD:
                                com.alibaba.fastjson.JSONObject adObj = (com.alibaba.fastjson.JSONObject) headerJson.getData();
                                if (adObj == null) break;

                                SlideJson ad_img = adObj.getObject("pic_ad", SlideJson.class);

                                List<SlideJson> ad_text_list = JSON.parseArray(adObj.getJSONArray("text_ad").toString(), SlideJson
                                        .class);
                                SlideJson[] ad_text = ad_text_list.toArray(new SlideJson[ad_text_list.size()]);

                                ads = new AdJson(ad_img, ad_text);
                                list.add(VarConstant.NEWS_AD);
                                break;
                        }
                    }
                    initMain();
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
                    checkNews(newsJsons);
                    news = newsJsons;
                    if (newsAdapter == null) {
                        newsAdapter = new NewsAdapter(mContext, newsJsons);
                        newsItemFragment.plvContent.setAdapter(newsAdapter);
                    } else {
                        newsAdapter.setData(newsJsons);
                    }
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

        if (isMore)
            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    checkNews(newsJsons);
                    newsAdapter.addData(newsJsons);
                    news.addAll(newsJsons);
                    refreshView.onRefreshComplete();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    refreshView.onRefreshComplete();
                }
            });
        else {
            refreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshView.onRefreshComplete();
                }
            }, 500);
        }
    }

    /**
     * 获取list请求接口
     */
    private String getUrl(String code) {
        String url = HttpConstant.NEWS_LIST;
        try {
            JSONObject object = request.getJsonParam();
            if (!TextUtils.isEmpty(code))
                object.put(VarConstant.HTTP_CODE, code);
            if (!TextUtils.isEmpty(lastId))
                object.put(VarConstant.HTTP_LASTID, lastId);
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
    private void checkNews(List<NewsJson> news) {
        if (news == null) {
            lastId = "";
            return;
        }
        int size = news.size();
        lastId = news.get(size - 1).getO_id();
        if (size > VarConstant.LIST_MAX_SIZE) {
            news.remove(size - 1);
            isMore = true;
        } else {
            isMore = false;
        }
    }
}
