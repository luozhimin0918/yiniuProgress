package com.jyh.kxt.av.presenter;

import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.VideoAdapter;
import com.jyh.kxt.av.json.VideoListJson;
import com.jyh.kxt.av.ui.fragment.VideoItemFragment;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.library.base.http.HttpListener;
import com.library.base.http.VarConstant;
import com.library.base.http.VolleyRequest;
import com.library.util.EncryptionUtils;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.window.ToastView;

import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class VideoItemPresenter extends BasePresenter {

    @BindObject VideoItemFragment videoItemFragment;
    private RequestQueue queue;
    private String id;
    private VolleyRequest request;

    private String lastId = "";

    private VideoAdapter videoAdapter;
    private boolean isMore;

    public VideoItemPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void init(String id) {
        videoItemFragment.plRootView.loadWait();
        this.id = id;
        queue = videoItemFragment.getQueue();
        request = new VolleyRequest(mContext, queue);
        initLoad(null);
    }

    private void initLoad(final PullToRefreshBase refreshView) {
        request.doGet(getUrl(request), new HttpListener<List<VideoListJson>>() {
            @Override
            protected void onResponse(final List<VideoListJson> videoListJsons) {
                if (videoListJsons != null) {
                    checkList(videoListJsons);
                    videoAdapter = new VideoAdapter(mContext, videoListJsons);
                    videoItemFragment.plvContent.setAdapter(videoAdapter);
                    videoItemFragment.plRootView.loadOver();
                    if (refreshView != null)
                        refreshView.onRefreshComplete();
                } else {
                    onErrorResponse(null);
                    if (refreshView != null)
                        refreshView.onRefreshComplete();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
                try {

                    if (refreshView != null)
                        refreshView.onRefreshComplete();
                    videoItemFragment.plRootView.loadError();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getUrl(VolleyRequest volleyRequest) {
        String url = HttpConstant.VIDEO_LIST;
        try {
            com.alibaba.fastjson.JSONObject object = volleyRequest.getJsonParam();
            object.put(VarConstant.HTTP_ID, id);
            if (!TextUtils.isEmpty(lastId))
                object.put(VarConstant.HTTP_LASTID, lastId);
            url = url + VarConstant.HTTP_CONTENT + EncryptionUtils.createJWT(VarConstant.KEY, object.toString());
        } catch (Exception e) {
            e.printStackTrace();
            url = "";
        }
        return url;
    }

    /**
     * 刷新
     *
     * @param refreshView
     */
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        lastId = "";
        initLoad(refreshView);
    }

    /**
     * 加载更多
     *
     * @param refreshView
     */
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {

        if (isMore)

            request.doGet(getUrl(request), new HttpListener<List<VideoListJson>>() {
                @Override
                protected void onResponse(List<VideoListJson> list) {
                    if (list != null) {
                        checkList(list);
                        videoAdapter.addData(list);
                        videoAdapter.notifyDataSetChanged();
                    }
                    refreshView.onRefreshComplete();
                }

                @Override
                protected void onErrorResponse(VolleyError error) {
                    super.onErrorResponse(error);
                    refreshView.onRefreshComplete();
                }
            });
        else {
            ToastView.makeText3(mContext, mContext.getString(R.string.no_data));
            refreshView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshView.onRefreshComplete();
                }
            }, 500);
        }
    }

    /**
     * 更新lastId
     *
     * @param list
     */
    private void checkList(List<VideoListJson> list) {
        if (list == null) {
            lastId = "";
            return;
        }
        int size = list.size();
        lastId = list.get(size - 1).getId();
        if (size <= VarConstant.LIST_MAX_SIZE) {
            isMore = false;
        } else {
            isMore = true;
            list.remove(size - 1);
        }
    }
}
