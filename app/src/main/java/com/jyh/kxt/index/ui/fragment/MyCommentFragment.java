package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/5/22.
 */

public class MyCommentFragment extends BaseFragment {

    @BindView(R.id.rv_message) PullToRefreshListView rvMessage;
    @BindView(R.id.pll_content) PageLoadLayout pllContent;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_my_comment);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();



//        CommentAdapter commentAdapter = new CommentAdapter();


    }
}
