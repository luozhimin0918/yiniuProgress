package com.jyh.kxt.push.json;

import java.io.Serializable;

/** 
 *  */
public class KxItemCJRL implements Serializable {


	/**
	 * title : 10月CBI服务业乐观信心指数
	 * predicttime : 2016-10-24 18:00:00
	 * state : 英国
	 * before : -47
	 * forecast : -47
	 * reality : -8
	 * importance : 低
	 * effect : 金银 英镑 石油||
	 */

	private String title;
	private String predicttime;
	private String state;
	private String before;
	private String forecast;
	private String reality;
	private String importance;
	private String effect;
	private String time;

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

	public String getPredicttime() {
		return predicttime;
	}

	public void setPredicttime(String predicttime) {
		this.predicttime = predicttime;
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
}