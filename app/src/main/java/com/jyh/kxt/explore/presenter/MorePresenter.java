package com.jyh.kxt.explore.presenter;

import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.explore.ui.MoreActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.bean.EventBusClass;
import com.library.util.EncryptionUtils;
import com.library.util.SPUtils;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/8.
 */

public class MorePresenter extends BasePresenter {

    @BindObject MoreActivity moreActivity;

    private String type;
    private VolleyRequest request;

    private String lastId = "";
    private boolean isMore = false;

    public MorePresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(String type) {
        this.type = type;
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(getClass().getName());
        }
        lastId = "";
        request.doGet(getUrl(request), new HttpListener<List>() {
            @Override
            protected void onResponse(List o) {
                if (o == null || o.size() == 0) {
                    moreActivity.plRootView.loadEmptyData();
                    return;
                }

                if (o.size() > VarConstant.LIST_MAX_SIZE) {
                    isMore = true;
                    moreActivity.init(new ArrayList(o.subList(0, VarConstant.LIST_MAX_SIZE)));
                    lastId = moreActivity.moreAdapter.getLastId();
                } else {
                    isMore = false;
                    moreActivity.init(o);
                }

                //保存当前最新一条ID
                String mNewestId = moreActivity.moreAdapter.getNewestId();
                SPUtils.save(mContext, SpConstant.MORE_NEWEST_ID, mNewestId);


                UserJson userInfo = LoginUtils.getUserInfo(mContext);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_LOGIN_UPDATE, userInfo));
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                moreActivity.plRootView.loadError();
            }
        });
    }

    public void refresh() {
        lastId = "";
        request.doGet(getUrl(request), new HttpListener<List>() {
            @Override
            protected void onResponse(List o) {
                if (o == null || o.size() == 0) {
                    moreActivity.refresh(o);
                    return;
                }
                if (o.size() > VarConstant.LIST_MAX_SIZE) {
                    isMore = true;
                    moreActivity.refresh(new ArrayList(o.subList(0, VarConstant.LIST_MAX_SIZE)));
                    lastId = moreActivity.moreAdapter.getLastId();
                } else {
                    isMore = false;
                    moreActivity.refresh(o);
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                moreActivity.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moreActivity.plContent.onRefreshComplete();
                    }
                }, 500);
            }
        });
    }

    public void loadMore() {
        if (isMore)
            request.doGet(getUrl(request), new HttpListener<List>() {
                @Override
                protected void onResponse(List o) {
                    if (o == null || o.size() == 0) {
                        moreActivity.plContent.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                moreActivity.plContent.onRefreshComplete();
                            }
                        }, 500);
                        return;
                    }
                    if (o.size() > VarConstant.LIST_MAX_SIZE) {
                        isMore = true;
                        moreActivity.loadMore(new ArrayList(o.subList(0, VarConstant.LIST_MAX_SIZE)));
                        lastId = moreActivity.moreAdapter.getLastId();
                    } else {
                        isMore = false;
                        moreActivity.loadMore(o);
                    }
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    moreActivity.plRootView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moreActivity.plContent.onRefreshComplete();
                        }
                    }, 500);
                }
            });
        else {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            moreActivity.plContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    moreActivity.plContent.onRefreshComplete();
                }
            }, 500);
        }
    }

    private String getUrl(VolleyRequest request) {
        String url = "";
        JSONObject jsonParam = request.getJsonParam();
        if (lastId != null && !"".equals(lastId))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        String param = "";
        try {
            param = VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam
                    .toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (type) {
            case VarConstant.EXPLORE_ACTIVITY:
                url = HttpConstant.EXPLORE_ACTIVITY + param;
                break;
            case VarConstant.EXPLORE_TOPIC:
                url = HttpConstant.EXPLORE_TOPIC + param;
                break;
        }
        return url;
    }
}
