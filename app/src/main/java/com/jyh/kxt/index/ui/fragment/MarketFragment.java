package com.jyh.kxt.index.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.IntentConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.market.ui.MarketEditActivity;
import com.jyh.kxt.market.ui.SearchActivity;
import com.jyh.kxt.market.ui.fragment.MarketVPFragment;
import com.jyh.kxt.market.ui.fragment.OptionalFragment;
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
 * 首页-行情
 */
public class MarketFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.tv_right_icon1) TextView tvRightIcon1;

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

    public BaseFragment marketVPFragment, optionalFragment;
    private BaseFragment lastFragment;

    public static MarketFragment newInstance() {
        MarketFragment fragment = new MarketFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_market, LibActivity.StatusBarColor.THEME1);

        String[] mTitles = getResources().getStringArray(R.array.nav_market);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);
        changeUserImg(LoginUtils.getUserInfo(getContext()));

        onTabSelect(0);
    }

    private int position = 0;

    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            tvRightIcon1.setText("");
            tvRightIcon1.setBackground(ContextCompat.getDrawable(getContext(), R.mipmap.icon_search));
            currentFragment = marketVPFragment = marketVPFragment == null ? new MarketVPFragment() : marketVPFragment;
            ((MarketVPFragment) marketVPFragment).sendSocketParams();
        } else {
            tvRightIcon1.setText("编辑");
            tvRightIcon1.setBackground(null);
            currentFragment = optionalFragment = optionalFragment == null ? new OptionalFragment() : optionalFragment;
            ((OptionalFragment) optionalFragment).sendSocketParams();
        }
        this.position = position;
        replaceFragment(currentFragment);
        lastFragment = currentFragment;
    }

    public int getTabSelectPosition() {
        return position;
    }

    @Override
    public void onTabReselect(int position) {

    }

    @OnClick({R.id.iv_left_icon, R.id.tv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                ((MainActivity) getActivity()).showUserCenter();
                break;
            case R.id.tv_right_icon1:
                if (position == 1) {//如果是行情 - 编辑
                    Intent marketEditIntent = new Intent(MarketFragment.this.getContext(), MarketEditActivity.class);
                    startActivity(marketEditIntent);
                } else {
                    startActivity(new Intent(getContext(), SearchActivity.class));
                }
                break;
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentConstant.REQUESTCODE1 && resultCode == Activity.RESULT_OK && marketVPFragment !=
                null) {
            marketVPFragment.onActivityResult(requestCode, resultCode, data);
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
        if (marketVPFragment != null) {
            marketVPFragment.onChangeTheme();
        }
        if (optionalFragment != null) {
            optionalFragment.onChangeTheme();
        }
    }

    public void doubleClickFragment() {
        try {
            onTabSelect(0);
            stlNavigationBar.setCurrentTab(0);
            MarketVPFragment doubleClickMarketVPFragment = (MarketVPFragment) marketVPFragment;
            doubleClickMarketVPFragment.stlNavigationBar.setCurrentTab(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!isHidden()) {
                ((MarketVPFragment) marketVPFragment).sendSocketParams();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
