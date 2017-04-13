package com.jyh.kxt.index.ui.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.library.base.LibActivity;
import com.library.util.SystemUtil;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页-数据
 */
public class DatumFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

    @BindView(R.id.iv_right_icon2) ImageView ivCalendar;
    @BindView(R.id.iv_right_icon1) ImageView ivFiltrate;

    private BaseFragment dataFragment, calendarFragment;
    private BaseFragment lastFragment;

    public static DatumFragment newInstance() {
        DatumFragment fragment = new DatumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_datum, LibActivity.StatusBarColor.THEME1);

        ivCalendar.setVisibility(View.VISIBLE);
        ivFiltrate.setImageResource(R.mipmap.icon_rili_sx);

        String[] mTitles = getResources().getStringArray(R.array.nav_datum);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        onTabSelect(0);

    }

    @OnClick({R.id.iv_right_icon1})
    public void onNavClick(View view) {

        DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(getContext());
        int popHeight = screenDisplay.heightPixels - SystemUtil.dp2px(getContext(), 115);

        PopupUtil filtratePopup = new PopupUtil(getActivity());
        filtratePopup.createPopupView(R.layout.pop_calendar_filtrate);
        PopupUtil.Config config = new PopupUtil.Config();

        config.outsideTouchable = true;
        config.alpha = 0.5f;
        config.bgColor = 0X00000000;

        config.animationStyle = R.style.PopupWindow_Style2;
        config.width = WindowManager.LayoutParams.MATCH_PARENT;
        config.height = popHeight;
        filtratePopup.setConfig(config);

        filtratePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        filtratePopup.setOnDismissListener(new PopupUtil.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
    }

    @Override
    public void onTabSelect(int position) {
        BaseFragment currentFragment;
        if (position == 0) {
            currentFragment = calendarFragment = calendarFragment == null ? new CalendarFragment() : calendarFragment;
        } else {
            currentFragment = dataFragment = dataFragment == null ? new DataFragment() : dataFragment;
        }
        replaceFragment(currentFragment);
        lastFragment = currentFragment;
    }

    @Override
    public void onTabReselect(int position) {

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
     * 子 Fragment 调用
     *
     * @param monthToImageView
     */
    public void setMonthToImageView(Bitmap monthToImageView) {
        ivCalendar.setImageBitmap(monthToImageView);
    }
}
