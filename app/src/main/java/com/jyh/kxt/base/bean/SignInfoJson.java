package com.jyh.kxt.base.bean;

/**
 * Created by Mr'Dai on 2017/9/14.
 */

public class SignInfoJson {
    private String uid;
    private int sign_state;
    private int task_state;
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

    public void setAcquireTime(long acquireTime) {
        this.acquireTime = acquireTime;
    }
}
