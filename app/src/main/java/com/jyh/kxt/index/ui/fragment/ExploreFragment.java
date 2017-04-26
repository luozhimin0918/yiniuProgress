package com.jyh.kxt.index.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.index.presenter.ExplorePresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 首页-探索
 */
public class ExploreFragment extends BaseFragment {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.plv_content) PullToRefreshListView plvContent;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    private ExplorePresenter explorePresenter;

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_explore);
        explorePresenter = new ExplorePresenter(this);
        ivBarBreak.setImageResource(R.mipmap.icon_user_def_photo);

        explorePresenter.init();

    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bar_break:
                break;
        }
    }
}
