package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.index.ui.fragment.AttentionAuthorFragment;
import com.jyh.kxt.index.ui.fragment.AttentionArticleFragment;
import com.library.base.LibActivity;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的关注
 */
public class AttentionActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) public TextView tvBarFunction;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();
    private AttentionAuthorFragment attentionAuthorFragment;
    private AttentionArticleFragment attentionArticleFragment;
    private String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_attention, StatusBarColor.THEME1);

        tvBarTitle.setText("我的关注");
        tvBarFunction.setText("编辑");
        tvBarFunction.setVisibility(View.GONE);

        initFragments();

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        vpContent.addOnPageChangeListener(this);
        stlNavigationBar.setViewPager(vpContent, tabs);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));
    }

    private void initFragments() {

        tabs = new String[2];
        fragmentList.add(attentionAuthorFragment = new AttentionAuthorFragment());
        fragmentList.add(attentionArticleFragment = new AttentionArticleFragment());
        tabs[0] = "关注作者";
        tabs[1] = "专栏文章";
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                if ("编辑".equals(tvBarFunction.getText().toString())) {
                    attentionArticleFragment.editMode(true);
                    tvBarFunction.setText("取消");
                } else {
                    attentionArticleFragment.editMode(false);
                    tvBarFunction.setText("编辑");
                }
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            tvBarFunction.setVisibility(View.VISIBLE);
        } else {
            tvBarFunction.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
