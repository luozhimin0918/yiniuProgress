package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.base.widget.night.ThemeUtil;
import com.jyh.kxt.index.json.MainInitJson;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.main.adapter.FastInfoAdapter;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;
import com.library.widget.window.ToastView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
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

    public NewsFragment newsFragment;
    private FlashFragment flashFragment;
    private BaseFragment lastFragment;
    private BaseFragment currentFragment;

    private boolean isShowRightTopAdvert = false;
    private boolean flashTop;
    private boolean flashSound;

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
        onTabSelect(0);
    }


    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            currentFragment = newsFragment = newsFragment == null ? new NewsFragment() : newsFragment;
        } else {
            currentFragment = flashFragment = flashFragment == null ? new FlashFragment() : flashFragment;
        }
        replaceFragment(currentFragment);

        stlNavigationBar.setCurrentTab(position);
        lastFragment = currentFragment;
        changeRightIcon(position);
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
            setGifIcon();
        } else {
            currentFragment = flashFragment;

            ivRightIcon1.setVisibility(View.VISIBLE);
            ivRightIcon1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_rili_sx));
        }
    }

    @Override
    public void onTabReselect(int position) {
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
                try {
                    if (currentFragment instanceof NewsFragment) {
                        showPopWindowAdvert();
                    } else {
                        FastInfoAdapter adapter = flashFragment.flashPresenter.adapter;
                        if (adapter == null || adapter.isAdapterNullData()) {
                            ToastView.makeText3(getContext(), "暂无可筛选数据");
                            return;
                        }
                        flashFiltrate();  //快讯筛选
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public void closePopWindowAdvert() {
        isShowRightTopAdvert = true;
        ivRightIcon1.setVisibility(View.VISIBLE);
        if (currentFragment instanceof NewsFragment) {
            setGifIcon();
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
        final SwitchCompat scTop = (SwitchCompat) view.findViewById(R.id.sc_top);
        final SwitchCompat scSound = (SwitchCompat) view.findViewById(R.id.sc_sound);

        Set<String> set = SPUtils.getStringSet(getContext(), SpConstant.FLASH_FILTRATE);
        scHigh.setChecked(onlyShowHigh = SPUtils.getBoolean(getContext(), SpConstant.FLASH_FILTRATE_HIGH));
        scTop.setChecked(flashTop = SPUtils.getBooleanTrue(getContext(), SpConstant.FLASH_FILTRATE_TOP));
        scSound.setChecked(flashSound = SPUtils.getBooleanTrue(getContext(), SpConstant.FLASH_FILTRATE_SOUND));
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

        filtratePopup.showAtLocation(stlNavigationBar, Gravity.BOTTOM, 0, 0);

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
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_TOP, flashTop);
                SPUtils.save(getContext(), SpConstant.FLASH_FILTRATE_SOUND, flashSound);
                EventBus.getDefault().post(new EventBusClass(EventBusClass.EVENT_FLASH_FILTRATE, null));
                flashFragment.flashFiltrate();
                filtratePopup.dismiss();
            }
        });


        scHigh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onlyShowHigh = isChecked;
            }
        });
        scTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flashTop = isChecked;
            }
        });
        scSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flashSound = isChecked;
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
        if (getContext() != null && newsFragment == currentFragment) {
            setGifIcon();
        }
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

    private void setGifIcon() {
        if (getContext() != null) {
            try {
                MainInitJson mainInitJson = JSON.parseObject(SPUtils.getString(getContext(), SpConstant
                        .INIT_LOAD_APP_CONFIG), MainInitJson
                        .class);
                String icon = mainInitJson.getIndex_ad().getIcon();
                if (TextUtils.isEmpty(icon)) {
                    int theme = ThemeUtil.getAlertTheme(getContext());
                    switch (theme) {
                        case android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_Dialog_Alert:
                            Glide.with(getContext() ).load(R.mipmap.icon_advert_night).into(new GlideDrawableImageViewTarget
                                    (ivRightIcon1));
                            break;
                        case android.support.v7.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert:
                            Glide.with(getContext() ).load(R.mipmap.icon_advert_day).into(new GlideDrawableImageViewTarget
                                    (ivRightIcon1));
                            break;
                    }

                } else {
                    Glide.with(getContext()).load(icon).into(new GlideDrawableImageViewTarget(ivRightIcon1));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
