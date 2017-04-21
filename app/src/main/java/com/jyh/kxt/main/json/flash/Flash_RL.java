package com.jyh.kxt.main.json.flash;

/**
 * 项目名:Kxt
 * 类描述:快讯-要闻
 * 创建人:苟蒙蒙
 * 创建日期:2017/4/21.
 */

public class Flash_RL {

    private String title;//3月17日当周石油钻井总数,
    private String predicttime;//2017-03-18 01:00:00,
    private String time;//2017-03-18 01:00:10,
    private String state;//美国,
    private String before;//617,
    private String forecast;//617,
    private String reality;//631,
    private String importance;//高,
    private String effect;//美元|金银 石油|,
    private String effecttype;//1,
    private String autoid;//274040

    @Override
    public String toString() {
        return "Flash_RL{" +
                "title='" + title + '\'' +
                ", predicttime='" + predicttime + '\'' +
                ", time='" + time + '\'' +
                ", state='" + state + '\'' +
                ", before='" + before + '\'' +
                ", forecast='" + forecast + '\'' +
                ", reality='" + reality + '\'' +
                ", importance='" + importance + '\'' +
                ", effect='" + effect + '\'' +
                ", effecttype='" + effecttype + '\'' +
                ", autoid='" + autoid + '\'' +
                '}';
    }

    public Flash_RL() {
    }

    public Flash_RL(String title, String predicttime, String time, String state, String before, String forecast, String reality, String
            importance, String effect, String effecttype, String autoid) {

        this.title = title;
        this.predicttime = predicttime;
        this.time = time;
        this.state = state;
        this.before = before;
        this.forecast = forecast;
        this.reality = reality;
        this.importance = importance;
        this.effect = effect;
        this.effecttype = effecttype;
        this.autoid = autoid;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPredicttime() {
        return predicttime;
    }

    public void setPredicttime(String predicttime) {
        this.predicttime = predicttime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public String getReality() {
        return reality;
    }

    public void setReality(String reality) {
        this.reality = reality;
    }

    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getEffecttype() {
        return effecttype;
    }

    public void setEffecttype(String effecttype) {
        this.effecttype = effecttype;
    }

    public String getAutoid() {
        return autoid;
    }

    public void setAutoid(String autoid) {
        this.autoid = autoid;
    }
}
