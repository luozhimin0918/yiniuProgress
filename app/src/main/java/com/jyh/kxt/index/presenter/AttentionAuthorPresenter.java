package com.jyh.kxt.index.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.json.AuthorDetailsJson;
import com.jyh.kxt.index.ui.fragment.AttentionAuthorFragment;
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

public class AttentionAuthorPresenter extends BasePresenter {
    @BindObject AttentionAuthorFragment fragment;
    public VolleyRequest request;
    private String lastId = "";
    private boolean isMore = false;

    public AttentionAuthorPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        request.doGet(getUrl(), new HttpListener<List<AuthorDetailsJson>>() {
            @Override
            protected void onResponse(List<AuthorDetailsJson> authors) {
                if (authors == null || authors.size() == 0) {
                    fragment.plRootView.loadEmptyData();
                } else {
                    int size = authors.size();
                    List<AuthorDetailsJson> authorDetails = null;
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        authorDetails = new ArrayList<>(authors.subList(0, VarConstant.LIST_MAX_SIZE));
                        lastId = authors.get(VarConstant.LIST_MAX_SIZE - 1).getId();
                        isMore = true;
                    } else {
                        authorDetails = new ArrayList<>(authors);
                        lastId = "";
                        isMore = false;
                    }
                    fragment.init(authorDetails);
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
        request.doGet(getUrl(), new HttpListener<List<AuthorDetailsJson>>() {
            @Override
            protected void onResponse(List<AuthorDetailsJson> authorDetailsJsons) {
                if (authorDetailsJsons == null || authorDetailsJsons.size() == 0) {
                    fragment.plvContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.plvContent.onRefreshComplete();
                        }
                    }, 200);
                } else {
                    int size = authorDetailsJsons.size();
                    List<AuthorDetailsJson> authors;
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        authors = new ArrayList<>(authorDetailsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                        lastId = authors.get(VarConstant.LIST_MAX_SIZE - 1).getId();
                        isMore = true;
                    } else {
                        authors = new ArrayList<>(authorDetailsJsons);
                        lastId = "";
                        isMore = false;
                    }
                    fragment.refresh(authors);
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
            request.doGet(getUrl(), new HttpListener<List<AuthorDetailsJson>>() {
                @Override
                protected void onResponse(List<AuthorDetailsJson> authorDetailsJsons) {
                    if (authorDetailsJsons == null || authorDetailsJsons.size() == 0) {
                        fragment.plvContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fragment.plvContent.onRefreshComplete();
                            }
                        }, 200);
                    } else {
                        int size = authorDetailsJsons.size();
                        List<AuthorDetailsJson> authors;
                        if (size > VarConstant.LIST_MAX_SIZE) {
                            authors = new ArrayList<>(authorDetailsJsons.subList(0, VarConstant.LIST_MAX_SIZE));
                            lastId = authors.get(VarConstant.LIST_MAX_SIZE - 1).getId();
                            isMore = true;
                        } else {
                            authors = new ArrayList<>(authorDetailsJsons);
                            lastId = "";
                            isMore = false;
                        }
                        fragment.loadMore(authors);
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
        else {
            fragment.plvContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragment.plvContent.onRefreshComplete();
                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
                }
            }, 200);
        }
    }

    public String getUrl() {
        String url = HttpConstant.USER_FAVOR_WRITER;
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_ACCESS_TOKEN, userInfo.getToken());
        if (!RegexValidateUtil.isEmpty(lastId))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}
