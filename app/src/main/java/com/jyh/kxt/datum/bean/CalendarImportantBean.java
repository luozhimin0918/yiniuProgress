package com.jyh.kxt.datum.bean;

/**
 * Created by Mr'Dai on 2017/4/21.
 * important财经大事格式
 */

public class CalendarImportantBean extends CalendarType{

    /**
     * state :
     * time :
     * importance :
     * title :
     */

    private String state;
    private String time;
    private String importance;
    private String title;
    private boolean isShowLine = true;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShowLine() {
        return isShowLine;
    }

    public void setShowLine(boolean showLine) {
        isShowLine = showLine;
    }
}
