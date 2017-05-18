package com.jyh.kxt.index.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.widget.OptionLayout;
import com.jyh.kxt.main.ui.fragment.FlashFragment;
import com.jyh.kxt.main.ui.fragment.NewsFragment;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.util.SPUtils;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import org.greenrobot.eventbus.EventBus;

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
    @BindView(R.id.iv_left_icon) ImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivRightIcon2;
    @BindView(R.id.iv_right_icon1) ImageView ivRightIcon1;
    @BindView(R.id.vp_content)public ViewPager vpContent;

    private List<Fragment> fragmentList = new ArrayList<>();
    private BaseFragment newsFragment;
    private BaseFragment flashFrament;
    private BaseFragment currentFragment;

    private int position = 0;

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

        ivRightIcon1.setImageResource(R.mipmap.icon_search);

        newsFragment = new NewsFragment();
        flashFrament = new FlashFragment();

        fragmentList.add(newsFragment);
        fragmentList.add(flashFrament);

        vpContent.addOnPageChangeListener(this);
        vpContent.setAdapter(new BaseFragmentAdapter(getChildFragmentManager(), fragmentList));
        onTabSelect(0);
    }


    @Override
    public void onTabSelect(int position) {
        this.position = position;
        vpContent.setCurrentItem(position);
        if (position == 0) {
            currentFragment = newsFragment;
            ivRightIcon1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_search));
        } else {
            currentFragment = flashFrament;
            ivRightIcon1.setImageDrawable(ContextCompat.getDrawable(getContext(), R.mipmap.icon_rili_sx));
        }
    }

    @Override
    public void onTabReselect(int position) {

    }

    @OnClick({R.id.iv_left_icon, R.id.iv_right_icon2, R.id.iv_right_icon1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                //个人中心
                break;
            case R.id.iv_right_icon2:
                break;
            case R.id.iv_right_icon1:
                if (position == 0) {
                    //搜索
                } else {
                    //快讯筛选
                    flashFiltrate();
                }
                break;
        }
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
        final SwitchCompat scHigh = (SwitchCompat) view.findViewById(R.id.sc_high);

        Set<String> set = SPUtils.getStringSet(getContext(), SpConstant.FLASH_FILTRATE);
        scHigh.setChecked(SPUtils.getBoolean(getContext(), SpConstant.FLASH_FILTRATE_HIGH));
        if (set == null || set.size() == 0 || set.size() == 3) {
            set = new HashSet<>();
            set.addAll(Arrays.asList(getContext().getResources().getStringArray(R.array
                    .flash_silver)));
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (currentFragment != null)
            currentFragment.onActivityResult(requestCode, resultCode, data);
    }
}
