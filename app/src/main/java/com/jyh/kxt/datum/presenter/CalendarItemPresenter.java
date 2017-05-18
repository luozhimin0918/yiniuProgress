package com.jyh.kxt.datum.presenter;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.datum.bean.CalendarBean;
import com.jyh.kxt.datum.bean.CalendarFinanceBean;
import com.jyh.kxt.datum.bean.CalendarHolidayBean;
import com.jyh.kxt.datum.bean.CalendarImportantBean;
import com.jyh.kxt.datum.bean.CalendarNotBean;
import com.jyh.kxt.datum.bean.CalendarTitleBean;
import com.jyh.kxt.datum.bean.CalendarType;
import com.jyh.kxt.datum.ui.fragment.CalendarFragment;
import com.jyh.kxt.datum.ui.fragment.CalendarItemFragment;
import com.library.base.http.HttpListener;
import com.library.base.http.VolleyRequest;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarItemPresenter extends BasePresenter {
    @BindObject CalendarItemFragment calendarItemFragment;


    public List<CalendarType> calendarTypeList = new ArrayList<>();
    private CalendarFragment parentFragment;
    public List<CalendarBean> calendarBeen;

    public CalendarItemPresenter(IBaseView iBaseView) {
        super(iBaseView);

        parentFragment = (CalendarFragment) ((CalendarItemFragment) iBaseView).getParentFragment();
    }


    public void requestPublishData() {

        VolleyRequest volleyRequest = new VolleyRequest(mContext, mQueue);

        JSONObject json = volleyRequest.getJsonParam();
        json.put("date", calendarItemFragment.calendarDate);
        volleyRequest.setTag(calendarItemFragment.calendarDate);

        volleyRequest.doGet(HttpConstant.RILI, json, new HttpListener<List<CalendarBean>>() {
            @Override
            protected void onResponse(List<CalendarBean> calendarBeen) {
                CalendarItemPresenter.this.calendarBeen = calendarBeen;

                calendarItemFragment.pllContent.loadOver();

                updateOrAddAdapter(0);

            }

            @Override
            protected void onErrorResponse(VolleyError error) {

                calendarItemFragment.pllContent.loadOver();
                generateFinanceListData(null);
                generateImportantListData(null);
                generateHolidayListData(null);
                calendarItemFragment.setCalendarAdapter(calendarTypeList);
            }
        });
    }

    public void updateOrAddAdapter(int status) {
        calendarTypeList.clear();

        for (CalendarBean calendarItemBean : calendarBeen) {
            String type = calendarItemBean.getType();
            List<String> objectList = calendarItemBean.getData();

            if ("finance".equals(type)) {//财经数据
                generateFinanceListData(objectList);
            } else if ("important".equals(type)) {//事件数据
                generateImportantListData(objectList);
            } else if ("holiday".equals(type)) { //假期数据
                generateHolidayListData(objectList);
            }
        }
        if (status == 0) {
            calendarItemFragment.setCalendarAdapter(calendarTypeList);
        } else {
            calendarItemFragment.calendarItemAdapter.notifyDataSetChanged();
        }
    }


    //生成经济数据数组
    private void generateFinanceListData(List<String> data) {
        CalendarTitleBean titleBean = new CalendarTitleBean();
        titleBean.setAdapterType(CalendarFragment.AdapterType.TITLE);
        titleBean.setName("数据");
        titleBean.setSpaceType(0);
        calendarTypeList.add(titleBean);

        if (data == null || data.size() == 0) {
            addNotData("暂无数据信息");
            return;
        }

        boolean isHaveComplacent = false;
        for (String objData : data) {
            CalendarFinanceBean mCalendarFinanceBean = JSONObject.parseObject(objData, CalendarFinanceBean.class);
            boolean isMeetConditions = parentFragment.isFinanceMeetConditions(mCalendarFinanceBean);

            if (isMeetConditions) {
                mCalendarFinanceBean.setAdapterType(CalendarFragment.AdapterType.CONTENT1);
                calendarTypeList.add(mCalendarFinanceBean);
                isHaveComplacent = true;
            }
        }

        if (!isHaveComplacent) {
            addNotData("暂无数据信息");
        }
    }

    //生成事件数据
    private void generateImportantListData(List<String> data) {
        CalendarTitleBean titleBean = new CalendarTitleBean();
        titleBean.setAdapterType(CalendarFragment.AdapterType.TITLE);
        titleBean.setName("事件");
        titleBean.setSpaceType(1);
        calendarTypeList.add(titleBean);

        if (data == null || data.size() == 0) {
            addNotData("暂无事件数据");
            return;
        }

        boolean isHaveComplacent = false;
        for (String objData : data) {
            CalendarImportantBean mCalendarImportantBean = JSONObject.parseObject(objData, CalendarImportantBean.class);
            boolean isMeetConditions = parentFragment.isImportantMeetConditions(mCalendarImportantBean);

            if (isMeetConditions) {
                mCalendarImportantBean.setAdapterType(CalendarFragment.AdapterType.CONTENT2);
                calendarTypeList.add(mCalendarImportantBean);
                isHaveComplacent = true;
            }
        }
        if (!isHaveComplacent) {
            addNotData("暂无事件数据");
        }
    }

    //生成假期数据数组
    private void generateHolidayListData(List<String> data) {
        CalendarTitleBean titleBean = new CalendarTitleBean();
        titleBean.setAdapterType(CalendarFragment.AdapterType.TITLE);
        titleBean.setName("假期");
        titleBean.setSpaceType(1);
        calendarTypeList.add(titleBean);

        if (data == null || data.size() == 0) {
            addNotData("暂无假期公告");
            return;
        }

        for (String objData : data) {
            CalendarHolidayBean mCalendarHolidayBean = JSONObject.parseObject(objData, CalendarHolidayBean.class);
            mCalendarHolidayBean.setAdapterType(CalendarFragment.AdapterType.CONTENT3);
            calendarTypeList.add(mCalendarHolidayBean);
        }
    }

    private void addNotData(String tip) {
        CalendarNotBean calendarNotBean = new CalendarNotBean();
        calendarNotBean.setDescribe(tip);
        calendarNotBean.setAdapterType(CalendarFragment.AdapterType.NO_DATA);
        calendarTypeList.add(calendarNotBean);
    }
}
