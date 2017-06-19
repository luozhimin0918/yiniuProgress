package com.jyh.kxt.index.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.av.ui.fragment.RankFragment;
import com.jyh.kxt.av.ui.fragment.VideoFragment;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.index.ui.SearchActivity;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.base.http.VarConstant;
import com.library.bean.EventBusClass;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-视听
 */
public class AvFragment extends BaseFragment implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    public static AvFragment newInstance() {
        AvFragment fragment = new AvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;

    public VideoFragment videoFragment;
    private RankFragment rankFragment;

    private BaseFragment lastFragment;
    private BaseFragment currentFragment;


    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_av, LibActivity.StatusBarColor.THEME1);

        ivRightIcon1.setImageResource(R.mipmap.icon_search);
        String[] mTitles = getResources().getStringArray(R.array.nav_audio_visual);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        changeUserImg(LoginUtils.getUserInfo(getContext()));
        onTabSelect(0);
    }

    @Override
    public void onTabSelect(int position) {
        if (position == 0) {
            currentFragment = videoFragment = videoFragment == null ? new VideoFragment() : videoFragment;
        } else {
            currentFragment = rankFragment = rankFragment == null ? new RankFragment() : rankFragment;
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


    @Override
    public void onTabReselect(int position) {

    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon2, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                ((MainActivity) getActivity()).showUserCenter();
                break;
            case R.id.iv_right_icon2:

                break;
            case R.id.iv_right_icon1:
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra(IntentConstant.TYPE, VarConstant.VIDEO);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stlNavigationBar.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (videoFragment != null) {
            videoFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (rankFragment != null) {
            rankFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void changeUserImg(UserJson user) {
        if (user == null) {
            ivLeftIcon.setImageResource(R.mipmap.icon_user_def_photo);
        } else {
            Glide.with(getContext()).load(user.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo)
                    .placeholder(R.mipmap
                            .icon_user_def_photo).into(ivLeftIcon);
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

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (videoFragment != null) {
            videoFragment.onChangeTheme();
        }
        if (rankFragment != null) {
            rankFragment.onChangeTheme();
        }
    }

    public void doubleClickFragment() {
        try {
            onTabSelect(0);
            stlNavigationBar.setCurrentTab(0);
            videoFragment.stlNavigationBar.setCurrentTab(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
