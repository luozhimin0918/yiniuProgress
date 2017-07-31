package com.jyh.kxt.explore.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.trading.ui.fragment.ArticleFragment;
import com.jyh.kxt.explore.ui.fragment.AuthorFragment;
import com.library.util.RegexValidateUtil;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:名家专栏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/15.
 */

public class AuthorListActivity extends BaseActivity implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private String[] tabs = new String[]{"名家", "文章"};
    private List<Fragment> fragmentList = new ArrayList<>();
    private AuthorFragment authorFragment;
    public ArticleFragment articleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_authorlist, StatusBarColor.THEME1);

        String childTab = getIntent().getStringExtra(IntentConstant.TAB);
        articleFragment = new ArticleFragment();
        if (!RegexValidateUtil.isEmpty(childTab)) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentConstant.TAB, childTab);
            articleFragment.setArguments(bundle);
        }
        authorFragment = new AuthorFragment();

        fragmentList.add(authorFragment);
        fragmentList.add(articleFragment);

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));

        stlNavigationBar.setTabData(tabs);
        stlNavigationBar.setOnTabSelectListener(this);
        vpContent.addOnPageChangeListener(this);

        int index = getIntent().getIntExtra(IntentConstant.INDEX, 0);
        onTabSelect(index);
    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (articleFragment != null)
            articleFragment.onChangeTheme();
        if (authorFragment != null)
            authorFragment.onChangeTheme();
    }

    @OnClick(R.id.iv_left_icon)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onTabSelect(int position) {
        vpContent.setCurrentItem(position);
    }

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stlNavigationBar.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (articleFragment != null && data != null)
            articleFragment.onActivityResult(requestCode, resultCode, data);
    }
}
