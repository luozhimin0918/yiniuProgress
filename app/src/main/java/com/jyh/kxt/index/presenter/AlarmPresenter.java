package com.jyh.kxt.index.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.jyh.kxt.base.constant.SpConstant;
import com.library.util.JsonUtil;
import com.jyh.kxt.index.json.AlarmJson;
import com.library.util.SPUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mr'Dai on 2016/12/27.
 * 闹钟提醒数据
 */
public class AlarmPresenter {
    public static AlarmPresenter alarmPresenter = new AlarmPresenter();

    public static AlarmPresenter getInstance() {
        return alarmPresenter;
    }


    private HashMap<String, AlarmJson> alarmHashMap = new HashMap<>();

    public void initAlarmList(Context mContext) {
        String calendarAlarm = SPUtils.getString(mContext, SpConstant.CALENDAR_ALARM_LIST);

        if (!"".equals(calendarAlarm)) {
            List<AlarmJson> alarmList = JsonUtil.parseArray(calendarAlarm, AlarmJson.class);

            for (AlarmJson alarmJson : alarmList) {
                alarmHashMap.put(alarmJson.getCode(), alarmJson);
            }
        }
    }

    /**
     * false 表示不存在
     * true 表示已经设定了
     *
     * @param code
     * @return
     */
    public AlarmJson getAlarmItem(String code) {
        return alarmHashMap.get(code);
    }

    public void addAlarmItem(Context mContext, AlarmJson alarmJson) {

        AlarmJson oldAlarmJson = getAlarmItem(alarmJson.getCode());
        if (oldAlarmJson != null) {
            oldAlarmJson.setTime(alarmJson.getTime());
        } else {
            alarmHashMap.put(alarmJson.getCode(), alarmJson);
        }
        String calendarAlarm = SPUtils.getString(mContext, SpConstant.CALENDAR_ALARM_LIST);

        List<AlarmJson> alarmList;
        if (!"".equals(calendarAlarm)) {
            alarmList = JsonUtil.parseArray(calendarAlarm, AlarmJson.class);
        } else {
            alarmList = new ArrayList<>();
        }


        if (oldAlarmJson != null) {
            for (AlarmJson itemAlarmJson : alarmList) {
                if (itemAlarmJson.getCode().equals(oldAlarmJson.getCode())) {
                    itemAlarmJson.setTime(oldAlarmJson.getTime());
                }
            }
        } else {
            alarmList.add(alarmJson);
        }

        SPUtils.save(mContext, SpConstant.CALENDAR_ALARM_LIST, JSON.toJSONString(alarmList));
    }
}
