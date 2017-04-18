package com.jyh.kxt.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.constant.VarConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.json.AdJson;
import com.jyh.kxt.main.json.NewsHomeHeaderJson;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.Arrays;
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
    private RequestQueue queue;
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

        queue = newsFragment.getQueue();
        if (queue == null)
            queue = Volley.newRequestQueue(mContext);

        request = new VolleyRequest(mContext, queue);

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<List<NewsHomeHeaderJson>>() {

            @Override
            protected void onResponse(List<NewsHomeHeaderJson> newsHomeHeaderJsons) {

                List<NewsNavJson> newsNavs = null;
                List<SlideJson> slide = null;
                List<SlideJson> shortcut = null;
                List<QuotesJson> quotes = null;
                List<NewsJson> news = null;
                AdJson ad = null;

                ArrayList<String> list = new ArrayList<>();

                for (NewsHomeHeaderJson headerJson : newsHomeHeaderJsons) {
                    switch (headerJson.getType()) {
                        case VarConstant.NEWS_NAV:
                            JSONArray newsNavArray = (JSONArray) headerJson.getData();
                            if (newsNavArray == null)
                                break;
                            newsNavs = JSON.parseArray(newsNavArray.toString(), NewsNavJson.class);
                            break;
                        case VarConstant.NEWS_SLIDE:
                            JSONArray slideArray = (JSONArray) headerJson.getData();
                            if (slideArray == null) break;
                            slide = JSON.parseArray(slideArray.toString(), SlideJson.class);
                            if (slide.size() > 0)
                                list.add(VarConstant.NEWS_SLIDE);
                            break;
                        case VarConstant.NEWS_SHORTCUT:
                            JSONArray shortcutArray = (JSONArray) headerJson.getData();
                            if (shortcutArray == null) break;
                            shortcut = JSON.parseArray(shortcutArray.toString(), SlideJson.class);
                            if (shortcut.size() > 0)
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
                            JSONObject adObj = (JSONObject) headerJson.getData();
                            if (adObj == null) break;

                            SlideJson ad_img = adObj.getObject("pic_ad", SlideJson.class);

                            List<SlideJson> ad_text_list = JSON.parseArray(adObj.getJSONArray("text_ad").toString(), SlideJson
                                    .class);
                            SlideJson[] ad_text = ad_text_list.toArray(new SlideJson[ad_text_list.size()]);

                            ad = new AdJson(ad_img, ad_text);
                            list.add(VarConstant.NEWS_AD);
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
                    ToastView.makeText3(mContext, mContext.getString(R.string.toast_error_load));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
