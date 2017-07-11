package com.jyh.kxt.user.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseActivity;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.annotation.DelNumListener;
import com.jyh.kxt.base.annotation.ObserverData;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.user.ui.fragment.CollectFlashFragment;
import com.jyh.kxt.user.ui.fragment.CollectNewsFragment;
import com.jyh.kxt.user.ui.fragment.CollectVideoFragment;
import com.library.base.http.VarConstant;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SlidingTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;
import com.library.widget.viewpager.ControllableViewPager;
import com.library.widget.window.ToastView;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

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

public class CollectActivity extends BaseActivity implements DelNumListener, ViewPager.OnPageChangeListener, OnTabSelectListener {

    @BindView(R.id.iv_bar_break) ImageView ivBarBreak;
    @BindView(R.id.tv_bar_title) TextView tvBarTitle;
    @BindView(R.id.iv_bar_function) TextView ivBarFunction;
    @BindView(R.id.stl_navigation_bar) SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_content) ControllableViewPager vpContent;
    @BindView(R.id.iv_selAll) ImageView ivSelAll;
    @BindView(R.id.tv_del) TextView tvDel;
    @BindView(R.id.ll_del) LinearLayout llDel;

    private List<Fragment> fragmentList;
    private CollectVideoFragment videoFragment;
    private CollectNewsFragment newsFragment, authorFragment;
    private CollectFlashFragment flashFragment;
    private DelNumListener numListener;

    private boolean isVideoEdit, isNewsEdit, isFlashEdit, isAuthorEdit;
    private boolean isVideoAllSel, isNewsAllSel, isFlashAllSel, isAuthorAllSel;
    private TSnackbar snackBar;
    private int delNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_collect, StatusBarColor.THEME1);
        ButterKnife.bind(this);

        tvBarTitle.setText("我的收藏");
        ivBarFunction.setText("编辑");

        String[] tabs = new String[]{"视听", "文章", "快讯", "专栏"};

        initFragments();
        vpContent.setAdapter(new BaseFragmentAdapter(getSupportFragmentManager(), fragmentList));
        vpContent.addOnPageChangeListener(this);
        vpContent.setOffscreenPageLimit(4);
        stlNavigationBar.setViewPager(vpContent, tabs);
        stlNavigationBar.setTabWidth(SystemUtil.px2dp(getContext(), SystemUtil.getScreenDisplay(getContext()).widthPixels / 4));
        stlNavigationBar.setOnTabSelectListener(this);
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
                if (snackBar != null && snackBar.isShownOrQueued()) {
                    editMode(false);
                } else {

                    switch (vpContent.getCurrentItem()) {
                        case 0:
                            if (videoFragment.adapter != null && videoFragment.adapter.getData().size() > 0) {
                                isVideoEdit = !isVideoEdit;
                            } else {
                                isVideoEdit = false;
                            }
                            videoFragment.edit(isVideoEdit, numListener);
                            if (isVideoEdit) {
                                llDel.setVisibility(View.VISIBLE);
                                ivBarFunction.setText("取消");
                            } else {
                                llDel.setVisibility(View.GONE);
                                ivBarFunction.setText("编辑");
                            }
                            editMode(isVideoEdit);
                            break;
                        case 1:
                            if (newsFragment.adapter != null && newsFragment.adapter.getData().size() > 0) {
                                isNewsEdit = !isNewsEdit;
                            } else {
                                isNewsEdit = false;
                            }
                            newsFragment.edit(isNewsEdit, numListener);
                            if (isNewsEdit) {
                                llDel.setVisibility(View.VISIBLE);
                                ivBarFunction.setText("取消");
                            } else {
                                llDel.setVisibility(View.GONE);
                                ivBarFunction.setText("编辑");
                            }
                            editMode(isNewsEdit);
                            break;
                        case 2:
                            if (flashFragment.adapter != null && flashFragment.adapter.getData().size() > 0) {
                                isFlashEdit = !isFlashEdit;
                            } else {
                                isFlashEdit = false;
                            }
                            flashFragment.edit(isFlashEdit, numListener);
                            if (isFlashEdit) {
                                llDel.setVisibility(View.VISIBLE);
                                ivBarFunction.setText("取消");
                            } else {
                                llDel.setVisibility(View.GONE);
                                ivBarFunction.setText("编辑");
                            }
                            editMode(isFlashEdit);
                            break;
                        case 3:
                            if (authorFragment.adapter != null && authorFragment.adapter.getData().size() > 0) {
                                isAuthorEdit = !isAuthorEdit;
                            } else {
                                isAuthorEdit = false;
                            }
                            authorFragment.edit(isAuthorEdit, numListener);
                            if (isAuthorEdit) {
                                llDel.setVisibility(View.VISIBLE);
                                ivBarFunction.setText("取消");
                            } else {
                                llDel.setVisibility(View.GONE);
                                ivBarFunction.setText("编辑");
                            }
                            editMode(isAuthorEdit);
                            break;
                    }
                }

                break;
            case R.id.ll_selAll:
                //全选
                boolean selected = ivSelAll.isSelected();
                ivSelAll.setSelected(!selected);
                switch (vpContent.getCurrentItem()) {
                    case 0:
                        isVideoAllSel = !selected;
                        videoFragment.selAll(isVideoAllSel, numListener);
                        break;
                    case 1:
                        isNewsAllSel = !selected;
                        newsFragment.selAll(isNewsAllSel, numListener);
                        break;
                    case 2:
                        isFlashAllSel = !selected;
                        flashFragment.selAll(isFlashAllSel, numListener);
                        break;
                    case 3:
                        isAuthorAllSel = !selected;
                        authorFragment.selAll(isAuthorAllSel, numListener);
                        break;
                }

                break;
            case R.id.tv_del:
                //删除
                if (delNum != 0) {
                    snackBar = TSnackbar.make(tvDel, "删除收藏中...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
                    snackBar.setPromptThemBackground(Prompt.SUCCESS);
                    snackBar.addIconProgressLoading(0, true, false);
                    snackBar.show();
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
                        case 3:
                            authorFragment.del(numListener);
                            break;
                    }
                } else {
                    ToastView.makeText3(this, "请选中至少一项");
                }
                break;
        }
    }

    @Override
    public void quitEdit() {
        llDel.setVisibility(View.GONE);
        ivSelAll.setSelected(false);
        switch (vpContent.getCurrentItem()) {
            case 0:
                isVideoEdit = false;
                isVideoAllSel = false;
//                videoFragment.quitEdit(numListener);
                break;
            case 1:
                isNewsEdit = false;
                isNewsAllSel = false;
//                newsFragment.quitEdit(numListener);
                break;
            case 2:
                isFlashEdit = false;
                isFlashAllSel = false;
//                flashFragment.quitEdit(numListener);
                break;
            case 3:
                isAuthorEdit = false;
                isAuthorAllSel = false;
//                authorFragment.quitEdit(numListener);
                break;
        }
        ivBarFunction.setText("编辑");
        editMode(false);
    }

    @Override
    public void onBackPressed() {
        if (llDel.getVisibility() == View.VISIBLE) {
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
                case 3:
                    authorFragment.quitEdit(numListener);
                    break;
            }
        } else
            super.onBackPressed();
    }

    @Override
    public void delItem(Integer num) {
        try {
            tvDel.setText("删除(" + num + ")");
            this.delNum = num;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delError() {
        snackBar.setPromptThemBackground(Prompt.ERROR).setText("删除收藏失败").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delSuccessed() {
        snackBar.setPromptThemBackground(Prompt.SUCCESS).setText("删除收藏成功").setDuration(TSnackbar.LENGTH_LONG)
                .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                        .getDimensionPixelOffset(R.dimen.actionbar_height)).show();
    }

    @Override
    public void delAll(boolean isAll) {
        switch (vpContent.getCurrentItem()) {
            case 0:
                isVideoAllSel = isAll;
                break;
            case 1:
                isNewsAllSel = isAll;
                break;
            case 2:
                isFlashAllSel = isAll;
                break;
            case 3:
                isAuthorAllSel = isAll;
                break;
        }
        ivSelAll.setSelected(isAll);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (videoFragment != null && videoFragment.adapter != null) {
            videoFragment.adapter.notifyDefaul();
        }
        if (newsFragment != null && newsFragment.adapter != null) {
            newsFragment.adapter.notifyDefaul();
        }
        if (flashFragment != null && flashFragment.adapter != null) {
            flashFragment.adapter.notifyDefaul();
        }
        if (authorFragment != null && authorFragment.adapter != null) {
            authorFragment.adapter.notifyDefaul();
        }
        delNum = 0;
        tvDel.setText("删除(" + delNum + ")");
        isAuthorAllSel = false;
        isFlashAllSel = false;
        isNewsAllSel = false;
        isVideoAllSel = false;
        switch (position) {
            case 0:
                if (isVideoEdit) {
                    llDel.setVisibility(View.VISIBLE);
                    ivBarFunction.setText("取消");
                } else {
                    llDel.setVisibility(View.GONE);
                    ivBarFunction.setText("编辑");
                }
                ivSelAll.setSelected(isVideoAllSel);
                editMode(isVideoEdit);
                break;
            case 1:
                if (isNewsEdit) {
                    llDel.setVisibility(View.VISIBLE);
                    ivBarFunction.setText("取消");
                } else {
                    llDel.setVisibility(View.GONE);
                    ivBarFunction.setText("编辑");
                }
                ivSelAll.setSelected(isNewsAllSel);
                editMode(isNewsEdit);
                break;
            case 2:
                if (isFlashEdit) {
                    llDel.setVisibility(View.VISIBLE);
                    ivBarFunction.setText("取消");
                } else {
                    llDel.setVisibility(View.GONE);
                    ivBarFunction.setText("编辑");
                }
                ivSelAll.setSelected(isFlashAllSel);
                editMode(isFlashEdit);
                break;
            case 3:
                if (isAuthorEdit) {
                    llDel.setVisibility(View.VISIBLE);
                    ivBarFunction.setText("取消");
                } else {
                    llDel.setVisibility(View.GONE);
                    ivBarFunction.setText("编辑");
                }
                ivSelAll.setSelected(isAuthorAllSel);
                editMode(isAuthorEdit);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onChangeTheme() {
        super.onChangeTheme();
        if (flashFragment != null)
            flashFragment.onChangeTheme();
        if (newsFragment != null)
            newsFragment.onChangeTheme();
        if (videoFragment != null) {
            videoFragment.onChangeTheme();
        }
        if (authorFragment != null)
            authorFragment.onChangeTheme();
        if (stlNavigationBar != null)
            stlNavigationBar.notifyDataSetChanged();
    }

    private void editMode(boolean isEdit) {
        stlNavigationBar.setClickable(!isEdit);
        vpContent.setScrollable(!isEdit);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
