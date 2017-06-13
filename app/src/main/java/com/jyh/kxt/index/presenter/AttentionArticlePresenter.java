package com.jyh.kxt.index.presenter;

import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.utils.collect.CollectUtils;
import com.jyh.kxt.index.adapter.AttentionArticleAdapter;
import com.jyh.kxt.index.ui.fragment.AttentionArticleFragment;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.adapter.EditNewsAdapter;
import com.jyh.kxt.user.json.UserJson;
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
 * 创建日期:2017/5/17.
 */

public class AttentionArticlePresenter extends BasePresenter {
    @BindObject AttentionArticleFragment fragment;
    private VolleyRequest request;
    private String lastId = "";
    private boolean isMore;

    public AttentionArticlePresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                if (newsJsons == null || newsJsons.size() == 0) {
                    fragment.plRootView.loadEmptyData();
                } else {
                    List<NewsJson> news;
                    if (newsJsons.size() > VarConstant.LIST_MAX_SIZE) {
                        news = new ArrayList<NewsJson>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                        isMore = true;
                        lastId = newsJsons.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                    } else {
                        news = new ArrayList<NewsJson>(newsJsons);
                        isMore = false;
                        lastId = "";
                    }
                    fragment.init(news);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                fragment.plRootView.loadError();
            }
        });
    }

    public String getUrl() {
        String url = HttpConstant.USER_FAVOR_BLOGARTICLE;
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
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

    public void refresh() {
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsJsons) {
                if (newsJsons == null || newsJsons.size() == 0) {
                    fragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    List<NewsJson> news;
                    if (newsJsons.size() > VarConstant.LIST_MAX_SIZE) {
                        news = new ArrayList<NewsJson>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                        isMore = true;
                        lastId = newsJsons.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                    } else {
                        news = new ArrayList<NewsJson>(newsJsons);
                        isMore = false;
                        lastId = "";
                    }
                    fragment.refresh(news);
                }
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
                    if (newsJsons == null || newsJsons.size() == 0) {
                        fragment.plvContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment.plvContent.onRefreshComplete();
                            }
                        }, 200);
                    } else {
                        List<NewsJson> news;
                        if (newsJsons.size() > VarConstant.LIST_MAX_SIZE) {
                            news = new ArrayList<NewsJson>(newsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                            isMore = true;
                            lastId = newsJsons.get(VarConstant.LIST_MAX_SIZE - 1).getO_id();
                        } else {
                            news = new ArrayList<NewsJson>(newsJsons);
                            isMore = false;
                            lastId = "";
                        }
                        fragment.loadMore(news);
                    }
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

    public void selAll(boolean selected, DelNumListener delListener, AttentionArticleAdapter adapter) {
        if (selected) {
            //全选
            List<NewsJson> data = adapter.getData();
            for (NewsJson newsJson : data) {
                newsJson.setSel(true);
            }
            try {
                //设置选中数量
                delListener.delItem(data.size());
            } catch (Exception e) {
                e.printStackTrace();
                delListener.delItem(0);
            }
        } else {
            //取消全选
            List<NewsJson> data = adapter.getData();
            for (NewsJson newsJson : data) {
                newsJson.setSel(false);
            }
            //还原选中数量
            delListener.delItem(0);
        }
        adapter.notifyDataSetChanged();
    }

    public void del(final DelNumListener delNumListener, final AttentionArticleAdapter adapter) {
        //获取选中的id
        List<NewsJson> data = adapter.getData();
        String ids = "";
        for (NewsJson newsJson : data) {
            if (newsJson.isSel()) {
                String id = newsJson.getO_id();
                if (ids.equals("")) {
                    ids = id;
                } else {
                    ids += "," + id;
                }
            }
        }
        //选中非空判断
        if ("".equals(ids)) {
            ToastView.makeText3(mContext, "请选中至少一项");
            return;
        }

        final String finalIds = ids;
        CollectUtils.unCollects(mContext, VarConstant.COLLECT_TYPE_VIDEO,"", ids, new ObserverData() {
            @Override
            public void callback(Object o) {
                //删除取消收藏的数据
                adapter.removeById(finalIds);
                //退出编辑状态
                quitEdit(delNumListener, adapter);

            }

            @Override
            public void onError(Exception e) {
                //退出编辑状态
                quitEdit(delNumListener, adapter);
            }
        });
    }

    private void quitEdit(DelNumListener delNumListener, AttentionArticleAdapter adapter) {
        adapter.setEdit(false);
        List<NewsJson> data = adapter.getData();
        //还原删除按钮数字
        if (delNumListener != null)
            delNumListener.delItem(0);
        //空数据处理
        if (data == null || data.size() == 0) {
            fragment.plRootView.loadEmptyData();
            return;
        }
        //还原选中状态
        for (NewsJson newsJson : data) {
            newsJson.setSel(false);
        }
        fragment.ivSelAll.setSelected(false);
        fragment.llDel.setVisibility(View.GONE);
    }
}
