package com.jyh.kxt.datum.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;

import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.library.widget.datetimepicker.DateTimeUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarPresenter extends BasePresenter {
    final public int generateItemCount = 31;

    @BindObject CalendarFragment calendarFragment;

    private TextView oldSelectTabView;
    public List<Long> dataLongList = new ArrayList<>();    //日期Long类型
    public String[] navTitleList = new String[generateItemCount];  //导航栏Title

    public CalendarPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateDateItem(long selectTimeMillis) {


        long oneDayLong = 1000 * 60 * 60 * 24;

        for (int i = (generateItemCount - 1) / 2; i >= 1; i--) {
            long time = selectTimeMillis - oneDayLong * i;
            dataLongList.add(time);
        }

        dataLongList.add(selectTimeMillis);//当前日期

        for (int i = 1; i <= (generateItemCount - 1) / 2; i++) {
            long time = selectTimeMillis + oneDayLong * i;
            dataLongList.add(time);
        }

        Calendar mCalendar = Calendar.getInstance();
        for (int i = 0; i < dataLongList.size(); i++) {
            String day = (String) DateFormat.format("dd", dataLongList.get(i));

            mCalendar.setTimeInMillis(dataLongList.get(i));
            String dayOfWeek = DateTimeUtil.getWeekdays(mCalendar.get(Calendar.DAY_OF_WEEK));

            navTitleList[i] = day + "\n" + dayOfWeek;
        }
    }

    public BaseFragmentAdapter getPageAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        BaseFragmentAdapter baseFragmentAdapter = new BaseFragmentAdapter(fm, fragmentList) {
            @Override
            public CharSequence getPageTitle(int position) {
                String title = navTitleList[position];
                return getColorCharSequence(title, false);
            }
        };
        return baseFragmentAdapter;
    }

    private SpannableString getColorCharSequence(String title, boolean isSelect) {
        SpannableString msp = new SpannableString(title);

        int dayColor, weekColor;
        if (!isSelect) {
            dayColor = ContextCompat.getColor(mContext, R.color.font_color3);
            weekColor = ContextCompat.getColor(mContext, R.color.font_color6);
        } else {
            dayColor = ContextCompat.getColor(mContext, R.color.font_color8);
            weekColor = ContextCompat.getColor(mContext, R.color.font_color8);
        }

        msp.setSpan(new ForegroundColorSpan(dayColor), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new RelativeSizeSpan(2f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(weekColor), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return msp;
    }

    public void updateSelectedColor(int position) {
        SlidingTabLayout stlNavigationBar = calendarFragment.stlNavigationBar;
        TextView tvTitle = stlNavigationBar.getTitleView(position);
        String title = tvTitle.getText().toString();

        if (oldSelectTabView != null) {
            String oldSelectTitle = oldSelectTabView.getText().toString();
            SpannableString colorCharSequence = getColorCharSequence(oldSelectTitle, false);
            oldSelectTabView.setText(colorCharSequence);
        }

        SpannableString colorCharSequence = getColorCharSequence(title, true);
        tvTitle.setText(colorCharSequence);
        oldSelectTabView = tvTitle;
    }
}
