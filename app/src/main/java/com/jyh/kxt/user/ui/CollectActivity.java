package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.user.presenter.CollectActityPresenter;
import com.jyh.kxt.user.ui.fragment.CollectFlashFragment;
import com.jyh.kxt.user.ui.fragment.CollectNewsFragment;
import com.jyh.kxt.user.ui.fragment.CollectPointFragment;
import com.jyh.kxt.user.ui.fragment.CollectVideoFragment;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;
import com.library.widget.viewpager.ControllableViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:我的收藏
 * 创建人:苟蒙蒙
 * 创建日期:2017/5/2.
 */

public class CollectActivity extends BaseActivity {

    @BindView(R.id.iv_bar_break) public ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) public TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) public TextView ivBarFunction;
    @BindView(R.id.stl_navigation_bar) public SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) public ControllableViewPager vpContent;
    @BindView(R.id.iv_selAll) public ImageView ivSelAll;
    @BindView(R.id.tv_del) public TextView tvDel;
    @BindView(R.id.ll_del) public LinearLayout llDel;

    public List<Fragment> fragmentList;
    public CollectVideoFragment videoFragment;
    public CollectNewsFragment newsFragment, authorFragment;
    public CollectFlashFragment flashFragment;
    public CollectPointFragment pointFragment;


    public CollectActityPresenter collectActityPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collect, StatusBarColor.THEME1);
        ButterKnife.bind(this);
        collectActityPresenter=new CollectActityPresenter(this);
        tvBarTitle.setText("我的收藏");
        ivBarFunction.setText("编辑");

        String[] tabs = new String[]{"视听", "文章", "快讯", "专栏","观点"};

        initFragments();
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        vpContent.addOnPageChangeListener(collectActityPresenter);
//        vpContent.setOffscreenPageLimit(5);
        stlNavigationBar.setViewPager(vpContent, tabs);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), SystemUtil.getScreenDisplay(getContext()).widthPixels / 5));
        stlNavigationBar.setOnTabSelectListener(collectActityPresenter);
    }

    private void initFragments() {
        fragmentList = new ArrayList<>();
        videoFragment = new CollectVideoFragment();
        fragmentList.add(videoFragment);

        newsFragment = new CollectNewsFragment();
        Bundle newsBundle = new Bundle();
        newsBundle.putString(IntentConstant.TYPE, VarConstant.OCLASS_NEWS);
        newsFragment.setArguments(newsBundle);
        fragmentList.add(newsFragment);

        flashFragment = new CollectFlashFragment();
        fragmentList.add(flashFragment);

        authorFragment = new CollectNewsFragment();
        Bundle flashBundle = new Bundle();
        flashBundle.putString(IntentConstant.TYPE, VarConstant.OCLASS_BLOG);
        authorFragment.setArguments(flashBundle);
        fragmentList.add(authorFragment);

        pointFragment = new CollectPointFragment();
        fragmentList.add(pointFragment);
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.ll_selAll, R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                collectActityPresenter.editOpe();

                break;
            case R.id.ll_selAll:
                collectActityPresenter.selectAll();

                break;
            case R.id.tv_del:
                collectActityPresenter.deleteOpe();
                break;
        }
    }
    @Override
    public void onBackPressed() {
        collectActityPresenter.onBackPressed();
    }
    public void superBackPressed(){
        super.onBackPressed();
    }
    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
       collectActityPresenter.onChangeTheme();
    }


}
