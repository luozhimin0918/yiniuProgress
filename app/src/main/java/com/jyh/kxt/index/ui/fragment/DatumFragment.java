package com.jyh.kxt.index.ui.fragment;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jyh.kxt.R;
import com.jyh.kxt.base.BaseFragment;
import com.jyh.kxt.base.custom.RoundImageView;
import com.jyh.kxt.base.impl.OnRequestPermissions;
import com.jyh.kxt.base.util.PopupUtil;
import com.jyh.kxt.base.utils.LoginUtils;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.DataFragment;
import com.jyh.kxt.index.json.AlarmJson;
import com.jyh.kxt.index.presenter.AlarmPresenter;
import com.jyh.kxt.index.presenter.DatumPresenter;
import com.jyh.kxt.index.ui.MainActivity;
import com.jyh.kxt.user.json.UserJson;
import com.library.base.LibActivity;
import com.library.bean.EventBusClass;
import com.library.util.ObserverCall;
import com.library.util.SystemUtil;
import com.library.widget.pickerview.OptionsPickerView;
import com.library.widget.tablayout.SegmentTabLayout;
import com.library.widget.tablayout.listener.OnTabSelectListener;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;

import static com.umeng.message.proguard.k.A;

/**
 * 首页-数据
 */
public class DatumFragment extends BaseFragment implements OnTabSelectListener {

    @BindView(R.id.stl_navigation_bar) SegmentTabLayout stlNavigationBar;

    @BindView(R.id.iv_left_icon) RoundImageView ivLeftIcon;
    @BindView(R.id.iv_right_icon2) ImageView ivCalendar;
    @BindView(R.id.iv_right_icon1) ImageView ivFiltrate;
    @BindView(R.id.fl_root_content) FrameLayout flRootContent;

    private DatumPresenter datumPresenter;
    public PopupUtil filtratePopup;

    public BaseFragment dataFragment, calendarFragment;
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

        datumPresenter = new DatumPresenter(this);
        datumPresenter.requestAreaInfo(false);

        ivCalendar.setVisibility(View.VISIBLE);
        ivFiltrate.setImageResource(R.mipmap.icon_rili_sx);

        String[] mTitles = getResources().getStringArray(R.array.nav_datum);
        stlNavigationBar.setTabData(mTitles);
        stlNavigationBar.setOnTabSelectListener(this);

        onTabSelect(0);

        AlarmPresenter.getInstance().initAlarmList(getContext());

