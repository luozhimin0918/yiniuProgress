package com.jyh.kxt.datum.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
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
import com.jyh.kxt.index.ui.fragment.DatumFragment;
import com.library.util.SystemUtil;
import com.library.widget.datetimepicker.DateTimeUtil;
import com.library.widget.tablayout.SlidingTabLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarPresenter extends BasePresenter {
    final public int generateItemCount = 31;

    @BindObject CalendarFragment calendarFragment;

    private String currentMonth;
    private TextView oldSelectTabView;

    public List<Long> dataLongList = new ArrayList<>();    //日期Long类型
    public String[] navTitleList = new String[generateItemCount];  //导航栏Title

    public CalendarPresenter(IBaseView iBaseView) {
        super(iBaseView);
    }

    public void generateDateItem(long selectTimeMillis) {
        dataLongList.clear();

        //保证只有年月日
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date(selectTimeMillis);
            String format = simpleDateFormat.format(date);
            selectTimeMillis = simpleDateFormat.parse(format).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

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

        //改变右上角日期
        String month = (String) DateFormat.format("MM", dataLongList.get(position));
        if (!month.equals(currentMonth)) {
            Bitmap txtBitmap = generateMonthBitmap(month);
            DatumFragment datumFragment = (DatumFragment) calendarFragment.getParentFragment();
            datumFragment.setMonthToImageView(txtBitmap);
        }
        currentMonth = month;
    }

    private Bitmap generateMonthBitmap(String month) {
        Bitmap rqBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.icon_rili_rq);
        android.graphics.Bitmap.Config bitmapConfig = rqBitmap.getConfig();
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        int txtSize = SystemUtil.dp2px(mContext, 11);

        int bitmapColor = mContext.getResources().getColor(R.color.font_color5);
        int imageViewSize = mContext.getResources().getDimensionPixelSize(R.dimen.navigationBarRightImageViewSize);

        Bitmap txtBitmap = rqBitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(txtBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bitmapColor);
        paint.setTextSize(txtSize);

        Rect bounds = new Rect();
        paint.getTextBounds(month, 0, month.length(), bounds);

        //犹豫图片问题,Y得+10
        canvas.drawText(month, imageViewSize / 2 - bounds.centerX(), imageViewSize / 2 - bounds.centerY() + 5, paint);
        return txtBitmap;
    }
}
