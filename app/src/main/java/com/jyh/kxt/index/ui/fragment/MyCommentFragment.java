package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.R;
import com.jyh.kxt.av.adapter.CommentAdapter;
import com.jyh.kxt.av.json.CommentBean;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.presenter.MyCommentPresenter;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/5/22.
 */

public class MyCommentFragment extends BaseFragment {

    @BindView(R.id.rv_message) PullToRefreshListView rvMessage;
    @BindView(R.id.pll_content) PageLoadLayout pllContent;

    private MyCommentPresenter myCommentPresenter;

    private int fromFragment;
    private String requestUrl;

    private List<CommentBean> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_my_comment);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

        Bundle arguments = getArguments();

        fromFragment = arguments.getInt("from");
        requestUrl = fromFragment == 0 ? HttpConstant.MEMBER_COMMENT_MY : HttpConstant.MEMBER_COMMENT_REPLY;

        myCommentPresenter = new MyCommentPresenter(this);

        rvMessage.setMode(PullToRefreshBase.Mode.BOTH);
        rvMessage.onHeadRefreshing();
        rvMessage.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestCommentOrReply(PullToRefreshBase.Mode.PULL_FROM_START);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                requestCommentOrReply(PullToRefreshBase.Mode.PULL_FROM_END);
            }
        });
    }

    private void requestCommentOrReply(final PullToRefreshBase.Mode pullFrom) {

        UserJson userInfo = LoginUtils.getUserInfo(getContext());

        VolleyRequest volleyRequest = new VolleyRequest(getContext(), getQueue());
        JSONObject jsonParam = volleyRequest.getJsonParam();
        jsonParam.put("uid", userInfo.getUid());
        jsonParam.put("accessToken", userInfo.getToken());

        if (pullFrom == PullToRefreshBase.Mode.PULL_FROM_END) {
            CommentBean commentBean = commentList.get(commentList.size() - 1);
            jsonParam.put("last_id", commentBean.getId());
        }

        volleyRequest.doPost(requestUrl, jsonParam, new HttpListener<List<CommentBean>>() {
            @Override
            protected void onResponse(List<CommentBean> commentBeen) {
                rvMessage.onRefreshComplete();

               if (pullFrom == PullToRefreshBase.Mode.PULL_FROM_START) {
                    commentList.clear();
                }

                commentList.addAll(commentBeen);
                if (commentList.size() == 0) {
                    pllContent.loadEmptyData();
                    return;
                }

                if (commentBeen.size() == 0) {
                    TSnackbar.make(pllContent,
                            "没有更多消息了",
                            TSnackbar.LENGTH_LONG,
                            TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                            .setPromptThemBackground(Prompt.WARNING)
                            .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                                    .getDimensionPixelOffset(R.dimen.actionbar_height))
                            .show();
                    return;
                }

                if (pullFrom == PullToRefreshBase.Mode.PULL_FROM_START) {
                     if (commentAdapter == null) {
                        commentAdapter = new CommentAdapter(getContext(), commentList, myCommentPresenter);
                        commentAdapter.setAdapterFromStatus(fromFragment == 0 ? 1 : 2);
                        rvMessage.setAdapter(commentAdapter);
                    } else {
                        commentAdapter.notifyDataSetChanged();
                    }
                } else {
                    commentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            protected void onErrorResponse(VolleyError error) {
                super.onErrorResponse(error);
            }
        });
    }
}
