package com.jyh.kxt.main.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.alibaba.fastjson.JSON;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.index.ui.ClassifyActivity;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.json.QuotesJson;
import com.jyh.kxt.main.json.SlideJson;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import org.json.JSONException;
import org.json.JSONObject;

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

    public void more() {
        Intent intent = new Intent(mContext, ClassifyActivity.class);
        intent.putExtra(IntentConstant.INDEX, index);
        ((Activity) mContext).startActivityForResult(intent, IntentConstant.REQUESTCODE1);
    }

    public void init() {
        queue = newsFragment.getQueue();
        if (queue == null)
            queue = Volley.newRequestQueue(mContext);

        request = new VolleyRequest(mContext, queue);

        request.doGet(HttpConstant.INDEX_MAIN, new HttpListener<JSONObject>() {
            @Override
            protected void onResponse(JSONObject json) {
                try {
                    String status = json.getString("status");
                    if (status.equals("1")) {
                        JSONObject data = json.getJSONObject("data");

//                        data:{
//                            newsnav:[],  //要闻导航
//                            slide:[],    //幻灯片
//                            shortcut:[],      //中间按钮，便捷菜单
//                            quotes:[],
//                            ad:[],
//                            news:[]
//                        },

                        List<NewsNavJson> newsNavs = JSON.parseArray(data.optString("newsnav")+"", NewsNavJson.class);
                        List<SlideJson> slides = JSON.parseArray(data.optString("slide")+"", SlideJson.class);
                        List<SlideJson> shortcuts = JSON.parseArray(data.optString("shortcut")+"", SlideJson.class);
                        List<QuotesJson> quotes = JSON.parseArray(data.optString("quotes")+"", QuotesJson.class);
                        List<SlideJson> ads = JSON.parseArray(data.optString("ad")+"", SlideJson.class);
                        List<NewsJson> news = JSON.parseArray(data.optString("news")+"", NewsJson.class);

                        newsFragment.initView(newsNavs, slides, shortcuts, quotes, ads, news);

                    } else {
                        String msg = json.optString("msg");
                        ToastView.makeText3(mContext, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastView.makeText3(mContext, mContext.getString(R.string.toast_error_load));
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                ToastView.makeText3(mContext, mContext.getString(R.string.toast_error_load));
            }
        });
    }
}
