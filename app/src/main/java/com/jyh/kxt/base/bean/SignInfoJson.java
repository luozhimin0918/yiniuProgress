package com.jyh.kxt.base.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mr'Dai on 2017/9/14.
 */

public class SignInfoJson {
    private String uid;
    private int sign_state;  //是否签到
    private int task_state; //任务是否完成
    private long acquireTime;

    public int getSign_state() {
        return sign_state;
    }

    public void setSign_state(int sign_state) {
        this.sign_state = sign_state;
    }

    public int getTask_state() {
        return task_state;
    }

    public void setTask_state(int task_state) {
        this.task_state = task_state;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getAcquireTime() {
        return acquireTime;
    }

    /**
     * 系统时间
     */
    public void setAcquireTime() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String formatYMD = sdf.format(new Date());
            Date parseYMD = sdf.parse(formatYMD);
            this.acquireTime = parseYMD.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public SignInfoJson(String uid, int sign_state, int task_state) {
        this.uid = uid;
        this.sign_state = sign_state;
        this.task_state = task_state;
        setAcquireTime();
    }
}
