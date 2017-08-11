package com.jyh.kxt.index.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseListAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mr'Dai on 2017/8/11.
 */

public class PullListViewPresenter extends BasePresenter implements PullToRefreshBase.OnRefreshListener2<ListView> {
    enum LoadMode {
        LIST_PULL, PAGE_LOAD
    }

    /**
     * 加载数据的方式  0 ListView 直接加载   1  PageLoad 显示加载
     */
    private LoadMode mLoadMode = LoadMode.LIST_PULL;

    public interface OnLoadMoreListener {
        void alterParameter(List dataList, JSONObject jsonObject);
    }

    @BindView(R.id.pll_content) PageLoadLayout mPageLoadLayout;
    @BindView(R.id.ptrlv_content) PullToRefreshListView mPullToRefreshListView;

    private View contentView;
    private ViewGroup fatherView;

    private BaseListAdapter baseListAdapter;
    private String url;
    private JSONObject jsonObject;
    private Class<?> jsonBeanClass;

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public PullListViewPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void setLoadMode(LoadMode mLoadMode) {
        this.mLoadMode = mLoadMode;
    }

    public void setAdapter(BaseListAdapter baseListAdapter) {
        this.baseListAdapter = baseListAdapter;
        mPullToRefreshListView.setAdapter(baseListAdapter);
    }

    public void setRequestInfo(String url, JSONObject jsonObject, Class<?> jsonBeanClass) {
        this.url = url;
        this.jsonObject = jsonObject;
        this.jsonBeanClass = jsonBeanClass;
    }

    //创建视图
    public View createView(ViewGroup fatherView) {
        this.fatherView = fatherView;
        this.contentView = LayoutInflater.from(mContext).inflate(R.layout.pull_list_view, fatherView, false);
        ButterKnife.bind(this, contentView);

        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

        return contentView;
    }


    /**
     * 开始请求接口
     */
    public void pullStartRequest() {
        if (mLoadMode == LoadMode.PAGE_LOAD) {
            mPageLoadLayout.loadWait();
        } else {
            mPullToRefreshListView.onHeadRefreshing();
        }
        requestListData();
    }

    /**
     * 下拉动 初始化等
     */
    private void requestListData() {
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());

        jsonObject.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
        jsonObject.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);
        mVolleyRequest.doPost(url, jsonObject, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {

                List<?> arrayList = JSONArray.parseArray(s, jsonBeanClass);
                if (arrayList.size() == 0) {
                    mPageLoadLayout.loadEmptyData();
                    return;
                }
                mPageLoadLayout.loadOver();

                baseListAdapter.dataList.clear();
                baseListAdapter.dataList.addAll(arrayList);
                baseListAdapter.notifyDataSetChanged();

                mPullToRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                mPageLoadLayout.loadError();
                mPullToRefreshListView.onRefreshComplete();
            }
        });
    }

    /**
     * 上拉  加载更多
     */
    private void requestLoadMore() {
        VolleyRequest mVolleyRequest = new VolleyRequest(mContext, iBaseView.getQueue());
        mVolleyRequest.doPost(url, jsonObject, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                mPageLoadLayout.loadOver();
                mPullToRefreshListView.onRefreshComplete();

                List<?> arrayList = JSONArray.parseArray(s, jsonBeanClass);
                if (arrayList.size() == 0) {
                    mPullToRefreshListView.noMoreData();
                    return;
                }
                baseListAdapter.dataList.addAll(arrayList);
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                mPageLoadLayout.loadOver();
                mPullToRefreshListView.onRefreshComplete();
            }
        });
    }

    /**
     * 替换View 视图
     */
    public void replaceContentView() {
        fatherView.removeAllViews();
        fatherView.addView(contentView);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestListData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.alterParameter(baseListAdapter.dataList, jsonObject);
        }
        requestLoadMore();
    }


    public View getContentView() {
        return contentView;
    }
}
