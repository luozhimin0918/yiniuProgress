package com.jyh.kxt.push.json;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 推送实体类1
 */
public class PushBean1 extends PushBean implements Parcelable {


	/**
	 * autoid : 134112
	 * time : 1477271025
	 * code : KUAIXUN
	 * id : 4193
	 * content :
	 * status : 1
	 */

	private String content;
	private String status;
	private String socre;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSocre() {
		return socre;
	}

	public void setSocre(String socre) {
		this.socre = socre;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.content);
		dest.writeString(this.status);
		dest.writeString(this.socre);
	}

	public PushBean1() {
	}

	protected PushBean1(Parcel in) {
		this.content = in.readString();
		this.status = in.readString();
		this.socre = in.readString();
	}

	public static final Parcelable.Creator<PushBean1> CREATOR = new Parcelable.Creator<PushBean1>() {
		@Override
		public PushBean1 createFromParcel(Parcel source) {
			return new PushBean1(source);
		}

		@Override
		public PushBean1[] newArray(int size) {
			return new PushBean1[size];
		}
	};
}