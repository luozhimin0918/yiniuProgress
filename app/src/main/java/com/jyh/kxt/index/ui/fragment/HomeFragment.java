package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-要闻
 */
public class HomeFragment extends BaseFragment implements OnTabSelectListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;
    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;
    @BindView(R.id.vp_content) public ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();
    private NewsFragment newsFragment;
    private FlashFragment flashFrament;
    private BaseFragment currentFragment;

    private int position = 0;

    private boolean isShowRightTopAdvert = false;

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


        newsFragment = new NewsFragment();
        flashFrament = new FlashFragment();

        changeUserImg(LoginUtils.getUserInfo(getContext()));

        fragmentList.add(newsFragment);
        fragmentList.add(flashFrament);

        currentFragment = newsFragment;

        vpContent.addOnPageChangeListener(this);
        vpContent.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragmentList));
        onTabSelect(0);
    }


    @Override
    public void onTabSelect(int position) {
        this.position = position;
        vpContent.setCurrentItem(position);
        changeRightIcon(position);
    }

    /**
     * 更改右上角图片
     *
     * @param position
     */
    private void changeRightIcon(int position) {
        if (position == 0) {

            currentFragment = newsFragment;

            if (!isShowRightTopAdvert) {
                ivRightIcon1.setVisibility(View.GONE);
            } else {
                ivRightIcon1.setVisibility(View.VISIBLE);
            }

            ivRightIcon1.setImageResource(R.mipmap.icon_advert);
        } else {
            currentFragment = flashFrament;

            ivRightIcon1.setVisibility(View.VISIBLE);
            ivRightIcon1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_rili_sx));
        }
    }

    @Override
    public void onTabReselect(int position) {
        this.position = position;
        stlNavigationBar.setCurrentTab(position);
    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon2, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                //个人中心
                ((MainActivity) getActivity()).showUserCenter();
                break;
            case R.id.iv_right_icon2:
                break;
            case R.id.iv_right_icon1:
                if (currentFragment instanceof NewsFragment) {
                    showPopWindowAdvert();
                } else {
                    flashFiltrate();  //快讯筛选
                }
                break;
        }
    }

    public void closePopWindowAdvert() {
        if (currentFragment instanceof NewsFragment) {
            isShowRightTopAdvert = true;
            ivRightIcon1.setVisibility(View.VISIBLE);
            ivRightIcon1.setImageResource(R.mipmap.icon_advert);
        }
    }

    public void showPopWindowAdvert() {
        try {
            String appConfig = SPUtils.getString(getContext(), SpConstant.INIT_LOAD_APP_CONFIG);

            MainActivity mainActivity = (MainActivity) getActivity();

            MainInitJson mainInitJson = JSONObject.parseObject(appConfig, MainInitJson.class);
            MainInitJson.IndexAdBean indexAd = mainInitJson.getIndex_ad();
            mainActivity.mainPresenter.showPopAdvertisement(indexAd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过反射获取图片id
     *
     * @param imageView
     * @return
     */
    public int getImageId(ImageView imageView) {
        Field[] fields = imageView.getClass().getDeclaredFields();
        int imgid = 0;
        for (Field f : fields) {
            if (f.getName().equals("mResource")) {
                f.setAccessible(true);
                try {
                    imgid = f.getInt(imageView);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return imgid;
    }

    /**
     * 快讯筛选
     */
    boolean onlyShowHigh = false;

    private void flashFiltrate() {
        final PopupUtil filtratePopup = new PopupUtil(getActivity());
        View view = filtratePopup.createPopupView(R.layout.pop_flash_filtrate);

        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        TextView tvSure = (TextView) view.findViewById(R.id.tv_sure);
        final OptionLayout olContent = (OptionLayout) view.findViewById(R.id.ol_content);

        olContent.setMinSelectCount(1);
        olContent.setMaxSelectCount(3);
        olContent.setSelectMode(OptionLayout.SelectMode.CheckMode);

        final SwitchCompat scHigh = (SwitchCompat) view.findViewById(R.id.sc_high);

        Set<String> set = SPUtils.getStringSet(getContext(), SpConstant.FLASH_FILTRATE);
        scHigh.setChecked(SPUtils.getBoolean(getContext(), SpConstant.FLASH_FILTRATE_HIGH));
        if (set.size() == 0 || set.size() == 3) {
            set.clear();
            set.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array.flash_silver)));
            olContent.setSelectItemIndex(set);
        } else {
            olContent.setSelectItemIndex(set);
        }

        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = WindowManager.LayoutParams.WRAP_CONTENT;
        filtratePopup.setConfig(config);

        filtratePopup.showAtLocation(vpContent, Gravity.BOTTOM, 0, 0);

        filtratePopup.setOnDismissListener(new PopupUtil.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> set = new HashSet<>();
                set.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array.flash_silver)));
                olContent.setSelectItemIndex(set);
                scHigh.setChecked(false);
            }
        });

        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE, olContent.getSelectedMap());
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_HIGH, onlyShowHigh);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_FLASH_FILTRATE, null));
                flashFrament.flashFiltrate();
                filtratePopup.dismiss();
            }
        });


        scHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlyShowHigh = isChecked;
            }
        });

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        stlNavigationBar.setCurrentTab(position);
        changeRightIcon(position);
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
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onChangeTheme();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (!isHidden()) { //没有被隐藏
                if (newsFragment != null) {
                    newsFragment.sendSocketParams();
                }
            }
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
        if (fragmentList != null) {
            for (Fragment fragment : fragmentList) {
                if (fragment instanceof BaseFragment) {
                    ((BaseFragment) fragment).onNetChange(netMobile);
                }
            }
        }
    }
}
