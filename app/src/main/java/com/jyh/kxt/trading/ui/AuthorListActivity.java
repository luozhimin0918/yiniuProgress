package com.jyh.kxt.trading.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.trading.json.ColumnistNavJson;
import com.jyh.kxt.trading.presenter.AuthorListPresenter;
import com.jyh.kxt.trading.ui.fragment.AuthorFragment;
import com.library.util.SystemUtil;
import com.library.widget.PageLoadLayout;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:全部专栏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorListActivity extends BaseActivity implements PageLoadLayout.OnAfreshLoadListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) ImageView ivBarFunction;
    @BindView(R.id.pl_rootView) public PageLoadLayout plRootView;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private AuthorListPresenter presenter;
    private List<Fragment> fragments = new ArrayList<>();
    private String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading_authorlist, StatusBarColor.THEME1);

        tvBarTitle.setText("全部专栏");
        ivBarBreak.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ico_break));
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_search));
        vpContent.addOnPageChangeListener(this);
        plRootView.setOnAfreshLoadListener(this);

        presenter = new AuthorListPresenter(this);

        plRootView.loadWait();
        presenter.initTabInfo();

    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                // TODO: 2017/7/31  搜索
                break;
        }
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        ivBarBreak.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.ico_break));
        ivBarFunction.setImageDrawable(ContextCompat.getDrawable(this, R.mipmap.icon_search));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getQueue().cancelAll(AuthorListPresenter.class.getClass().getName());
    }

    public void initTabInfo(List<ColumnistNavJson> navs) {
        if (navs == null || navs.size() == 0) {
            plRootView.loadError();
            return;
        }
        int size = navs.size();
        tabs = new String[size];
        for (int i = 0; i < size; i++) {
            ColumnistNavJson columnistNavJson = navs.get(i);
            tabs[i] = columnistNavJson.getName();

            Fragment fragment = new AuthorFragment();
            Bundle bundle = new Bundle();
            bundle.putString(AuthorFragment.CODE, columnistNavJson.getCode());
            fragment.setArguments(bundle);

            fragments.add(fragment);
        }
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragments));
        stlNavigationBar.setViewPager(vpContent, tabs);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        if (size <= 4)
            stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / size));
        plRootView.loadOver();
    }

    @Override
    public void OnAfreshLoad() {
        plRootView.loadWait();
        presenter.initTabInfo();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
