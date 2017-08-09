package com.jyh.kxt.search.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.search.json.QuoteItemJson;
import com.jyh.kxt.search.json.QuoteJson;
import com.jyh.kxt.search.ui.fragment.SearchItemFragment;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.json.ViewPointTradeBean;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/8.
 */

public class SearchItemPresenter extends BasePresenter {

    @BindObject SearchItemFragment fragment;
    private VolleyRequest request;
    private String searchType;
    private String searchKey;
    private String lastId;
    private boolean isMore;

    public SearchItemPresenter(IBaseView iBaseView, String searchType) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName() + searchType);
        this.searchType = searchType;
    }

    public void init(final String searchKey) {
        this.searchKey = searchKey;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, searchType);
        jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        jsonParam.put(VarConstant.HTTP_WORD, searchKey);
        request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String result) {
                switch (searchType) {
                    case VarConstant.SEARCH_TYPE_MAIN:
                        List<JSONObject> array = JSON.parseArray(result, JSONObject.class);
                        for (JSONObject job : array) {
                            String type = job.getString("type");
                            if (type != null && type.equals("quotes")) {
                                QuoteJson quoteJson = JSON.parseObject(job.toJSONString(), QuoteJson.class);
                                fragment.initQuote(quoteJson);
                            } else {
                                List<ViewPointTradeBean> viewpoints = JSON.parseArray(job.getString("data"), ViewPointTradeBean.class);
                                fragment.init(viewpoints);
                            }
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIEWPOINT:
                        List<ViewPointTradeBean> viewpoints = JSON.parseArray(result, ViewPointTradeBean.class);
                        fragment.init(viewpoints);
                        break;
                    case VarConstant.SEARCH_TYPE_NEWS:
                        List<NewsJson> newsJsons = JSON.parseArray(result, NewsJson.class);
                        fragment.init(newsJsons);
                        break;
                    case VarConstant.SEARCH_TYPE_VIDEO:
                        List<VideoListJson> videos = JSON.parseArray(result, VideoListJson.class);
                        fragment.init(videos);
                        break;
                    case VarConstant.SEARCH_TYPE_COLUMNIST:
                        List<ColumnistListJson> authorNewsJsons = JSON.parseArray(result, ColumnistListJson.class);
                        fragment.init(authorNewsJsons);
                        break;
                    case VarConstant.SEARCH_TYPE_BLOG:
                        List<NewsJson> blogs = JSON.parseArray(result, NewsJson.class);
                        fragment.init(blogs);
                        break;
                    case VarConstant.SEARCH_TYPE_QUOTE:
                        List<QuoteItemJson> quotes = JSON.parseArray(result, QuoteItemJson.class);
                        fragment.init(quotes);
                        break;
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        lastId = "";
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, searchType);
        jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        jsonParam.put(VarConstant.HTTP_WORD, searchKey);
        request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
            @Override
            protected void onResponse(String result) {
                switch (searchType) {
                    case VarConstant.SEARCH_TYPE_MAIN:
                        List<JSONObject> array = JSON.parseArray(result, JSONObject.class);
                        int size = array.size();
                        for (JSONObject job : array) {
                            String type = job.getString("type");
                            if (type != null && type.equals("quotes")) {
                                QuoteJson quoteJson = JSON.parseObject(job.toJSONString(), QuoteJson.class);
                                fragment.initQuote(quoteJson);
                            } else {
                                List<ViewPointTradeBean> viewpoints = JSON.parseArray(job.getString("data"), ViewPointTradeBean.class);
                                fragment.refresh(viewpoints);
                            }
                        }
                        break;
                    case VarConstant.SEARCH_TYPE_VIEWPOINT:
                        List<ViewPointTradeBean> viewpoints = JSON.parseArray(result, ViewPointTradeBean.class);
                        fragment.refresh(viewpoints);
                        break;
                    case VarConstant.SEARCH_TYPE_NEWS:
                        List<NewsJson> newsJsons = JSON.parseArray(result, NewsJson.class);
                        fragment.refresh(newsJsons);
                        break;
                    case VarConstant.SEARCH_TYPE_VIDEO:
                        List<VideoListJson> videos = JSON.parseArray(result, VideoListJson.class);
                        fragment.refresh(videos);
                        break;
                    case VarConstant.SEARCH_TYPE_COLUMNIST:
                        List<ColumnistListJson> authorNewsJsons = JSON.parseArray(result, ColumnistListJson.class);
                        fragment.refresh(authorNewsJsons);
                        break;
                    case VarConstant.SEARCH_TYPE_BLOG:
                        List<NewsJson> blogs = JSON.parseArray(result, NewsJson.class);
                        fragment.refresh(blogs);
                        break;
                    case VarConstant.SEARCH_TYPE_QUOTE:
                        List<QuoteItemJson> quotes = JSON.parseArray(result, QuoteItemJson.class);
                        fragment.refresh(quotes);
                        break;
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragment.plContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_CODE, searchType);
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
            jsonParam.put(VarConstant.HTTP_WORD, searchKey);
            request.doPost(HttpConstant.SEARCH_LIST, jsonParam, new HttpListener<String>() {
                @Override
                protected void onResponse(String result) {
                    switch (searchType) {
                        case VarConstant.SEARCH_TYPE_MAIN:
                            List<ViewPointTradeBean> viewpoints = JSON.parseArray(result, ViewPointTradeBean.class);
                            fragment.loadMore(viewpoints);
                            break;
                        case VarConstant.SEARCH_TYPE_VIEWPOINT:
                            List<ViewPointTradeBean> viewpoints2 = JSON.parseArray(result, ViewPointTradeBean.class);
                            fragment.loadMore(viewpoints2);
                            break;
                        case VarConstant.SEARCH_TYPE_NEWS:
                            List<NewsJson> newsJsons = JSON.parseArray(result, NewsJson.class);
                            fragment.loadMore(newsJsons);
                            break;
                        case VarConstant.SEARCH_TYPE_VIDEO:
                            List<VideoListJson> videos = JSON.parseArray(result, VideoListJson.class);
                            fragment.loadMore(videos);
                            break;
                        case VarConstant.SEARCH_TYPE_COLUMNIST:
                            List<ColumnistListJson> authorNewsJsons = JSON.parseArray(result, ColumnistListJson.class);
                            fragment.loadMore(authorNewsJsons);
                            break;
                        case VarConstant.SEARCH_TYPE_BLOG:
                            List<NewsJson> blogs = JSON.parseArray(result, NewsJson.class);
                            fragment.loadMore(blogs);
                            break;
                        case VarConstant.SEARCH_TYPE_QUOTE:
                            List<QuoteItemJson> quotes = JSON.parseArray(result, QuoteItemJson.class);
                            fragment.loadMore(quotes);
                            break;
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    fragment.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            fragment.plContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                    fragment.plContent.onRefreshComplete();
                }
            }, 200);
        }
    }

    public void setLastId(String id) {
        lastId = id;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
