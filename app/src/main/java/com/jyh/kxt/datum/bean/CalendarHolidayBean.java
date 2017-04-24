package com.jyh.kxt.datum.bean;

/**
 * Created by Mr'Dai on 2017/4/21.
 * holiday假期预告格式
 */

public class CalendarHolidayBean  extends CalendarType{

    /**
     * state :
     * time :
     * site :
     * title :
     */

    private String state;
    private String time;
    private String site;
    private String title;

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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
