package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.adapter.CalendarItemAdapter;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.datum.bean.CalendarType;
import com.jyh.kxt.datum.presenter.CalendarItemPresenter;
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.library.util.ObserverCall;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshBase;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-日历 - Item
 */

public class CalendarItemFragment extends BaseFragment {
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.ptrlv_content) public PullToRefreshListView ptrlvContent;

    public CalendarItemPresenter mCalendarItemPresenter;
    public CalendarItemAdapter calendarItemAdapter;

    public String calendarDate;

    @Override
    protected void onInitialize(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_calendar_item);
    }

    @Override
    public void userVisibleHint() {
        super.userVisibleHint();

        calendarDate = getCalendarDate();
        mCalendarItemPresenter = new CalendarItemPresenter(this);

        pllContent.loadWait();

        ptrlvContent.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ptrlvContent.setDividerNull();
        ptrlvContent.getRefreshableView().setDividerHeight(0);
        ptrlvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mCalendarItemPresenter.calendarTypeList.clear();
                mCalendarItemPresenter.requestPublishData();
            }
        });
        mCalendarItemPresenter.requestPublishData();
    }

    public void setCalendarAdapter(List<CalendarType> calendarBeen) {
        ptrlvContent.onRefreshComplete();

        calendarItemAdapter = new CalendarItemAdapter(getContext(), calendarBeen);
        calendarItemAdapter.setParentFragment(this);
        ptrlvContent.setAdapter(calendarItemAdapter);


    }

    public String getCalendarDate() {
        CalendarFragment parentFragment = (CalendarFragment) getParentFragment();
        String calendarDate = parentFragment.getCalendarDate(this);
        return calendarDate;
    }

    public void showTimingWindow(CalendarType mCalendarType, ObserverCall<Integer> observerCall) {
        if (mCalendarType instanceof CalendarFinanceBean) {
            CalendarFinanceBean mCalendarFinanceBean = (CalendarFinanceBean) mCalendarType;
            String time = mCalendarFinanceBean.getTime();

            DatumFragment parentFragment = (DatumFragment) getParentFragment().getParentFragment();
            parentFragment.showTimingWindow(time, observerCall);
        }
    }

    public void addCityData(HashSet<String> hashSetCity, HashSet<String> hashSetSelectedCity) {
        CalendarFragment parentFragment = (CalendarFragment) getParentFragment();
        parentFragment.addCityDataToFragment(this,hashSetCity, hashSetSelectedCity);
    }

    @Override
    public void onChangeTheme() {
        super.onChangeTheme();
        if (ptrlvContent != null) {
            ptrlvContent.setDividerNull();
        }

        if (calendarItemAdapter != null) {
            calendarItemAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        RequestQueue queue = getQueue();
        if (calendarDate != null) {
            queue.cancelAll(calendarDate);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void updateFiltration() {
        mCalendarItemPresenter.updateOrAddAdapter(1);
    }

    public void resetFiltration() {
        try {
            pllContent.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCalendarItemPresenter.updateOrAddAdapter(2);
                }
            },500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
