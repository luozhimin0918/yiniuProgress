package com.jyh.kxt.score.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.score.ui.fragment.ScoreDetailFragment;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:KxtProfessional
 * 类描述:金币明细
 * 创建人:苟蒙蒙
 * 创建日期:2017/9/4.
 */

public class CoinHistoryActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_history, StatusBarColor.THEME1);

        initView();

    }

    private void initView() {
        ivBarFunction.setVisibility(View.INVISIBLE);
        tvBarTitle.setText("金币明细");
        String[] tabs = new String[]{"本月明细", "月度汇总"};
        List<Fragment> fragments = new ArrayList<>();
        ScoreDetailFragment fragmentDay = new ScoreDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ScoreDetailFragment.TYPE, ScoreDetailFragment.TYPE_DAY);
        fragmentDay.setArguments(bundle);
        ScoreDetailFragment fragmentMonth = new ScoreDetailFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putString(ScoreDetailFragment.TYPE, ScoreDetailFragment.TYPE_MONTH);
        fragmentMonth.setArguments(bundle2);
        fragments.add(fragmentDay);
        fragments.add(fragmentMonth);
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragments));
        stlNavigationBar.setViewPager(vpContent,tabs);
        stlNavigationBar.setCurrentTab(0);
        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(this);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(this, screenDisplay.widthPixels / 2));
    }

    @OnClick(R.id.iv_bar_break)
    public void onClick() {
        onBackPressed();
    }
}
