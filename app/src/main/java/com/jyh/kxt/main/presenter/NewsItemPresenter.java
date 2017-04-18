package com.jyh.kxt.main.presenter;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.VarConstant;
import com.jyh.kxt.main.adapter.BtnAdapter;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.viewpager.BannerLayout;
import com.library.widget.window.ToastView;

import org.json.JSONObject;

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

    public NewsItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setMain(boolean isMain) {
        this.isMain = isMain;
    }

    public void init(Bundle arguments) {

        newsItemFragment.plRootView.loadWait();

        String code = arguments.getString(IntentConstant.CODE);
        if (isMain) {
            slides = arguments.getParcelableArrayList(IntentConstant.NEWS_SLIDE);
            shortcuts = arguments.getParcelableArrayList(IntentConstant.NEWS_SHORTCUTS);
            quotes = arguments.getParcelableArrayList(IntentConstant.NEWS_QUOTES);
            ads = arguments.getParcelable(IntentConstant.NEWS_ADS);
            news = arguments.getParcelableArrayList(IntentConstant.NEWS_NEWS);
            list = arguments.getStringArrayList(IntentConstant.NEWS_LIST);

            newsAdapter = new NewsAdapter(mContext, news);
            newsItemFragment.plvContent.setAdapter(newsAdapter);

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
            newsItemFragment.plvContent.getRefreshableView().addHeaderView(homeHeadView);
            newsItemFragment.plRootView.loadOver();
        } else {
            queue = newsItemFragment.getQueue();
            VolleyRequest request = new VolleyRequest(mContext, queue);
            request.doGet(getUrl(code), new HttpListener<List<NewsJson>>() {

                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    newsAdapter = new NewsAdapter(mContext, newsJsons);
                    newsItemFragment.plvContent.setAdapter(newsAdapter);
                    newsItemFragment.plRootView.loadOver();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    try {
                        newsItemFragment.plRootView.loadError();
                        ToastView.makeText3(mContext, mContext.getString(R.string.toast_error_load));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    /**
     * 获取list请求接口
     */
    private String getUrl(String code) {
        String url = HttpConstant.NEWS_LIST;
        try {
            JSONObject object = new JSONObject();
            object.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
            object.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);
            object.put(VarConstant.HTTP_CODE, code);
            url = url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(com.library.base.http.VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }


    private LinearLayout homeHeadView;

    public void initHeadViewLayout() {

        AbsListView.LayoutParams headLayoutParams = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT);

        homeHeadView = new LinearLayout(mContext);
        homeHeadView.setOrientation(LinearLayout.VERTICAL);
        homeHeadView.setLayoutParams(headLayoutParams);

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
        for (int i = 0; i < carouselList.size(); i++) {
            String carouselItem = carouselList.get(i).getPicture();
            carouseList.add(HttpConstant.IMG_URL + carouselItem);
        }
        carouseView.setViewUrls(carouseList, currentItem);
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
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    /**
     * 加载更多
     *
     * @param refreshView
     */
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }
}
