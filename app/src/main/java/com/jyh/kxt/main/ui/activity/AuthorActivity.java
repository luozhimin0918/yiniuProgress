package com.jyh.kxt.main.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.fragment.VideoItemFragment;
import com.jyh.kxt.base.BaseActivity;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshListView;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:作者
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/3.
 */

public class AuthorActivity extends BaseActivity {
    @BindView(R.id.v_like) View vLike;
    @BindView(R.id.iv_photo) ImageView ivPhoto;
    @BindView(R.id.tv_name) TextView tvName;
    @BindView(R.id.tv_fans) TextView tvFans;
    @BindView(R.id.tv_ding) TextView tvDing;
    @BindView(R.id.tv_article) TextView tvArticle;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.pl_rootView) PageLoadLayout plRootView;
    @BindView(R.id.vp_content) ViewPager vpContent;
    private boolean isLike;

    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_author, StatusBarColor.NO_COLOR);

        String[] tabs = new String[]{"文章", "简介"};

        initFragments();

        stlNavigationBar.setViewPager(vpContent,tabs);

    }

    private void initFragments() {
        fragmentList=new ArrayList<>();
        fragmentList.add(new Fragment());
        fragmentList.add(new Fragment());
    }

    @OnClick({R.id.iv_break, R.id.v_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_break:
                onBackPressed();
                break;
            case R.id.v_like:
                isLike = !isLike;
                vLike.setSelected(isLike);
                break;
        }
    }
}
