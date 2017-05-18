package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.constant.SpConstant;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.datum.bean.CalendarImportantBean;
import com.jyh.kxt.datum.presenter.CalendarPresenter;
import com.library.util.SPUtils;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public Set<String> stateSet;
    public Set<String> importanceSet;
    public Set<String> areaSet;
    public Set<String> judgeSet;

    private List<Fragment> fragmentList = new ArrayList<>();//内容页面Fragment

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_calendar);

        calendarPresenter = new CalendarPresenter(this);
        calendarPresenter.generateDateItem(System.currentTimeMillis());

        initializeFiltrationSet();
        createItemFragments();
    }

    private FragmentManager fm;

    public void createItemFragments() {
        String[] navTitles = calendarPresenter.navTitleList;
        for (int i = 0; i < navTitles.length; i++) {
            CalendarItemFragment calendarItemFragment = new CalendarItemFragment();
            fragmentList.add(calendarItemFragment);
        }

        fm = getChildFragmentManager();

        BaseFragmentAdapter pageAdapter = calendarPresenter.getPageAdapter(fm, fragmentList);
        vpCalendarList.setAdapter(pageAdapter);
        stlNavigationBar.setViewPager(vpCalendarList);

        int itemPosition = calendarPresenter.generateItemCount / 2;
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

            calendarPresenter.generateDateItem(timeInMillis);
            createItemFragments();
        }
    }


    /**
     * 初始化过滤的Set集合
     */
    public void initializeFiltrationSet() {
        stateSet = SPUtils.getStringSet(getContext(), SpConstant.DATUM_STATE);
        importanceSet = SPUtils.getStringSet(getContext(), SpConstant.DATUM_IMPORTANCE);
        areaSet = SPUtils.getStringSet(getContext(), SpConstant.DATUM_AREA);
        judgeSet = SPUtils.getStringSet(getContext(), SpConstant.DATUM_JUDGE);
    }

    /**
     * 开始刷新
     */
    public void updateFiltration() {

        try {
            List<Fragment> fragments = fm.getFragments();
            for (int i = 0; i < fragments.size(); i++) {
                if (fragments.get(i) != null) {
                    CalendarItemFragment calendarItemFragment = (CalendarItemFragment) fragments.get(i);
                    calendarItemFragment.updateFiltration();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断日历数据是否满足条件
     *
     * @param mCalendarFinanceBean
     * @return
     */
    public boolean isFinanceMeetConditions(CalendarFinanceBean mCalendarFinanceBean) {
        if (stateSet != null) {

            boolean isPublished;
            try {
                Float.parseFloat(mCalendarFinanceBean.getReality().replace("%", ""));
                isPublished = true;
            } catch (NumberFormatException e) {
                isPublished = false;
            }

            if (!stateSet.contains("全部")) {
                String publishedState = isPublished ? "已公布" : "未公布";
                if (!stateSet.contains(publishedState)) {
                    return false;
                }
            }
        }

        if (areaSet != null) {
            if (!areaSet.contains("全部")) {
                if (!areaSet.contains(mCalendarFinanceBean.getState())) {
                    return false;
                }
            }
        }

        /*if (judgeSet != null) {//0 利多美元  1 利多金银石油 2 影响较小
            if (!judgeSet.contains("全部")) {
                switch (mCalendarFinanceBean.getEffecttype()) {
                    case 0:
                        if (!judgeSet.contains("外汇")) {
                            return false;
                        }
                        break;
                    case 1:
                        if (!judgeSet.contains("美元")) {
                            return false;
                        }
                        break;
                    case 2:

                        break;
                }
            }
        }*/
        if (importanceSet != null) {
            if (!importanceSet.contains("全部")) {
                if (!importanceSet.contains(mCalendarFinanceBean.getImportance())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断事件是否满足条件
     *
     * @param mCalendarImportantBean
     * @return
     */
    public boolean isImportantMeetConditions(CalendarImportantBean mCalendarImportantBean) {
        if (areaSet != null) {
            if (!areaSet.contains("全部")) {
                if (!areaSet.contains(mCalendarImportantBean.getState())) {
                    return false;
                }
            }
        }
        if (importanceSet != null) {
            if (!importanceSet.contains("全部")) {
                if (!importanceSet.contains(mCalendarImportantBean.getImportance())) {
                    return false;
                }
            }
        }
        return true;
    }
}
