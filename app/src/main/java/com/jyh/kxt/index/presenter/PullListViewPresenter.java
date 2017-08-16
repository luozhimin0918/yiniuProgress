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

public class PullListViewPresenter extends BasePresenter implements PullToRefreshBase.OnRefreshListener2<ListView>, PageLoadLayout
        .OnAfreshLoadListener {

    enum LoadMode {
        LIST_PULL, PAGE_LOAD
    }

    /**
     * 加载数据的方式  0 ListView 直接加载   1  PageLoad 显示加载
     */
    private LoadMode mLoadMode = LoadMode.LIST_PULL;

    public interface OnLoadMoreListener {
        void beforeParameter(List dataList, JSONObject jsonObject);
    }

    @BindView(R.id.pll_content) PageLoadLayout mPageLoadLayout;
    @BindView(R.id.ptrlv_content) PullToRefreshListView mPullToRefreshListView;

    private View contentView;
    private ViewGroup fatherView;

    private BaseListAdapter baseListAdapter;
    private String url;
    /**
     * 下拉参数
     */
    private JSONObject startParams;
    /**
     * 上拉加载更多参数
     */
    private JSONObject endParams;

    private Class<?> jsonBeanClass;

    /**
     * 对于视图的Tag
     */
    private String contentTag;

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

    public void setRequestInfo(String url, JSONObject defaultParams, Class<?> jsonBeanClass) {
        this.url = url;
        this.startParams = defaultParams;
        this.jsonBeanClass = jsonBeanClass;

        startParams.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
        startParams.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);

        startParams.put(VarConstant.HTTP_VERSION, VarConstant.HTTP_VERSION_VALUE);
        startParams.put(VarConstant.HTTP_SYSTEM, VarConstant.HTTP_SYSTEM_VALUE);

        this.endParams = JSONObject.parseObject(startParams.toJSONString());
    }

    //创建视图
    public View createView(ViewGroup fatherView) {
        this.fatherView = fatherView;
        this.contentView = LayoutInflater.from(mContext).inflate(R.layout.pull_list_view, fatherView, false);
        ButterKnife.bind(this, contentView);

        mPullToRefreshListView.setOnRefreshListener(this);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        mPageLoadLayout.setOnAfreshLoadListener(this);

        return contentView;
    }


    /**
     * 开始请求接口
     */
    public void startRequest() {
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

        mVolleyRequest.doPost(url, startParams, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                mPullToRefreshListView.removeFootNoMore();

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
        mVolleyRequest.doPost(url, endParams, new HttpListener<String>() {
            @Override
            protected void onResponse(String s) {
                mPageLoadLayout.loadOver();
                mPullToRefreshListView.onRefreshComplete();

                List<?> arrayList = JSONArray.parseArray(s, jsonBeanClass);
                if (arrayList.size() == 0) {
                    mPullToRefreshListView.addFootNoMore();
                    return;
                }
                baseListAdapter.dataList.addAll(arrayList);
                baseListAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                mPageLoadLayout.loadOver();
                mPullToRefreshListView.onRefreshComplete();
            }
        });
    }

    public void switchContentView() {
        fatherView.removeAllViews();
        fatherView.addView(contentView);
    }

    /**
     * 替换View 视图
     *
     * @param contentTag
     */
    public void switchContentView(String contentTag) {
        if (this.contentTag == null) {
            this.contentTag = contentTag;
        }
        //Tag一致则认为是当前的视图
        if (contentTag.equals(fatherView.getTag())) {
            fatherView.removeAllViews();
            fatherView.addView(contentView);
        }
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        requestListData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        try {
            if (baseListAdapter.dataList == null || baseListAdapter.dataList.size() == 0) {
                return;
            }
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.beforeParameter(baseListAdapter.dataList, endParams);
            }
            requestLoadMore();
        } catch (Exception e) {

        }
    }


    @Override
    public void OnAfreshLoad() {
        startRequest();
    }

    public View getContentView() {
        return contentView;
    }

    public BaseListAdapter getBaseListAdapter() {
        return baseListAdapter;
    }
}
