package com.jyh.kxt.datum.bean;

/**
 * Created by Mr'Dai on 2017/4/21.
 * 经济数据
 */

public class CalendarFinanceBean  extends CalendarType{

    /**
     * code :
     * importance : 高
     * time :
     * title :
     * reality :
     * forecast :
     * before :
     * state :
     * effect :
     * effecttype : 1
     */

    private String code;
    private String importance;
    private String time;
    private String title;
    private String reality;
    private String forecast;
    private String before;
    private String state;
    private String effect;
    private int effecttype;

    private boolean isShowLine = true;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReality() {
        return reality;
    }

    public void setReality(String reality) {
        this.reality = reality;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getEffecttype() {
        return effecttype;
    }

    public void setEffecttype(int effecttype) {
        this.effecttype = effecttype;
    }

    public boolean isShowLine() {
        return isShowLine;
    }

    public void setShowLine(boolean showLine) {
        isShowLine = showLine;
    }
}
