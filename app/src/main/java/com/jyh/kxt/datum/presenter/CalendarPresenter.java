package com.jyh.kxt.datum.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;

import com.jyh.kxt.base.BaseFragmentAdapter;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.library.widget.datetimepicker.DateTimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarPresenter extends BasePresenter {
    final int generateItemCount = 31;

    @BindObject CalendarFragment calendarFragment;

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

                SpannableString msp = new SpannableString(title);
                msp.setSpan(new RelativeSizeSpan(1.5f), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                msp.setSpan(new RelativeSizeSpan(0.8f), 3, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return msp;
            }
        };
        return baseFragmentAdapter;
    }
}
