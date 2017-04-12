package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.datum.presenter.CalendarPresenter;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-日历
 */
public class CalendarFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @BindView(R.id.stl_navigation_bar) public SlidingTabLayout stlNavigationBar;
    @BindView(R.id.vp_calendar_list) public ViewPager vpCalendarList;

    private CalendarPresenter calendarPresenter;

    private List<Fragment> fragmentList = new ArrayList<>();//内容页面Fragment

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_calendar);

        calendarPresenter = new CalendarPresenter(this);
        calendarPresenter.generateDateItem(System.currentTimeMillis());

        createItemFragments();
    }

    public void createItemFragments() {
        String[] navTitles = calendarPresenter.navTitleList;
        for (int i = 0; i < navTitles.length; i++) {
            CalendarItemFragment calendarItemFragment = new CalendarItemFragment();
            fragmentList.add(calendarItemFragment);
        }

        FragmentManager fm = getChildFragmentManager();
        BaseFragmentAdapter pageAdapter = calendarPresenter.getPageAdapter(fm, fragmentList);
        vpCalendarList.setAdapter(pageAdapter);
        stlNavigationBar.setViewPager(vpCalendarList);

        vpCalendarList.setCurrentItem((calendarPresenter.generateItemCount + 1) / 2);
        vpCalendarList.addOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        TextView tabView = stlNavigationBar.getTitleView(position);
        calendarPresenter.updateSelectedColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
