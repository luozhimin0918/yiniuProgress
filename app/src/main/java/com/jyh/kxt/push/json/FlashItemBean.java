package com.jyh.kxt.push.json;

import java.io.Serializable;

/** 
 *  */
public class FlashItemBean implements Serializable {


	/**
	 * title : 日本央行行长黑田东彦：哪怕长期收益率上升，也没必要下调
	 * time : 2016-10-27 09:53:35
	 * importance : 低
	 */

	private String title;
	private String time;
	private String importance;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
}