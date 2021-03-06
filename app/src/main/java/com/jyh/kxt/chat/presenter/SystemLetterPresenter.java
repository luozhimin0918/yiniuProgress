package com.jyh.kxt.chat.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.chat.SystemLetterActivity;
import com.jyh.kxt.chat.json.LetterSysJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;

import java.util.List;

/**
 * 项目名:KxtProfessional
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/8/29.
 */

public class SystemLetterPresenter extends BasePresenter {

    @BindObject SystemLetterActivity activity;

    private VolleyRequest request;

    public SystemLetterPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init() {
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_SENDER, userInfo.getUid());
        request.doGet(HttpConstant.MSG_SYS_LIST, jsonParam, new HttpListener<List<LetterSysJson>>() {
            @Override
            protected void onResponse(List<LetterSysJson> letterSysJsons) {
                activity.initData(letterSysJsons);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.loadError();
            }
        });
    }

//    public void loadMore() {
//        if (isMore) {
//            JSONObject jsonParam = request.getJsonParam();
//            UserJson userInfo = LoginUtils.getUserInfo(mContext);
//            jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
//            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
//            request.doGet(HttpConstant.MSG_SYS_LIST, jsonParam, new HttpListener<List<LetterSysJson>>() {
//                @Override
//                protected void onResponse(List<LetterSysJson> letterSysJsons) {
//                    activity.loadMore(letterSysJsons);
//                    activity.plRootView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            activity.plContent.onRefreshComplete();
//                        }
//                    }, 200);
//                }
//
//                @Override
//                protected void onErrorResponse(VolleyError error) {
//                    super.onErrorResponse(error);
//                    activity.plRootView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            activity.plContent.onRefreshComplete();
//                        }
//                    }, 200);
//                }
//            });
//        } else {
//            activity.plRootView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    activity.plContent.onRefreshComplete();
//                    ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
//                }
//            }, 200);
//        }
//    }

    public void refresh() {
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(mContext);
        jsonParam.put(VarConstant.HTTP_SENDER, userInfo.getUid());
        request.doGet(HttpConstant.MSG_SYS_LIST, jsonParam, new HttpListener<List<LetterSysJson>>() {
            @Override
            protected void onResponse(List<LetterSysJson> letterSysJsons) {
                activity.refresh(letterSysJsons);
                activity.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
                    }
                }, 200);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                activity.plRootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        activity.plContent.onRefreshComplete();
                    }
                }, 200);
            }
        });
    }

    /**
     * 数据处理
     *
     * @param data
     * @return
     */
    public List<LetterSysJson> disposeData(List<LetterSysJson> data) {

//        SPUtils.save(mContext, SpConstant.LAST_SYS_CHAT,data.get());

        return data;
    }
}
