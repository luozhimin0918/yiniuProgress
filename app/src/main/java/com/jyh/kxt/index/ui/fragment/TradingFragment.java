package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.search.ui.SearchActivity;
import com.jyh.kxt.trading.ui.fragment.ArticleFragment;
import com.jyh.kxt.trading.ui.fragment.ViewpointFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名:Kxt
 * 类描述:
 * 创建人:苟蒙蒙
 * 创建日期:2017/7/26.
 */

public class TradingFragment extends BaseFragment implements OnTabSelectListener {
    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;
    @BindView(R.id.fl_content) FrameLayout flContent;
    private BaseFragment lastFragment;
    private ArticleFragment articleFragment;
    private ViewpointFragment viewpointFragment;
    private int index;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_trading, LibActivity.StatusBarColor.THEME1);

        ivRightIcon1.setImageResource(R.mipmap.icon_search);

        String[] mTitles = getResources().getStringArray(R.array.nav_trading);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);
        changeUserImg(LoginUtils.getUserInfo(getContext()));
        onTabSelect(0);
    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                ((MainActivity) getActivity()).showUserCenter();
                break;
            case R.id.iv_right_icon1:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                if (index == 0) {
                    intent.putExtra(SearchActivity.TYPE, VarConstant.SEARCH_TYPE_VIEWPOINT);
                } else {
                    intent.putExtra(SearchActivity.TYPE, VarConstant.SEARCH_TYPE_BLOG);
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        if (articleFragment != null) {
            articleFragment.onNetChange(netMobile);
        }
        if (viewpointFragment != null) {
            viewpointFragment.onNetChange(netMobile);
        }
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        stlNavigationBar.setBarStrokeColor(
                ContextCompat.getColor(getContext(), R.color.segmentTabLayout_indicator_color));
        ivRightIcon1.setImageResource(R.mipmap.icon_search);
        if (articleFragment != null) {
            articleFragment.onChangeTheme();
        }
        if (viewpointFragment != null) {
            viewpointFragment.onChangeTheme();
        }
    }

    @Override
    public void onTabSelect(int position) {
        this.index = position;
        BaseFragment currentFragment;
        if (position == 1) {
            currentFragment = articleFragment = articleFragment == null ? new ArticleFragment() : articleFragment;
        } else {
            currentFragment = viewpointFragment = viewpointFragment == null ? new ViewpointFragment() :
                    viewpointFragment;
        }
        replaceFragment(currentFragment);

        stlNavigationBar.setCurrentTab(position);
        lastFragment = currentFragment;
    }

    private void replaceFragment(BaseFragment toFragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (lastFragment != null) {
            transaction.hide(lastFragment);
        }
        if (toFragment.isAdded()) {
            transaction.show(toFragment);
        } else {
            transaction.add(R.id.fl_content, toFragment, toFragment instanceof ViewpointFragment ?
                    "ViewpointFragment" : null);
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onTabReselect(int position) {
        stlNavigationBar.setCurrentTab(position);
    }

    public void doubleClickFragment() {
        try {
            onTabSelect(0);
            stlNavigationBar.setCurrentTab(0);
            if(viewpointFragment!= null){
                viewpointFragment.doubleClickFragment();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (lastFragment != null) {
            lastFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
                UserJson userJson = (UserJson) eventBus.intentObj;
                changeUserImg(userJson);
                break;
            case EventBusClass.EVENT_LOGOUT:
                changeUserImg(null);
                break;
            case EventBusClass.EVENT_CHANGEUSERINFO:
                changeUserImg((UserJson) eventBus.intentObj);
                break;
        }
    }

    private void changeUserImg(UserJson user) {
        try {
            if (user == null) {
                ivLeftIcon.setImageResource(R.mipmap.icon_user_def_photo);
            } else {
                Glide.with(getContext()).load(user.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo)
                        .placeholder(R.mipmap.icon_user_def_photo).into(ivLeftIcon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
