package com.jyh.kxt.trading.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.trading.json.ColumnistList;
import com.jyh.kxt.trading.json.ColumnistListJson;
import com.jyh.kxt.trading.ui.fragment.AuthorFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.window.ToastView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/31.
 */

public class AuthorItemPresenter extends BasePresenter {

    @BindObject AuthorFragment authorFragment;
    private VolleyRequest request;
    private String code;
    private boolean isMore;
    private String lastId;

    public AuthorItemPresenter(IBaseView iBaseView, String code) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        this.code = code;
        request.setTag(code);
    }

    public void init() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_TYPE, code);
        request.doPost(HttpConstant.TRADING_COLUMNIST_LIST, jsonParam, new HttpListener<ColumnistList>() {
            @Override
            protected void onResponse(ColumnistList data) {
                if (data == null || data.getData() == null || data.getData().size() == 0) {
                    authorFragment.plRootView.loadEmptyData();
                } else {
                    List<ColumnistListJson> jsons;
                    List<ColumnistListJson> columnistListJsons = data.getData();
                    int size = columnistListJsons.size();
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        isMore = true;
                        jsons = new ArrayList<>(columnistListJsons.subList(0, size - 1));
                        lastId = data.getCurrent_page();
                    } else {
                        isMore = false;
                        lastId = data.getCurrent_page();
                        jsons = new ArrayList<>(columnistListJsons);
                    }
                    authorFragment.init(jsons);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorFragment.plRootView.loadError();
            }
        });
    }

    public void refreshView() {
        JSONObject jsonParam = request.getJsonParam();
        jsonParam.put(VarConstant.HTTP_TYPE, code);
        lastId = "";
        request.doPost(HttpConstant.TRADING_COLUMNIST_LIST, jsonParam, new HttpListener<ColumnistList>() {
            @Override
            protected void onResponse(ColumnistList data) {
                if (data == null || data.getData() == null || data.getData().size() == 0) {
                } else {
                    List<ColumnistListJson> jsons;
                    List<ColumnistListJson> columnistListJsons;
                    columnistListJsons = data.getData();
                    int size = columnistListJsons.size();
                    if (size > VarConstant.LIST_MAX_SIZE) {
                        isMore = true;
                        jsons = new ArrayList<>(columnistListJsons.subList(0, size - 1));
                        lastId = data.getCurrent_page();
                    } else {
                        isMore = false;
                        lastId = data.getCurrent_page();
                        jsons = new ArrayList<>(columnistListJsons);
                    }
                    authorFragment.refresh(jsons);
                }
                authorFragment.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (authorFragment != null && authorFragment.plContent != null)
                            authorFragment.plContent.onRefreshComplete();
                    }
                }, 200);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                authorFragment.plContent.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (authorFragment != null && authorFragment.plContent != null)
                            authorFragment.plContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    public void loadMore() {
        if (isMore) {
            JSONObject jsonParam = request.getJsonParam();
            jsonParam.put(VarConstant.HTTP_TYPE, code);
            jsonParam.put(VarConstant.HTTP_CURRENT_PAGE, lastId);
            request.doPost(HttpConstant.TRADING_COLUMNIST_LIST, jsonParam, new HttpListener<ColumnistList>() {
                @Override
                protected void onResponse(ColumnistList data) {
                    if (data == null || data.getData() == null || data.getData().size() == 0) {

                    } else {
                        List<ColumnistListJson> jsons;
                        List<ColumnistListJson> columnistListJsons=data.getData();
                        int size = columnistListJsons.size();
                        if (size > VarConstant.LIST_MAX_SIZE) {
                            isMore = true;
                            jsons = new ArrayList<>(columnistListJsons.subList(0, size - 1));
                            lastId = data.getCurrent_page();
                        } else {
                            isMore = false;
                            lastId = data.getCurrent_page();
                            jsons = new ArrayList<>(columnistListJsons);
                        }
                        authorFragment.loadMore(jsons);
                    }
                    authorFragment.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (authorFragment != null && authorFragment.plContent != null)
                                authorFragment.plContent.onRefreshComplete();
                        }
                    }, 200);
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    authorFragment.plContent.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (authorFragment != null && authorFragment.plContent != null)
                                authorFragment.plContent.onRefreshComplete();
                        }
                    }, 200);
                }
            });
        } else {
            ToastView.makeText(mContext, mContext.getString(R.string.no_data));
            authorFragment.plContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (authorFragment != null && authorFragment.plContent != null)
                        authorFragment.plContent.onRefreshComplete();
                }
            }, 200);
        }
    }
}
