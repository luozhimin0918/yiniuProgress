package com.jyh.kxt.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.index.json.HomeHeaderJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:要闻
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/12.
 */

public class NewsPresenter extends BasePresenter {

    @BindObject()
    NewsFragment newsFragment;

    public int index = 0;
    private VolleyRequest request;

    public NewsPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void addOnPageChangeListener(ViewPager vpNewsList) {
        vpNewsList.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                index = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void more(String[] tabs) {
        Intent intent = new Intent(mContext, ClassifyActivity.class);
        intent.putExtra(IntentConstant.INDEX, index);
        intent.putExtra(IntentConstant.ACTIONNAV, tabs);
        ((Activity) mContext).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
    }

    public void init() {

        newsFragment.plRootView.loadWait();

        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<HomeHeaderJson>>() {

            @Override
            protected void onResponse(List<HomeHeaderJson> newsHomeHeaderJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<QuotesJson> quotes = null;
                List<NewsJson> news = null;
                AdJson ad = null;

                ArrayList<String> list = new ArrayList<>();

                for (HomeHeaderJson headerJson : newsHomeHeaderJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            try {
                                JSONArray newsNavArray = (JSONArray) headerJson.getData();
                                if (newsNavArray == null)
                                    break;
                                newsNavs = JSON.parseArray(newsNavArray.toString(), NewsNavJson.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SLIDE:
                            try {
                                JSONArray slideArray = (JSONArray) headerJson.getData();
                                if (slideArray == null) break;
                                slide = JSON.parseArray(slideArray.toString(), SlideJson.class);
                                if (slide.size() > 0)
                                    list.add(VarConstant.NEWS_SLIDE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            try {
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcut.size() > 0)
                                    list.add(VarConstant.NEWS_SHORTCUT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_LIST:
                            try {
                                JSONArray newsArray = (JSONArray) headerJson.getData();
                                if (newsArray == null) break;
                                news = JSON.parseArray(newsArray.toString(), NewsJson.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_QUOTES:
                            try {
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), QuotesJson.class);
                                if (quotes.size() > 0)
                                    list.add(VarConstant.NEWS_QUOTES);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD:
                            try {
                                JSONObject adObj = (JSONObject) headerJson.getData();
                                if (adObj == null) break;

                                SlideJson ad_img = adObj.getObject("pic_ad", SlideJson.class);

                                List<SlideJson> ad_text_list = JSON.parseArray(adObj.getJSONArray("text_ad").toString(), SlideJson
                                        .class);
                                SlideJson[] ad_text = ad_text_list.toArray(new SlideJson[ad_text_list.size()]);

                                ad = new AdJson(ad_img, ad_text);
                                list.add(VarConstant.NEWS_AD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
                newsFragment.initView(newsNavs, slide, shortcut, quotes, ad, news, list);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    newsFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 重新加载
     */
    public void reLoad() {
        if (request == null)
            request = new VolleyRequest(mContext, mQueue);

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<HomeHeaderJson>>() {

            @Override
            protected void onResponse(List<HomeHeaderJson> newsHomeHeaderJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<QuotesJson> quotes = null;
                List<NewsJson> news = null;
                AdJson ad = null;

                ArrayList<String> list = new ArrayList<>();

                for (HomeHeaderJson headerJson : newsHomeHeaderJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            try {
                                JSONArray newsNavArray = (JSONArray) headerJson.getData();
                                if (newsNavArray == null)
                                    break;
                                newsNavs = JSON.parseArray(newsNavArray.toString(), NewsNavJson.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SLIDE:
                            try {
                                JSONArray slideArray = (JSONArray) headerJson.getData();
                                if (slideArray == null) break;
                                slide = JSON.parseArray(slideArray.toString(), SlideJson.class);
                                if (slide.size() > 0)
                                    list.add(VarConstant.NEWS_SLIDE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            try {
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcut.size() > 0)
                                    list.add(VarConstant.NEWS_SHORTCUT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_LIST:
                            try {
                                JSONArray newsArray = (JSONArray) headerJson.getData();
                                if (newsArray == null) break;
                                news = JSON.parseArray(newsArray.toString(), NewsJson.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_QUOTES:
                            try {
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), QuotesJson.class);
                                if (quotes.size() > 0)
                                    list.add(VarConstant.NEWS_QUOTES);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD:
                            try {
                                JSONObject adObj = (JSONObject) headerJson.getData();
                                if (adObj == null) break;

                                SlideJson ad_img = adObj.getObject("pic_ad", SlideJson.class);

                                List<SlideJson> ad_text_list = JSON.parseArray(adObj.getJSONArray("text_ad").toString(), SlideJson
                                        .class);
                                SlideJson[] ad_text = ad_text_list.toArray(new SlideJson[ad_text_list.size()]);

                                ad = new AdJson(ad_img, ad_text);
                                list.add(VarConstant.NEWS_AD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
                newsFragment.initView(newsNavs, slide, shortcut, quotes, ad, news, list);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    newsFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
