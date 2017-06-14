package com.jyh.kxt.main.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.main.json.NewsNavJson;
import com.jyh.kxt.main.presenter.DpPresenter;
import com.jyh.kxt.main.ui.fragment.DpItemFragment;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 专家 点评
 */
public class DpActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;

    private DpPresenter dpPresenter;
    private String[] tabs;
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_dp, StatusBarColor.THEME1);

        dpPresenter = new DpPresenter(this);
        tvBarTitle.setText("专家点评");

        plRootView.setOnAfreshLoadListener(this);

        plRootView.loadWait();
        dpPresenter.initActionBar();

    }

    @OnClick(R.id.iv_bar_break)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQueue.cancelAll(dpPresenter.getClass().getName());
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        dpPresenter.initActionBar();
    }

    public void initActionBar(List<NewsNavJson> navs) {
        if (navs == null || navs.size() == 0) {
            plRootView.loadEmptyData();
        } else {
            initFragments(navs);
            plRootView.loadOver();
        }
    }

    private void initFragments(List<NewsNavJson> navs) {
        int size = navs.size();
        tabs = new String[size];
        fragmentList.clear();
        for (int i = 0; i < size; i++) {
            NewsNavJson nav = navs.get(i);
            tabs[i] = nav.getName();
            DpItemFragment dpItemFragment = new DpItemFragment();
            Bundle args = new Bundle();
            args.putString(IntentConstant.CODE, nav.getCode());
            dpItemFragment.setArguments(args);
            fragmentList.add(dpItemFragment);
        }
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        stlNavigationBar.setViewPager(vpContent, tabs, true);
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (fragmentList != null)
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onChangeTheme();
                }
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onChangeTheme();
    }
}
