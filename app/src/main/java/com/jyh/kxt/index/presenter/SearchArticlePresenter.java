package com.jyh.kxt.index.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.index.ui.fragment.SearchArticleFragment;
import com.jyh.kxt.main.json.NewsJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.window.ToastView;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class SearchArticlePresenter extends BasePresenter {
    private VolleyRequest request;
    @BindObject SearchArticleFragment fragment;

    private String lastId = "";
    private boolean isMore;
    private String key;

    public SearchArticlePresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init(String key) {
        this.key = key;
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                List<NewsJson> list = manageData(newsJsons);
                fragment.init(newsJsons);
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
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                List<NewsJson> list = manageData(newsJsons);
                fragment.refresh(list);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fragment.plvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore)
            request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    List<NewsJson> list = manageData(newsJsons);
                    fragment.loadMore(list);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    fragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        else
            fragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragment.plvContent.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 200);
    }

    private List<NewsJson> manageData(List<NewsJson> newsJsons) {
        if (newsJsons == null || newsJsons.size() == 0) {
            return null;
        } else {
            List<NewsJson> list;
            if (newsJsons.size() > VarConstant.LIST_MAX_SIZE) {
                isMore = true;
                list = new ArrayList<>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                lastId = newsJsons.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
            } else {
                isMore = false;
                list = new ArrayList<>(newsJsons);
                lastId = "";
            }
            return list;
        }
    }

    public String getUrl() {
        String url = HttpConstant.SEARCH_ARTICLE;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_WORD, key);
        if (!RegexValidateUtil.isEmpty(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return url;
        }
    }
}
