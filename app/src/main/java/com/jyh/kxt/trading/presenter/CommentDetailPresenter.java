package com.jyh.kxt.trading.presenter;

import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.trading.adapter.CommentDetailAdapter;
import com.jyh.kxt.trading.json.CommentDetailBean;
import com.jyh.kxt.trading.ui.CommentDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/8/3.
 */

public class CommentDetailPresenter extends BasePresenter {

    @BindObject CommentDetailActivity commentDetailActivity;

    public CommentDetailPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void requestInitData() {
        List<CommentDetailBean> commentDetailList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CommentDetailBean commentDetailBean = new CommentDetailBean();
            commentDetailList.add(commentDetailBean);
        }
        CommentDetailAdapter commentDetailAdapter = new CommentDetailAdapter(mContext, commentDetailList);
        commentDetailActivity.pullToRefreshListView.setAdapter(commentDetailAdapter);
    }
}
