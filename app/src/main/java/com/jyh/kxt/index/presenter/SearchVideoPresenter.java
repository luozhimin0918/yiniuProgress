package com.jyh.kxt.index.presenter;

import android.nfc.tech.TagTechnology;
import android.provider.MediaStore;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.index.ui.fragment.SearchVideoFragment;
import com.jyh.kxt.main.json.NewsJson;
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

public class SearchVideoPresenter extends BasePresenter {

    private VolleyRequest request;
    @BindObject SearchVideoFragment fragment;
    private String key;
    private String lastId;
    private boolean isMore;

    public SearchVideoPresenter(IBaseView iBaseView) {
        super(iBaseView);
        request = new VolleyRequest(mContext, mQueue);
        request.setTag(getClass().getName());
    }

    public void init(String key) {
        this.key = key;
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> list) {
                List<VideoListJson> videos = manageData(list);
                fragment.init(videos);
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
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> list) {
                List<VideoListJson> videos = manageData(list);
                fragment.refresh(videos);
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
            request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
                @Override
                protected void onResponse(List<VideoListJson> list) {
                    List<VideoListJson> videos = manageData(list);
                    fragment.loadMore(videos);
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


    private List<VideoListJson> manageData(List<VideoListJson> videos) {
        if (videos == null || videos.size() == 0) {
            return null;
        } else {
            List<VideoListJson> list;
            if (videos.size() > VarConstant.LIST_MAX_SIZE) {
                isMore = true;
                list = new ArrayList<>(videos.subList(0, VarConstant.LIST_MAX_SIZE));
                lastId = videos.get(VarConstant.LIST_MAX_SIZE - 1).getId();
            } else {
                isMore = false;
                list = new ArrayList<>(videos);
                lastId = "";
            }
            return list;
        }
    }

    public String getUrl() {
        String url = HttpConstant.SEARCH_VIDEO;
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
