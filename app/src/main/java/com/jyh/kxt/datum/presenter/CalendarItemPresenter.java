package com.jyh.kxt.datum.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.jyh.kxt.base.BasePresenter;
import com.jyh.kxt.base.IBaseView;
import com.jyh.kxt.base.annotation.BindObject;
import com.jyh.kxt.base.constant.HttpConstant;
import com.jyh.kxt.base.json.AdItemJson;
import com.jyh.kxt.base.json.AdTitleIconBean;
import com.jyh.kxt.base.json.AdTitleItemBean;
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

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/**
 * Created by Mr'Dai on 2017/4/11.
 */

public class CalendarItemPresenter extends BasePresenter {
    @BindObject CalendarItemFragment calendarItemFragment;


    private HashSet<String> hashSetCity = new HashSet<>();
    private HashSet<String> hashSetSelectedCity = new HashSet<>();

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

                try {
                    calendarItemFragment.pllContent.loadOver();
                    generateFinanceListData(null, null, null, 0);
                    generateImportantListData(null, 0);
                    generateHolidayListData(null, 0);
                    calendarItemFragment.setCalendarAdapter(calendarTypeList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * @param status 默认状态为0  选择规则之后刷新为1  重置所有不执行过滤为2
     */
    public void updateOrAddAdapter(int status) {
        try {
            calendarTypeList.clear();
            if (status == 0) {
                CalendarFragment parentFragment = (CalendarFragment) calendarItemFragment.getParentFragment();
                parentFragment.areaSet = null;
            }

            for (CalendarBean calendarItemBean : calendarBeen) {
                String type = calendarItemBean.getType();

                if ("finance".equals(type)) {//财经数据
                    try {
                        String dataJson = null;
                        List<AdTitleItemBean> ads = null;
                        List<String> objectList = null;
                        AdTitleIconBean iconBean = null;
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(calendarItemBean.getData());
                            JSONArray adsJson = jsonObject.optJSONArray("ad");
                            dataJson = jsonObject.optString("data");

                            ads = null;
                            objectList = null;
                            iconBean = JSON.parseObject(jsonObject.optString("icon"), AdTitleIconBean.class);
                            if (adsJson != null) {
                                ads = JSON.parseArray(adsJson.toString(), AdTitleItemBean.class);
                            }
                        } catch (JSONException e) {
                            dataJson = calendarItemBean.getData();
                        }
                        if (dataJson != null && !dataJson.equals("")) {
                            objectList = JSON.parseArray(dataJson, String.class);
                        }
                        generateFinanceListData(ads, iconBean, objectList, status);
                    } catch (Exception e) {
                        generateFinanceListData(null, null, null, 0);
                    }
                } else if ("important".equals(type)) {//事件数据
                    List<String> objectList = JSON.parseArray(calendarItemBean.getData(), String.class);
                    generateImportantListData(objectList, status);
                } else if ("holiday".equals(type)) { //假期数据
                    List<String> objectList = JSON.parseArray(calendarItemBean.getData(), String.class);
                    generateHolidayListData(objectList, status);
                }
            }

            if (status == 0) {
                calendarItemFragment.setCalendarAdapter(calendarTypeList);
                calendarItemFragment.addCityData(hashSetCity, hashSetSelectedCity);

            } else {
                calendarItemFragment.calendarItemAdapter.notifyDataSetChanged();
            }
            calendarItemFragment.ptrlvContent.onRefreshComplete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //生成经济数据数组
    private void generateFinanceListData(List<AdTitleItemBean> ads, AdTitleIconBean icon, List<String> data, int status) {
        CalendarTitleBean titleBean = new CalendarTitleBean();
        titleBean.setAdapterType(CalendarFragment.AdapterType.TITLE);
        titleBean.setName("数据");
        titleBean.setSpaceType(0);
        titleBean.setAds(ads);
        titleBean.setIcon(icon);
        titleBean.setShowAd(true);
        calendarTypeList.add(titleBean);

        if (data == null || data.size() == 0) {
            addNotData("暂无数据信息");
            return;
        }

        boolean isHaveComplacent = false;
        for (int i = 0; i < data.size(); i++) {
            String objData = data.get(i);
            CalendarFinanceBean mCalendarFinanceBean = JSONObject.parseObject(objData, CalendarFinanceBean.class);
            boolean isMeetConditions;
            if (status == 2) {
                isMeetConditions = true;
            } else {
                isMeetConditions = parentFragment.isFinanceMeetConditions(mCalendarFinanceBean);
            }

            if (status == 0) {
                hashSetCity.add(mCalendarFinanceBean.getState());
            }

            if (isMeetConditions) {
                mCalendarFinanceBean.setAdapterType(CalendarFragment.AdapterType.CONTENT1);
                calendarTypeList.add(mCalendarFinanceBean);
                isHaveComplacent = true;
            }

            if (i == data.size() - 1) {
                mCalendarFinanceBean.setShowLine(false);
            }
        }

        if (!isHaveComplacent) {
            addNotData("暂无数据信息");
        }
    }

    //生成事件数据
    private void generateImportantListData(List<String> data, int status) {
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
        for (int i = 0; i < data.size(); i++) {
            String objData = data.get(i);
            CalendarImportantBean mCalendarImportantBean = JSONObject.parseObject(objData, CalendarImportantBean.class);
            boolean isMeetConditions = /*parentFragment.isImportantMeetConditions(mCalendarImportantBean)*/true;
            if (status == 0) {
                hashSetCity.add(mCalendarImportantBean.getState());
            }

            if (isMeetConditions) {
                mCalendarImportantBean.setAdapterType(CalendarFragment.AdapterType.CONTENT2);
                calendarTypeList.add(mCalendarImportantBean);
                isHaveComplacent = true;
            }
            if (i == data.size() - 1) {
                mCalendarImportantBean.setShowLine(false);
            }
        }
        if (!isHaveComplacent) {
            addNotData("暂无事件数据");
        }
    }

    //生成假期数据数组

    private void generateHolidayListData(List<String> data, int status) {
        CalendarTitleBean titleBean = new CalendarTitleBean();
        titleBean.setAdapterType(CalendarFragment.AdapterType.TITLE);
        titleBean.setName("假期");
        titleBean.setSpaceType(1);
        calendarTypeList.add(titleBean);

        if (data == null || data.size() == 0) {
            addNotData("暂无假期公告");
            return;
        }

        for (int i = 0; i < data.size(); i++) {
            String objData = data.get(i);
            CalendarHolidayBean mCalendarHolidayBean = JSONObject.parseObject(objData, CalendarHolidayBean.class);
            mCalendarHolidayBean.setAdapterType(CalendarFragment.AdapterType.CONTENT3);
            calendarTypeList.add(mCalendarHolidayBean);
            if (i == data.size() - 1) {
                mCalendarHolidayBean.setShowLine(false);
            }
        }
    }

    private void addNotData(String tip) {
        CalendarNotBean calendarNotBean = new CalendarNotBean();
        calendarNotBean.setDescribe(tip);
        calendarNotBean.setAdapterType(CalendarFragment.AdapterType.NO_DATA);
        calendarTypeList.add(calendarNotBean);
    }
}
