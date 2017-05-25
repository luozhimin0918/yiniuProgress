package com.jyh.kxt.av.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.av.adapter.RankAdapter;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.fragment.RankItemFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.handmark.PullToRefreshBase;

import java.util.List;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class RankItemPresenter extends BasePresenter implements PullToRefreshBase.OnRefreshListener2 {

    @BindObject RankItemFragment rankItemFragment;
    private String url;
    private VolleyRequest request;
    private String lastId;
    private RankAdapter rankAdapter;

    public RankItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(final String rankUrl) {
        url = rankUrl;
        if (request == null) {
            request = new VolleyRequest(mContext, mQueue);
            request.setTag(url);
        }
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> list) {

                if (rankAdapter == null) {
                    rankAdapter = new RankAdapter(mContext, list);
                    rankItemFragment.plvContent.setAdapter(rankAdapter);
                } else {
                    rankAdapter.setData(list);
                }
                try {
                    rankItemFragment.plvContent.onRefreshComplete();
                    rankItemFragment.plRootView.loadOver();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    rankItemFragment.plvContent.onRefreshComplete();
                    rankItemFragment.plRootView.loadError();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    private String getUrl() {
        JSONObject jsonParam = request.getJsonParam();
        if (!TextUtils.isEmpty(lastId)) {
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);
        }
        try {
            return url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        lastId = "";
        init(url);
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        List<VideoListJson> dataList = rankAdapter.dataList;
        int lastPosition = dataList.size() - 1;
        lastId = dataList.get(lastPosition).getId();
        request.doGet(getUrl(), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(List<VideoListJson> list) {
                rankAdapter.addData(list);
                try {
                    refreshView.onRefreshComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {
                    refreshView.onRefreshComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onChangeTheme() {
        if (rankAdapter != null)
            rankAdapter.notifyDataSetChanged();
    }
}
