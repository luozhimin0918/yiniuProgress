package com.jyh.kxt.trading.presenter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.trading.adapter.ViewPointDetailAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.ui.ViewPointDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/2.
 */

public class ViewPointDetailPresenter extends BasePresenter {

    @BindObject ViewPointDetailActivity mViewPointDetailActivity;

    private LinearLayout headLinearLayout;
    private List<CommentDetailBean> commentDetailList = new ArrayList<>();

    public ViewPointDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestInitData() {
        //写上网络请求

        /**
         * 处理Adapter
         */

        for (int i = 0; i < 5; i++) {
            CommentDetailBean commentDetailBean = new CommentDetailBean();
            commentDetailList.add(commentDetailBean);
        }

        ViewPointDetailAdapter mViewPointDetailAdapter = new ViewPointDetailAdapter(mContext, commentDetailList);
        mViewPointDetailActivity.mPullPinnedListView.setAdapter(mViewPointDetailAdapter);

        /**
         * 初始化LinearLayout
         */
        headLinearLayout = new LinearLayout(mContext);
        headLinearLayout.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        headLinearLayout.setOrientation(LinearLayout.VERTICAL);
        /**
         * 加入头部详情信息
         */
        View mHeadDetailView = LayoutInflater.from(mContext)
                .inflate(R.layout.view_point_detail_head,
                        mViewPointDetailActivity.mPullPinnedListView,
                        false);

        headLinearLayout.addView(mHeadDetailView);


        /**
         *  加入评论
         */
        mViewPointDetailActivity.commentPresenter.bindListView(headLinearLayout);
        mViewPointDetailActivity.commentPresenter.createMoreView(null);

        /**
         * 添加头部到ListView
         */
        mViewPointDetailActivity.mPullPinnedListView.getRefreshableView().addHeaderView(headLinearLayout);
    }
}
