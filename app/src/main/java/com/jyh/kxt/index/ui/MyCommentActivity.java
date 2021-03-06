package com.jyh.kxt.index.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.index.ui.fragment.MyCommentFragment;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyCommentActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();
    private String[] tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment, StatusBarColor.THEME1);

        tvBarTitle.setText("我的发言");

        tabs = new String[2];
        MyCommentFragment commentMy = new MyCommentFragment();
        Bundle commentMyBundle = new Bundle();
        commentMyBundle.putInt("from", 1);
        commentMy.setArguments(commentMyBundle);
        fragmentList.add(commentMy)


        ;

        MyCommentFragment commentReply = new MyCommentFragment();
        Bundle commentReplyBundle = new Bundle();
        commentReplyBundle.putInt("from", 0);
        commentReply.setArguments(commentReplyBundle);
        fragmentList.add(commentReply);

        tabs[0] = "我的评论";
        tabs[1] = "回复我的";

        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        vpContent.addOnPageChangeListener(this);
        stlNavigationBar.setViewPager(vpContent, tabs);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));
    }

    @OnClick({R.id.iv_bar_break})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
        }
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
