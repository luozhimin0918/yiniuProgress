package com.jyh.kxt.datum.ui.fragment;

import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.datum.adapter.CalendarItemAdapter;
import com.jyh.kxt.datum.bean.CalendarType;
import com.jyh.kxt.datum.presenter.CalendarItemPresenter;
import com.library.widget.PageLoadLayout;
import com.library.widget.handmark.PullToRefreshListView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by Mr'Dai on 2017/4/11.
 * 数据-日历 - Item
 */

public class CalendarItemFragment extends BaseFragment {
    @BindView(R.id.pll_content) public PageLoadLayout pllContent;
    @BindView(R.id.ptrlv_content) PullToRefreshListView ptrlvContent;

    private CalendarItemPresenter mCalendarItemPresenter;
    private CalendarItemAdapter calendarItemAdapter;

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
        mCalendarItemPresenter.requestPublishData();
    }

    public void setCalendarAdapter(List<CalendarType> calendarBeen) {
        calendarItemAdapter = new CalendarItemAdapter(getContext(), calendarBeen);
        ptrlvContent.getRefreshableView().setDividerHeight(0);
        ptrlvContent.setAdapter(calendarItemAdapter);
    }

    public String getCalendarDate() {
        CalendarFragment parentFragment = (CalendarFragment) getParentFragment();
        String calendarDate = parentFragment.getCalendarDate(this);
        return calendarDate;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        RequestQueue queue = getQueue();
        if (calendarDate != null) {
            queue.cancelAll(calendarDate);
        }
    }
}
