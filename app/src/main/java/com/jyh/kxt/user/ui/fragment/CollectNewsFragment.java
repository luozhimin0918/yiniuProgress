package com.jyh.kxt.user.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.main.adapter.NewsAdapter;
import com.jyh.kxt.main.json.NewsJson;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * 项目名:Kxt
 * 类描述:收藏-文章
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectNewsFragment extends BaseFragment implements PageLoadLayout.OnAfreshLoadListener, PullToRefreshBase.OnRefreshListener2 {

    @BindView(R.id.plv_content) PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;

    private VolleyRequest request;
    private String lastId;
    private NewsAdapter newsAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_news_item);

        plRootView.setOnAfreshLoadListener(this);
        plvContent.setOnRefreshListener(this);

        if (request == null)
            request = new VolleyRequest(getContext(), getQueue());

        plRootView.loadWait();
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsList) {
                newsAdapter=new NewsAdapter(getContext(),newsList);
                plvContent.setAdapter(newsAdapter);
                plRootView.loadOver();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                plRootView.loadError();
            }
        });

    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        lastId = "";
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsList) {
                newsAdapter.setData(newsList);
                refreshView.onRefreshComplete();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                refreshView.onRefreshComplete();
            }
        });
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        getLastId();
        request.doGet(getUrl(), new HttpListener<List<NewsJson>>() {
            @Override
            protected void onResponse(List<NewsJson> newsList) {
                newsAdapter.addData(newsList);
                refreshView.onRefreshComplete();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                refreshView.onRefreshComplete();
            }
        });
    }

    @Override
    public void OnAfreshLoad() {

    }

    public String getUrl() {
        JSONObject jsonParam = request.getJsonParam();
        UserJson userInfo = LoginUtils.getUserInfo(getContext());
        jsonParam.put(VarConstant.HTTP_UID, userInfo.getUid());
        jsonParam.put(VarConstant.HTTP_TOKEN, userInfo.getAccessToken());
        if (!TextUtils.isEmpty(lastId))
            jsonParam.put(VarConstant.HTTP_LASTID, lastId);

        try {
            return HttpConstant.COLLECT_NEWS + EncryptionUtils.createJWT(VarConstant.KEY, jsonParam.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return HttpConstant.COLLECT_NEWS;
        }
    }

    /**
     * 获取lastId
     */
    private void getLastId() {
        List<NewsJson> dataList = newsAdapter.dataList;
        int lastPosition = dataList.size() - 1;
        lastId = dataList.get(lastPosition).getTitle();
    }
}
