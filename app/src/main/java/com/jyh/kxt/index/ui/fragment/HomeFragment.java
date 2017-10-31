package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-要闻
 */
public class HomeFragment extends BaseFragment implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.bar_red_dot) TextView tvRedDot;

    @BindView(R.id.fl_bar_fun) FrameLayout flActionBarFun;

    public NewsFragment newsFragment;
    private FlashFragment flashFragment;

    private BaseFragment lastFragment;
    private BaseFragment currentFragment;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home, LibActivity.StatusBarColor.THEME1);

        String[] mTitles = getResources().getStringArray(R.array.nav_index);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);
        changeUserImg(LoginUtils.getUserInfo(getContext()));

        stlNavigationBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                onTabSelect(0);
            }
        },600);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
            case EventBusClass.EVENT_LOGIN_UPDATE:
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

    @Override
    public void onTabReselect(int position) {

    }

    @Override
    public void onTabSelect(int position) {
        if (position == 0) {
            currentFragment = newsFragment = newsFragment == null ? new NewsFragment() : newsFragment;
            newsFragment.onTabSelect(flActionBarFun);
        } else {
            currentFragment = flashFragment = flashFragment == null ? new FlashFragment() : flashFragment;
            flashFragment.onTabSelect(flActionBarFun);
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
            transaction.add(R.id.fl_content, toFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    @OnClick({R.id.iv_left_icon})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                //个人中心
                ((MainActivity) getActivity()).showUserCenter();
                break;
        }
    }

    public void closePopWindowAdvert() {

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stlNavigationBar.setCurrentTab(position);

        if (position == 0) {
            newsFragment.onTabSelect(flActionBarFun);
        } else {
            flashFragment.onTabSelect(flActionBarFun);
        }
    }

    public void notifyNavLayout() {
        int currentTab = stlNavigationBar.getCurrentTab();
        onPageSelected(currentTab);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (currentFragment != null) {
            currentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void changeUserImg(UserJson user) {
        try {
            if (user == null) {
                ivLeftIcon.setImageResource(R.mipmap.icon_user_def_photo);
                tvRedDot.setVisibility(View.GONE);
            } else {
                Glide.with(getContext()).load(user.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo)
                        .placeholder(R.mipmap.icon_user_def_photo).into(ivLeftIcon);
                if (user.getIs_unread_msg() == 1|| user.getIs_unread_reply() == 1) {
                    tvRedDot.setVisibility(View.VISIBLE);
                } else {
                    tvRedDot.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    public void onChangeTheme() {
        super.onChangeTheme();

        if (newsFragment != null) {
            newsFragment.onChangeTheme();
        }
        if (flashFragment != null) {
            flashFragment.onChangeTheme();
        }

        stlNavigationBar.setBarStrokeColor(
                ContextCompat.getColor(getContext(), R.color.segmentTabLayout_indicator_color));
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isResumed()) { //没有被隐藏
                if (newsFragment != null) {
                    newsFragment.sendSocketParams();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void doubleClickFragment() {
        try {
            onTabSelect(0);
            stlNavigationBar.setCurrentTab(0);
            newsFragment.stlNavigationBar.setCurrentTab(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetChange(int netMobile) {
        super.onNetChange(netMobile);
        if (newsFragment != null) {
            newsFragment.onNetChange(netMobile);
        }
        if (flashFragment != null) {
            flashFragment.onNetChange(netMobile);
        }
    }
}
