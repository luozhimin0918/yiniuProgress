package com.jyh.kxt.index.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.library.base.LibActivity;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;

import butterknife.BindView;

/**
 * 首页-数据
 */
public class DatumFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

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

        String[] mTitles = getResources().getStringArray(R.array.nav_datum);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        onTabSelect(0);
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
}
