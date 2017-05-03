package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.user.ui.fragment.CollectFlashFragment;
import com.jyh.kxt.user.ui.fragment.CollectNewsFragment;
import com.jyh.kxt.user.ui.fragment.CollectVideoFragment;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:我的收藏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collect, StatusBarColor.THEME1);

        tvBarTitle.setText("我的收藏");
        ivBarFunction.setText("编辑");

        String[] tabs = new String[]{"视听", "文章", "快讯"};

        initFragments();
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        stlNavigationBar.setViewPager(vpContent, tabs);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), SystemUtil.getScreenDisplay(getContext()).widthPixels / 3));
    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new CollectVideoFragment());
        fragmentList.add(new CollectNewsFragment());
        fragmentList.add(new CollectFlashFragment());
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                //编辑
                break;
        }
    }
}
