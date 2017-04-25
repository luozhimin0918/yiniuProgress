package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;

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

    public static enum AdapterType {
        TITLE, CONTENT1, CONTENT2, CONTENT3, NO_DATA
    }

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

        int itemPosition = (calendarPresenter.generateItemCount + 1) / 2;
        vpCalendarList.addOnPageChangeListener(this);
        vpCalendarList.setCurrentItem(itemPosition);
        calendarPresenter.updateSelectedColor(itemPosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        calendarPresenter.updateSelectedColor(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * 得到Fragment 对应的时间
     *
     * @param fragment
     * @return
     */
    public String getCalendarDate(Fragment fragment) {
        int indexOf = fragmentList.indexOf(fragment);
        Long aLong = calendarPresenter.dataLongList.get(indexOf);
        String mCalendarDate = (String) DateFormat.format("yyyy-MM-dd", aLong);
        return mCalendarDate;
    }

    public void gotoCorrespondItem(long timeInMillis) {
        int indexOf = calendarPresenter.dataLongList.indexOf(timeInMillis);
        if (indexOf != -1) {

            vpCalendarList.setCurrentItem(indexOf);
            calendarPresenter.updateSelectedColor(indexOf);

        } else {//重新所有数据
            fragmentList.clear();
            vpCalendarList.removeAllViews();

            calendarPresenter.generateDateItem(System.currentTimeMillis());
            createItemFragments();
        }
    }
}