        changeUserImg(LoginUtils.getUserInfo(getContext()));
    }

    @OnClick({R.id.iv_right_icon1, R.id.iv_right_icon2,R.id.iv_left_icon})
    public void onNavClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left_icon:
                ((MainActivity) getActivity()).showUserCenter();
                break;
            case R.id.iv_right_icon1:
                if (filtratePopup == null) {

                    DisplayMetrics screenDisplay = SystemUtil.getScreenDisplay(getContext());
                    int popHeight = screenDisplay.heightPixels - SystemUtil.dp2px(getContext(), 115);

                    filtratePopup = new PopupUtil(getActivity());
                    View filtrateView = filtratePopup.createPopupView(R.layout.pop_calendar_filtrate);
                    datumPresenter.registerFiltrateAgency(filtrateView);

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
                } else {
                    filtratePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                }
                break;
            case R.id.iv_right_icon2:
                datumPresenter.openCalendar();
                break;
        }

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

    public void gotoCorrespondItem(long timeInMillis) {
        if (calendarFragment != null) {
            ((CalendarFragment) calendarFragment).gotoCorrespondItem(timeInMillis);
        }
    }

    public void showTimingWindow(String time, final ObserverCall<Integer> observerCall) {
        final ArrayList<String> timeList = new ArrayList<>();
        timeList.add("提前5分钟");
        timeList.add("提前10分钟");
        timeList.add("提前15分钟");
        timeList.add("提前30分钟");
        timeList.add("提前一个小时");
        OptionsPickerView timePickerView = new OptionsPickerView
                .Builder(getContext(),
                new OptionsPickerView.OnOptionsSelectListener() {
                    @Override
                    public void onOptionsSelect(int options1, int options2, int options3, View v) {
                        observerCall.onComplete(options1);
                    }
                })
                .setDividerColor(ContextCompat.getColor(getContext(), R.color.line_color3))
                .setTextColorCenter(ContextCompat.getColor(getContext(), R.color.font_color5)) //设置选中项文字颜色
                .setSelectOptions(0)
                .setContentTextSize(25)
                .setDecorView(flRootContent)
                .build();
        timePickerView.setPicker(timeList);
        timePickerView.show();
    }


    public CalendarFragment getCalendarFragment() {
        return (CalendarFragment) calendarFragment;
    }

    /*
             * 获取闹钟权限成功
             */
    private Uri calenderURL;
    private Uri calenderEventURL;
    private Uri calenderReminderURL;

    public void obtainAlarmPermissionsSuccess(long baseTime,
                                              CalendarFinanceBean calendarFinanceBean,
                                              OnRequestPermissions onRequestPermissions) {

        calenderURL = CalendarContract.Calendars.CONTENT_URI/*"content://com.android.calendar/calendars"*/;
        calenderEventURL = CalendarContract.Events.CONTENT_URI/* "content://com.android.calendar/events"*/;
        calenderReminderURL = CalendarContract.Reminders.CONTENT_URI/*"content://com.android.calendar/reminders"*/;


        int updateEventId = -1;
        //首先查询是替换还是增加
        Cursor existCursor = getActivity().getContentResolver().query(calenderEventURL, null, null, null, null);
        existCursor.getCount();
        if (existCursor != null) {
            while (existCursor.moveToNext()) {
                try {
                    String eventTitle = existCursor.getString(existCursor.getColumnIndex(CalendarContract.Events
                            .TITLE));
                    if (calendarFinanceBean.getTitle().equals(eventTitle)) {
                        updateEventId = existCursor.getInt(existCursor.getColumnIndex("_id"));
                    }
                } catch (Exception e) {
                }
            }
            existCursor.close();
        }


        Cursor userCursor = null;
        Cursor queryCursor = null;
        try {
            /**
             * 创建日历的ID值
             */
            String calendarId;

            userCursor = getActivity().getContentResolver().query(calenderURL,
                    null,
                    null,
                    null,
                    null);

            if (null != userCursor && userCursor.getCount() > 0) {
                userCursor.moveToFirst();
                calendarId = userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                calendarId = initCalendarAccount();
            }

            ContentValues event = new ContentValues();
            event.put(CalendarContract.Events.TITLE, calendarFinanceBean.getTitle());
            event.put(CalendarContract.Events.DESCRIPTION, calendarFinanceBean.getTitle());
            event.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            event.put(CalendarContract.Events.DTSTART, baseTime);//起始时间
            event.put(CalendarContract.Events.DTEND, baseTime); //截止时间
            event.put(CalendarContract.Events.HAS_ALARM, 1); //控制是否事件触发报警，提醒如下

            try {
                event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            } catch (Exception e) {
            }

            //查询是否插入成功
            boolean isInsertSuccess = false;

            ContentResolver contentResolver = getActivity().getContentResolver(); // 为刚才新添加的event添加reminder
            ContentValues values = new ContentValues();


            if (updateEventId == -1) {

                Uri insert = contentResolver.insert(calenderEventURL, event);
                long id = Long.parseLong(insert.getLastPathSegment());

                values.put(CalendarContract.Reminders.MINUTES, 0); //系统的提前10分钟有提醒
                values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                values.put(CalendarContract.Reminders.EVENT_ID, id);

                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, values); // 调用这个方法返回值是一个Uri

                queryCursor = getActivity().getContentResolver().query(calenderEventURL, null, null, null, null);
                if (queryCursor.moveToFirst()) {

                    String eventId = queryCursor.getString(queryCursor.getColumnIndex("_id"));

                    if (!TextUtils.isEmpty(eventId)) {
                        isInsertSuccess = true;
                    }
                }


            } else {
                isInsertSuccess = true;
                contentResolver.update(calenderEventURL, event,
                        CalendarContract
                                .Events._ID + "=" + updateEventId, null);
            }
            if (isInsertSuccess) {
                AlarmJson mAlarmJson = new AlarmJson();
                mAlarmJson.setCode(calendarFinanceBean.getCode());
                mAlarmJson.setTime(String.valueOf(baseTime));

                AlarmPresenter.getInstance().addAlarmItem(getContext(), mAlarmJson);
            } else {
                throw new NullPointerException();
            }

            TSnackbar tSnackbar = TSnackbar.make(flRootContent,
                    "设置成功",
                    Snackbar.LENGTH_SHORT,
                    TSnackbar.APPEAR_FROM_TOP_TO_DOWN)
                    .setMinHeight(SystemUtil.getStatuBarHeight(getContext()), getResources()
                            .getDimensionPixelOffset(R.dimen.actionbar_height));

            int color = ContextCompat.getColor(getContext(), R.color.red_btn_bg_color);
            tSnackbar.setBackgroundColor(color);
            tSnackbar.setPromptThemBackground(Prompt.WARNING);
            tSnackbar.show();


            onRequestPermissions.doSomething();
        } catch (Exception e) {
            e.printStackTrace();
            obtainAlarmPermissionsFailure();
        } finally {

            if (userCursor != null) {
                userCursor.close();
            }

            if (queryCursor != null) {
                queryCursor.close();
            }
        }
    }

    /**
     * 获取闹钟权限失败
     */
    public void obtainAlarmPermissionsFailure() {
        new AlertDialog.Builder(getActivity()).setTitle("提示")
                .setMessage("需要开启日历的通知和提醒后定时提醒才能生效，是否前往设置？")
                .setPositiveButton("前往",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                                startActivity(intent);
                            }
                        }).setNegativeButton("好", null).show();
    }

    /**
     * 如果不存在帐号则创建
     *
     * @return
     */
    private String initCalendarAccount() {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, "快讯通");
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, "快讯通");
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "快讯通");
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, -9206951);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, "mygmailaddress@gmail.com");
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = CalendarContract.Calendars.CONTENT_URI;
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "快讯通")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        Uri insertCalendarAccount = getActivity().getContentResolver().insert(calendarUri, value);

        Cursor userCursor = getActivity().getContentResolver().query(insertCalendarAccount,
                null,
                null,
                null,
                null);
        if (null != userCursor && userCursor.getCount() > 0) {
            userCursor.moveToFirst();
            String calendarId = userCursor.getString(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            userCursor.close();
            return calendarId;
        }
        userCursor.close();
        return null;
    }

    private void changeUserImg(UserJson user) {
        if (user == null) {
            ivLeftIcon.setImageResource(R.mipmap.icon_user_def_photo);
        } else {
            Glide.with(getContext()).load(user.getPicture()).asBitmap().error(R.mipmap.icon_user_def_photo).placeholder(R.mipmap
                    .icon_user_def_photo).into(ivLeftIcon);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBusClass eventBus) {
        switch (eventBus.fromCode) {
            case EventBusClass.EVENT_LOGIN:
                UserJson userJson = (UserJson) eventBus.intentObj;
                changeUserImg(userJson);
                break;
            case EventBusClass.EVENT_LOGOUT:
                changeUserImg(null);
                break;
            case EventBusClass.EVENT_CHANGEUSERINFO:
                changeUserImg((UserJson) eventBus.intentObj);
                break;
        }
    }
}
