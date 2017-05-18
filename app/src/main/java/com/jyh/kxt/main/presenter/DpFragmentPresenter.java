package com.jyh.kxt.main.presenter;

import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.main.ui.fragment.DpItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.util.RegexValidateUtil;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/18.
 */

public class DpFragmentPresenter extends BasePresenter {
    @BindObject DpItemFragment dpItemFragment;
    private String code;
    private VolleyRequest request;
    private String lastId;
    private boolean isMore;

    public DpFragmentPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init(String code) {
        this.code = code;
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                List<NewsJson> news = manageData(newsJsons);
                dpItemFragment.init(news);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dpItemFragment.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        lastId = "";
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                List<NewsJson> news = manageData(newsJsons);
                dpItemFragment.refresh(news);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                dpItemFragment.plvContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dpItemFragment.plvContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore) {
            request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
                @Override
                protected void onResponse(List<NewsJson> newsJsons) {
                    List<NewsJson> news = manageData(newsJsons);
                    dpItemFragment.loadMore(news);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    dpItemFragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dpItemFragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            dpItemFragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dpItemFragment.plvContent.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 200);
        }
    }

    private String getUrl() {
        String url = HttpConstant.DP_LIST;
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_CODE, code);
        if (!RegexValidateUtil.isEmpty(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
        try {
            url += VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    private List<NewsJson> manageData(List<NewsJson> newsJsons) {

        if (newsJsons == null || newsJsons.size() == 0) {
            dpItemFragment.plRootView.loadEmptyData();
            return null;
        } else {
            List<NewsJson> news;
            int size = newsJsons.size();
            if (size > VarConstant.LIST_MAX_SIZE) {
                news = new ArrayList<>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                isMore = true;
                lastId = newsJsons.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
            } else {
                news = new ArrayList<>(newsJsons);
                isMore = false;
                lastId = "";
            }
            return news;
        }
    }
}
