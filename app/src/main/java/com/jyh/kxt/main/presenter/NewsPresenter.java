package com.jyh.kxt.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.database.DefaultDatabaseErrorHandler;
import android.support.v4.view.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.json.AdJson2;
import com.jyh.kxt.main.json.MainNewsContentJson;
import com.library.base.http.PreCacheHttpResponse;
import com.library.util.JsonUtil;
import com.jyh.kxt.index.json.TypeDataJson;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.market.bean.MarketItemBean;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;

import rx.internal.operators.OnSubscribeRange;

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

        HttpListener<List<TypeDataJson>> listHttpListener = new HttpListener<List<TypeDataJson>>() {
            @Override
            protected boolean onStart() {
                MainActivity mainActivity = (MainActivity) newsFragment.getActivity();
                int mActivityFrom = mainActivity.mActivityFrom;
                if (mActivityFrom == 1) {
                    onResponse(getCacheT());
                    return false;
                }
                return true;
            }

            @Override
            protected void onResponse(List<TypeDataJson> newsTypeDataJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<MarketItemBean> quotes = null;
                MainNewsContentJson news = null;
                AdJson ad = null;
                AdJson2 adSlide = null;
                AdJson adBtn = null;

                ArrayList<String> list = new ArrayList<>();

                for (TypeDataJson headerJson : newsTypeDataJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            try {
                                JSONArray newsNavArray = (JSONArray) headerJson.getData();
                                if (newsNavArray == null) {
                                    break;
                                }
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
                                if (slide.size() > 0) {
                                    list.add(VarConstant.NEWS_SLIDE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            try {
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcut.size() > 0) {
                                    list.add(VarConstant.NEWS_SHORTCUT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_LIST:
                            try {
                                news = JSON.parseObject(JSON.toJSONString(headerJson), MainNewsContentJson.class);
                            } catch (Exception e) {
                                news = new MainNewsContentJson();
                                MainNewsContentJson.DataBean dataBean = new MainNewsContentJson.DataBean();
                                news.setType("news");
                                dataBean.setData(JSON.parseArray(JSON.toJSONString(headerJson.getData()), NewsJson.class));
                                news.setData(dataBean);
                            }

                            break;
                        case VarConstant.NEWS_QUOTES:
                            try {
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), MarketItemBean.class);
                                if (quotes.size() > 0) {
                                    list.add(VarConstant.NEWS_QUOTES);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD:
                            try {
                                ad = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD_SLIDE:
                            try {
                                adSlide = JSONObject.parseObject(headerJson.getData().toString(), AdJson2.class);
                                list.add(VarConstant.NEWS_AD_SLIDE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD_SHORTCUT:
                            try {
                                adBtn = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD_SHORTCUT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        default:
                            break;
                    }
                }
                newsFragment.initView(newsNavs, slide, shortcut, quotes, ad, adSlide, adBtn, news, list);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                List<TypeDataJson> cacheT = getCacheT();
                if (cacheT != null) {
                    onResponse(cacheT);
                } else {
                    try {
                        newsFragment.plRootView.loadError();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Object cacheData = PreCacheHttpResponse.getInstance().getCacheData(HttpConstant.INDEX_MAIN);
        if (cacheData != null) {
            try {
                List<TypeDataJson> typeDataList = (List<TypeDataJson>) cacheData;
                listHttpListener.onPreCacheResponse(typeDataList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            request.doGet(HttpConstant.INDEX_MAIN, listHttpListener);
        }
    }

    /**
     * 重新加载
     */
    public void reLoad() {
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
        }

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<TypeDataJson>>() {

            @Override
            protected void onResponse(List<TypeDataJson> newsTypeDataJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<MarketItemBean> quotes = null;
                MainNewsContentJson news = null;
                AdJson ad = null;
                AdJson2 adSlide = null;
                AdJson adBtn = null;

                ArrayList<String> list = new ArrayList<>();

                for (TypeDataJson headerJson : newsTypeDataJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            try {
                                JSONArray newsNavArray = (JSONArray) headerJson.getData();
                                if (newsNavArray == null) {
                                    break;
                                }
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
                                if (slide.size() > 0) {
                                    list.add(VarConstant.NEWS_SLIDE);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            try {
                                JSONArray shortcutArray = (JSONArray) headerJson.getData();
                                if (shortcutArray == null) break;
                                shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                                if (shortcut.size() > 0) {
                                    list.add(VarConstant.NEWS_SHORTCUT);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_LIST:
                            try {
                                news = JSON.parseObject(JSON.toJSONString(headerJson), MainNewsContentJson.class);
                            } catch (Exception e) {
                                news = new MainNewsContentJson();
                                MainNewsContentJson.DataBean dataBean = new MainNewsContentJson.DataBean();
                                news.setType("news");
                                dataBean.setData(JSON.parseArray(JSON.toJSONString(headerJson.getData()), NewsJson.class));
                                news.setData(dataBean);
                            }

                            break;
                        case VarConstant.NEWS_QUOTES:
                            try {
                                JSONArray quotesArray = (JSONArray) headerJson.getData();
                                if (quotesArray == null) break;
                                quotes = JSON.parseArray(quotesArray.toString(), MarketItemBean.class);
                                if (quotes.size() > 0) {
                                    list.add(VarConstant.NEWS_QUOTES);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD:
                            try {
                                ad = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD_SHORTCUT:
                            try {
                                adBtn = JSONObject.parseObject(headerJson.getData().toString(), AdJson.class);
                                list.add(VarConstant.NEWS_AD_SHORTCUT);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case VarConstant.NEWS_AD_SLIDE:
                            try {
                                adSlide = JSONObject.parseObject(headerJson.getData().toString(), AdJson2.class);
                                list.add(VarConstant.NEWS_AD_SLIDE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
                newsFragment.initView(newsNavs, slide, shortcut, quotes, ad, adSlide, adBtn, news, list);
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
