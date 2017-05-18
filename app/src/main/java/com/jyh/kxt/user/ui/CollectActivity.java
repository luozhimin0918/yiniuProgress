package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.user.ui.fragment.CollectFlashFragment;
import com.jyh.kxt.user.ui.fragment.CollectNewsFragment;
import com.jyh.kxt.user.ui.fragment.CollectVideoFragment;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;

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

public class CollectActivity extends BaseActivity implements DelNumListener{

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ViewPager vpContent;
    @BindView(R.id.iv_selAll) ImageView ivSelAll;
    @BindView(R.id.tv_del) TextView tvDel;
    @BindView(R.id.ll_del) LinearLayout llDel;

    private List<Fragment> fragmentList;
    private CollectVideoFragment videoFragment;
    private CollectNewsFragment newsFragment;
    private CollectFlashFragment flashFragment;
    private DelNumListener numListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collect, StatusBarColor.THEME1);
        ButterKnife.bind(this);

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
        videoFragment = new CollectVideoFragment();
        fragmentList.add(videoFragment);
        newsFragment = new CollectNewsFragment();
        fragmentList.add(newsFragment);
        flashFragment = new CollectFlashFragment();
        fragmentList.add(flashFragment);

        numListener = this;
    }

    @OnClick({R.id.iv_bar_break, R.id.iv_bar_function, R.id.ll_selAll, R.id.tv_del})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_bar_break:
                onBackPressed();
                break;
            case R.id.iv_bar_function:
                //编辑
                boolean isShowEdit = false;
                switch (vpContent.getCurrentItem()) {
                    case 0:
                        if (videoFragment.adapter != null && videoFragment.adapter.getData().size() > 0) {
                            isShowEdit = true;
                        } else {
                            isShowEdit = false;
                        }
                        videoFragment.edit(numListener);
                        break;
                    case 1:
                        if (newsFragment.adapter != null && newsFragment.adapter.getData().size() > 0) {
                            isShowEdit = true;
                        } else {
                            isShowEdit = false;
                        }
                        newsFragment.edit(numListener);
                        break;
                    case 2:
                        if (flashFragment.adapter != null && flashFragment.adapter.getData().size() > 0) {
                            isShowEdit = true;
                        } else {
                            isShowEdit = false;
                        }
                        flashFragment.edit(numListener);
                        break;
                }
                if (isShowEdit) {
                    llDel.setVisibility(View.VISIBLE);
                    ivBarFunction.setText("取消");
                } else {
                    llDel.setVisibility(View.GONE);
                    ivBarFunction.setText("编辑");
                }
                break;
            case R.id.ll_selAll:
                //全选
                if (ivSelAll.isSelected()) {
                    ivSelAll.setSelected(false);
                } else {
                    ivSelAll.setSelected(true);
                }
                switch (vpContent.getCurrentItem()) {
                    case 0:
                        videoFragment.selAll(ivSelAll.isSelected(), numListener);
                        break;
                    case 1:
                        newsFragment.selAll(ivSelAll.isSelected(), numListener);
                        break;
                    case 2:
                        flashFragment.selAll(ivSelAll.isSelected(), numListener);
                        break;
                }
                break;
            case R.id.tv_del:
                //删除
                switch (vpContent.getCurrentItem()) {
                    case 0:
                        videoFragment.del(numListener);
                        break;
                    case 1:
                        newsFragment.del(numListener);
                        break;
                    case 2:
                        flashFragment.del(numListener);
                        break;
                }
                quitEdit();
                break;
        }
    }

    private void quitEdit() {
        llDel.setVisibility(View.GONE);
        ivSelAll.setSelected(false);
        switch (vpContent.getCurrentItem()) {
            case 0:
                videoFragment.quitEdit(numListener);
                break;
            case 1:
                newsFragment.quitEdit(numListener);
                break;
            case 2:
                flashFragment.quitEdit(numListener);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (llDel.getVisibility() == View.VISIBLE) {
            quitEdit();
        } else
            super.onBackPressed();
    }

    @Override
    public void delItem(Integer num) {
        try {
            tvDel.setText("删除(" + num + ")");
            ivBarFunction.setText("编辑");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delError() {

    }

}
